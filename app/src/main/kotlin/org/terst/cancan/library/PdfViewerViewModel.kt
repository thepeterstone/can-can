package org.terst.cancan.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.terst.cancan.core.CanCanLogger
import java.io.File
import javax.inject.Inject

data class PdfViewerUiState(
    val document: LibraryDocument? = null,
    val pageCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class PdfViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: LibraryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    private var pdfRenderer: PdfRenderer? = null
    private var pfd: ParcelFileDescriptor? = null
    private val renderMutex = Mutex()

    init {
        val documentId = savedStateHandle.get<String>("documentId") ?: ""
        val document = repository.documents.find { it.id == documentId }
        if (document != null) {
            _uiState.update { it.copy(document = document) }
            viewModelScope.launch(Dispatchers.IO) { openDocument(document) }
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Document not found: $documentId") }
            CanCanLogger.log("PdfViewerViewModel: unknown documentId=$documentId")
        }
    }

    private suspend fun openDocument(document: LibraryDocument) {
        try {
            val cacheFile = File(context.cacheDir, "library/${document.id}.pdf")
            if (!cacheFile.exists()) {
                cacheFile.parentFile?.mkdirs()
                context.assets.open(document.assetPath).use { input ->
                    cacheFile.outputStream().use { output -> input.copyTo(output) }
                }
            }
            pfd = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(pfd!!)
            val pageCount = pdfRenderer!!.pageCount
            _uiState.update { it.copy(pageCount = pageCount, isLoading = false) }
            CanCanLogger.log("PdfViewerViewModel: opened ${document.id} — $pageCount pages")
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to open PDF") }
            CanCanLogger.log("PdfViewerViewModel: failed to open ${document.id} — ${e.message}")
        }
    }

    suspend fun renderPage(pageIndex: Int, widthPx: Int): Bitmap = withContext(Dispatchers.IO) {
        renderMutex.withLock {
            val renderer = pdfRenderer
                ?: return@withContext Bitmap.createBitmap(widthPx, widthPx, Bitmap.Config.ARGB_8888)
            val page = renderer.openPage(pageIndex)
            val height = (widthPx * page.height / page.width.toFloat()).toInt().coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(widthPx, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmap
        }
    }

    override fun onCleared() {
        super.onCleared()
        pdfRenderer?.close()
        pfd?.close()
    }
}

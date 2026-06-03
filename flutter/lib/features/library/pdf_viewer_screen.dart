import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:syncfusion_flutter_pdfviewer/pdfviewer.dart';
import 'library_notifier.dart';

class PdfViewerScreen extends ConsumerWidget {
  final String documentId;
  const PdfViewerScreen({super.key, required this.documentId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final repo = ref.read(libraryRepositoryProvider);
    final doc = repo.getById(documentId);

    if (doc == null) {
      return Scaffold(
        appBar: AppBar(),
        body: const Center(child: Text('Document not found.')),
      );
    }

    return Scaffold(
      appBar: AppBar(title: Text(doc.title)),
      body: SfPdfViewer.asset(doc.assetPath),
    );
  }
}

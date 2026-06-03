import '../models/library_document.dart';

class LibraryRepository {
  // PDF assets must be downloaded separately (run scripts/download_reference_pdfs.py)
  // and placed in flutter/assets/library/. This list mirrors the Android hardcoded list.
  static const _documents = <LibraryDocument>[
    LibraryDocument(
      id: 'usda_complete_guide',
      title: 'USDA Complete Guide to Home Canning',
      category: 'Canning',
      description:
          'The definitive reference for safe home canning from the USDA National Center for Home Food Preservation.',
      assetPath: 'assets/library/usda_complete_guide.pdf',
    ),
    LibraryDocument(
      id: 'ball_blue_book',
      title: 'Ball Blue Book Guide to Preserving',
      category: 'Canning',
      description:
          'Classic home preserving guide covering canning, pickling, freezing, and more.',
      assetPath: 'assets/library/ball_blue_book.pdf',
    ),
    LibraryDocument(
      id: 'lds_food_storage',
      title: 'LDS Food Storage Guide',
      category: 'Food Storage',
      description:
          'Comprehensive food storage and preservation guide for long-term emergency preparedness.',
      assetPath: 'assets/library/lds_food_storage.pdf',
    ),
  ];

  List<LibraryDocument> getAll() => _documents;

  LibraryDocument? getById(String id) {
    try {
      return _documents.firstWhere((d) => d.id == id);
    } catch (_) {
      return null;
    }
  }
}

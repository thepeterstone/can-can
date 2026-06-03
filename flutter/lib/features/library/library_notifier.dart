import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/library_document.dart';
import '../../data/repositories/library_repository.dart';

final libraryRepositoryProvider = Provider((_) => LibraryRepository());

final libraryProvider = Provider<List<LibraryDocument>>((ref) {
  return ref.read(libraryRepositoryProvider).getAll();
});

final libraryCategoryProvider = StateProvider<String?>((_) => null);

final filteredLibraryProvider = Provider<List<LibraryDocument>>((ref) {
  final all = ref.watch(libraryProvider);
  final category = ref.watch(libraryCategoryProvider);
  if (category == null) return all;
  return all.where((d) => d.category == category).toList();
});

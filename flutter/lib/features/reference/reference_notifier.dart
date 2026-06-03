import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/reference_item.dart';
import '../../data/repositories/reference_repository.dart';

final referenceRepositoryProvider = Provider((_) => ReferenceRepository());

final referenceProvider = FutureProvider<List<ReferenceItem>>((ref) {
  return ref.read(referenceRepositoryProvider).getAll();
});

class ReferenceFilter {
  final String query;
  final String? category;

  const ReferenceFilter({this.query = '', this.category});

  ReferenceFilter copyWith({String? query, Object? category = _sentinel}) =>
      ReferenceFilter(
        query: query ?? this.query,
        category: category == _sentinel ? this.category : category as String?,
      );
}

const Object _sentinel = Object();

final referenceFilterProvider =
    StateProvider<ReferenceFilter>((_) => const ReferenceFilter());

final filteredReferenceProvider =
    Provider<AsyncValue<List<ReferenceItem>>>((ref) {
  final all = ref.watch(referenceProvider);
  final filter = ref.watch(referenceFilterProvider);
  return all.whenData((items) {
    var result = items;
    if (filter.category != null) {
      result = result.where((r) => r.category == filter.category).toList();
    }
    if (filter.query.isNotEmpty) {
      final q = filter.query.toLowerCase();
      result = result.where((r) => r.name.toLowerCase().contains(q)).toList();
    }
    return result;
  });
});

const referenceCategories = [
  'Tomatoes',
  'Vegetables',
  'Fruits',
  'Jams',
  'Pickles',
  'Meats',
  'Fermentation',
  'Foraging',
  'Hawaii Foraging',
  'Hawaii Fishing',
  'Dehydrating',
  'Smoking & Curing',
];

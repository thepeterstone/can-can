import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/recipe_item.dart';
import '../../data/repositories/recipe_repository.dart';

final recipeRepositoryProvider = Provider((_) => RecipeRepository());

final recipesProvider = FutureProvider<List<RecipeItem>>((ref) {
  return ref.read(recipeRepositoryProvider).getAll();
});

class RecipesFilter {
  final String query;
  final String? category;

  const RecipesFilter({this.query = '', this.category});

  RecipesFilter copyWith({String? query, Object? category = _sentinel}) =>
      RecipesFilter(
        query: query ?? this.query,
        category: category == _sentinel ? this.category : category as String?,
      );
}

const Object _sentinel = Object();

final recipesFilterProvider =
    StateProvider<RecipesFilter>((_) => const RecipesFilter());

final filteredRecipesProvider = Provider<AsyncValue<List<RecipeItem>>>((ref) {
  final all = ref.watch(recipesProvider);
  final filter = ref.watch(recipesFilterProvider);
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

const recipeCategories = [
  'Water Bath Canning',
  'Pressure Canning',
  'Fermentation',
  'Dehydrating',
  'Smoking & Curing',
];

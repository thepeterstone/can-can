import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/database/app_database.dart';
import '../../data/models/inventory_item.dart';
import '../../data/repositories/inventory_repository.dart';

final appDatabaseProvider = Provider((_) => AppDatabase());

final inventoryRepositoryProvider = Provider((ref) {
  return InventoryRepository(ref.read(appDatabaseProvider));
});

final inventoryProvider = StreamProvider<List<InventoryItem>>((ref) {
  return ref.watch(inventoryRepositoryProvider).watchAll();
});

class InventoryFilter {
  final String query;
  final String? category;

  const InventoryFilter({this.query = '', this.category});

  InventoryFilter copyWith({String? query, Object? category = _sentinel}) =>
      InventoryFilter(
        query: query ?? this.query,
        category: category == _sentinel ? this.category : category as String?,
      );
}

const Object _sentinel = Object();

final inventoryFilterProvider =
    StateProvider<InventoryFilter>((_) => const InventoryFilter());

final filteredInventoryProvider =
    Provider<AsyncValue<List<InventoryItem>>>((ref) {
  final all = ref.watch(inventoryProvider);
  final filter = ref.watch(inventoryFilterProvider);
  return all.whenData((items) {
    var result = items;
    if (filter.category != null) {
      result = result.where((i) => i.category == filter.category).toList();
    }
    if (filter.query.isNotEmpty) {
      final q = filter.query.toLowerCase();
      result = result.where((i) => i.name.toLowerCase().contains(q)).toList();
    }
    return result;
  });
});

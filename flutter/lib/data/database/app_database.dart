import 'dart:io';
import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

part 'app_database.g.dart';

@DataClassName('InventoryRow')
class InventoryItems extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get name => text()();
  TextColumn get category => text()();
  IntColumn get quantity => integer()();
  TextColumn get unit => text()();
  TextColumn get notes => text().withDefault(const Constant(''))();
  IntColumn get dateAdded => integer()(); // milliseconds since epoch
  IntColumn get expiryDate => integer().nullable()(); // milliseconds since epoch
  TextColumn get barcode => text().withDefault(const Constant(''))();
}

@DriftDatabase(tables: [InventoryItems])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  Stream<List<InventoryRow>> watchAll() {
    return (select(inventoryItems)
          ..orderBy([
            (t) => OrderingTerm(
                  expression: t.expiryDate,
                  mode: OrderingMode.asc,
                  nulls: NullsOrder.first,
                ),
            (t) => OrderingTerm(expression: t.name),
          ]))
        .watch();
  }

  Future<void> upsertItem(InventoryItemsCompanion item) =>
      into(inventoryItems).insertOnConflictUpdate(item);

  Future<void> deleteItem(int id) =>
      (delete(inventoryItems)..where((t) => t.id.equals(id))).go();
}

LazyDatabase _openConnection() {
  return LazyDatabase(() async {
    final dir = await getApplicationDocumentsDirectory();
    final file = File(p.join(dir.path, 'can_can.sqlite'));
    return NativeDatabase.createInBackground(file);
  });
}

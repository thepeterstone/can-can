import 'package:drift/drift.dart';
import '../database/app_database.dart';
import '../models/inventory_item.dart';

class InventoryRepository {
  final AppDatabase _db;

  InventoryRepository(this._db);

  Stream<List<InventoryItem>> watchAll() {
    return _db.watchAll().map(
          (rows) => rows.map(_fromRow).toList(),
        );
  }

  Future<void> save(InventoryItem item) {
    return _db.upsertItem(
      InventoryItemsCompanion(
        id: item.id == 0 ? const Value.absent() : Value(item.id),
        name: Value(item.name),
        category: Value(item.category),
        quantity: Value(item.quantity),
        unit: Value(item.unit),
        notes: Value(item.notes),
        dateAdded: Value(item.dateAdded.millisecondsSinceEpoch),
        expiryDate: Value(item.expiryDate?.millisecondsSinceEpoch),
        barcode: Value(item.barcode),
      ),
    );
  }

  Future<void> delete(int id) => _db.deleteItem(id);

  static InventoryItem _fromRow(InventoryRow row) => InventoryItem(
        id: row.id,
        name: row.name,
        category: row.category,
        quantity: row.quantity,
        unit: row.unit,
        notes: row.notes,
        dateAdded: DateTime.fromMillisecondsSinceEpoch(row.dateAdded),
        expiryDate: row.expiryDate != null
            ? DateTime.fromMillisecondsSinceEpoch(row.expiryDate!)
            : null,
        barcode: row.barcode,
      );
}

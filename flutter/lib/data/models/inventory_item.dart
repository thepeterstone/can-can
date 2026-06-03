class InventoryItem {
  final int id;
  final String name;
  final String category;
  final int quantity;
  final String unit;
  final String notes;
  final DateTime dateAdded;
  final DateTime? expiryDate;
  final String barcode;

  const InventoryItem({
    this.id = 0,
    required this.name,
    required this.category,
    required this.quantity,
    required this.unit,
    this.notes = '',
    required this.dateAdded,
    this.expiryDate,
    this.barcode = '',
  });

  InventoryItem copyWith({
    int? id,
    String? name,
    String? category,
    int? quantity,
    String? unit,
    String? notes,
    DateTime? dateAdded,
    Object? expiryDate = _sentinel,
    String? barcode,
  }) {
    return InventoryItem(
      id: id ?? this.id,
      name: name ?? this.name,
      category: category ?? this.category,
      quantity: quantity ?? this.quantity,
      unit: unit ?? this.unit,
      notes: notes ?? this.notes,
      dateAdded: dateAdded ?? this.dateAdded,
      expiryDate: expiryDate == _sentinel ? this.expiryDate : expiryDate as DateTime?,
      barcode: barcode ?? this.barcode,
    );
  }
}

const Object _sentinel = Object();

const inventoryCategories = [
  'Water Bath Canning',
  'Pressure Canning',
  'Fermentation',
  'Dehydrating',
  'Smoking & Curing',
  'Frozen',
  'Other',
];

const inventoryUnits = [
  'jars',
  'half-pints',
  'pints',
  'quarts',
  'lbs',
  'oz',
  'pieces',
  'batches',
];

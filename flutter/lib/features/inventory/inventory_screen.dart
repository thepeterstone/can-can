import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../../data/models/inventory_item.dart';
import 'inventory_notifier.dart';

class InventoryScreen extends ConsumerWidget {
  const InventoryScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final filter = ref.watch(inventoryFilterProvider);
    final filtered = ref.watch(filteredInventoryProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Inventory')),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showItemSheet(context, ref, null),
        child: const Icon(Icons.add),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(12, 8, 12, 0),
            child: SearchBar(
              hintText: 'Search inventory…',
              leading: const Icon(Icons.search),
              onChanged: (q) => ref
                  .read(inventoryFilterProvider.notifier)
                  .update((s) => s.copyWith(query: q)),
            ),
          ),
          _CategoryChips(selected: filter.category),
          const SizedBox(height: 4),
          Expanded(
            child: filtered.when(
              data: (items) => items.isEmpty
                  ? const Center(child: Text('No items. Add something!'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      itemCount: items.length,
                      itemBuilder: (_, i) => _InventoryCard(
                        item: items[i],
                        onTap: () => _showItemSheet(context, ref, items[i]),
                      ),
                    ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (e, _) => Center(child: Text('Error: $e')),
            ),
          ),
        ],
      ),
    );
  }

  void _showItemSheet(BuildContext context, WidgetRef ref, InventoryItem? item) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      builder: (_) => _ItemSheet(existing: item),
    );
  }
}

class _CategoryChips extends ConsumerWidget {
  final String? selected;
  const _CategoryChips({required this.selected});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return SizedBox(
      height: 44,
      child: ListView(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12),
        children: [
          Padding(
            padding: const EdgeInsets.only(right: 8),
            child: FilterChip(
              label: const Text('All'),
              selected: selected == null,
              onSelected: (_) => ref
                  .read(inventoryFilterProvider.notifier)
                  .update((s) => s.copyWith(category: null)),
            ),
          ),
          ...inventoryCategories.map(
            (cat) => Padding(
              padding: const EdgeInsets.only(right: 8),
              child: FilterChip(
                label: Text(cat),
                selected: selected == cat,
                onSelected: (_) => ref
                    .read(inventoryFilterProvider.notifier)
                    .update((s) => s.copyWith(category: cat)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _InventoryCard extends StatelessWidget {
  final InventoryItem item;
  final VoidCallback onTap;
  const _InventoryCard({required this.item, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final now = DateTime.now();
    final expiry = item.expiryDate;

    Color expiryColor = theme.colorScheme.onSurface.withValues(alpha: 0.5);
    String expiryLabel = 'No expiry';
    if (expiry != null) {
      if (expiry.isBefore(now)) {
        expiryColor = theme.colorScheme.error;
        expiryLabel = 'Expired';
      } else if (expiry.difference(now).inDays < 30) {
        expiryColor = Colors.orange;
        expiryLabel = 'Expires soon';
      } else {
        final d = expiry;
        expiryLabel =
            '${_monthName(d.month)} ${d.day}, ${d.year}';
      }
    }

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        onTap: onTap,
        title: Text(item.name, style: const TextStyle(fontWeight: FontWeight.w600)),
        subtitle: Text('${item.quantity} ${item.unit}'),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: theme.colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(item.category,
                  style: TextStyle(
                      fontSize: 10,
                      color: theme.colorScheme.onPrimaryContainer)),
            ),
            const SizedBox(height: 4),
            Text(expiryLabel,
                style: TextStyle(fontSize: 11, color: expiryColor)),
          ],
        ),
      ),
    );
  }

  static String _monthName(int m) => const [
        '',
        'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
        'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
      ][m];
}

class _ItemSheet extends ConsumerStatefulWidget {
  final InventoryItem? existing;
  const _ItemSheet({this.existing});

  @override
  ConsumerState<_ItemSheet> createState() => _ItemSheetState();
}

class _ItemSheetState extends ConsumerState<_ItemSheet> {
  final _nameCtrl = TextEditingController();
  final _qtyCtrl = TextEditingController();
  final _notesCtrl = TextEditingController();
  final _barcodeCtrl = TextEditingController();
  String _category = inventoryCategories.first;
  String _unit = inventoryUnits.first;
  DateTime? _expiryDate;
  bool _saving = false;

  @override
  void initState() {
    super.initState();
    final e = widget.existing;
    if (e != null) {
      _nameCtrl.text = e.name;
      _qtyCtrl.text = e.quantity.toString();
      _notesCtrl.text = e.notes;
      _barcodeCtrl.text = e.barcode;
      _category = e.category;
      _unit = e.unit;
      _expiryDate = e.expiryDate;
    } else {
      _qtyCtrl.text = '1';
    }
  }

  @override
  void dispose() {
    _nameCtrl.dispose();
    _qtyCtrl.dispose();
    _notesCtrl.dispose();
    _barcodeCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final repo = ref.read(inventoryRepositoryProvider);
    return Padding(
      padding: EdgeInsets.only(
        left: 16, right: 16, top: 16,
        bottom: MediaQuery.of(context).viewInsets.bottom + 16,
      ),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(widget.existing == null ? 'Add Item' : 'Edit Item',
                style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 16),
            TextField(
              controller: _nameCtrl,
              decoration: const InputDecoration(
                  labelText: 'Name *', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _category,
              decoration: const InputDecoration(
                  labelText: 'Category', border: OutlineInputBorder()),
              items: inventoryCategories
                  .map((c) => DropdownMenuItem(value: c, child: Text(c)))
                  .toList(),
              onChanged: (v) => setState(() => _category = v!),
            ),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: TextField(
                  controller: _qtyCtrl,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                      labelText: 'Quantity', border: OutlineInputBorder()),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: DropdownButtonFormField<String>(
                  value: _unit,
                  decoration: const InputDecoration(
                      labelText: 'Unit', border: OutlineInputBorder()),
                  items: inventoryUnits
                      .map((u) => DropdownMenuItem(value: u, child: Text(u)))
                      .toList(),
                  onChanged: (v) => setState(() => _unit = v!),
                ),
              ),
            ]),
            const SizedBox(height: 12),
            TextField(
              controller: _notesCtrl,
              maxLines: 3,
              decoration: const InputDecoration(
                  labelText: 'Notes', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: OutlinedButton.icon(
                  icon: const Icon(Icons.calendar_today),
                  label: Text(_expiryDate == null
                      ? 'Set expiry date'
                      : 'Expiry: ${_expiryDate!.month}/${_expiryDate!.day}/${_expiryDate!.year}'),
                  onPressed: () async {
                    final d = await showDatePicker(
                      context: context,
                      initialDate: _expiryDate ?? DateTime.now().add(const Duration(days: 365)),
                      firstDate: DateTime.now(),
                      lastDate: DateTime.now().add(const Duration(days: 365 * 10)),
                    );
                    if (d != null) setState(() => _expiryDate = d);
                  },
                ),
              ),
              if (_expiryDate != null) ...[
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () => setState(() => _expiryDate = null),
                ),
              ],
            ]),
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: TextField(
                  controller: _barcodeCtrl,
                  decoration: const InputDecoration(
                      labelText: 'Barcode (optional)',
                      border: OutlineInputBorder()),
                ),
              ),
              const SizedBox(width: 8),
              IconButton.filled(
                icon: const Icon(Icons.qr_code_scanner),
                onPressed: () => _openScanner(context),
              ),
            ]),
            const SizedBox(height: 20),
            Row(children: [
              if (widget.existing != null)
                Expanded(
                  child: OutlinedButton(
                    style: OutlinedButton.styleFrom(
                        foregroundColor:
                            Theme.of(context).colorScheme.error),
                    onPressed: _saving ? null : () async {
                      await repo.delete(widget.existing!.id);
                      if (context.mounted) Navigator.pop(context);
                    },
                    child: const Text('Delete'),
                  ),
                ),
              if (widget.existing != null) const SizedBox(width: 8),
              Expanded(
                child: FilledButton(
                  onPressed: _saving ? null : () => _save(repo, context),
                  child: Text(_saving ? 'Saving…' : 'Save'),
                ),
              ),
            ]),
          ],
        ),
      ),
    );
  }

  Future<void> _save(repo, BuildContext context) async {
    final name = _nameCtrl.text.trim();
    if (name.isEmpty) return;
    setState(() => _saving = true);
    final item = InventoryItem(
      id: widget.existing?.id ?? 0,
      name: name,
      category: _category,
      quantity: int.tryParse(_qtyCtrl.text) ?? 1,
      unit: _unit,
      notes: _notesCtrl.text.trim(),
      dateAdded: widget.existing?.dateAdded ?? DateTime.now(),
      expiryDate: _expiryDate,
      barcode: _barcodeCtrl.text.trim(),
    );
    await repo.save(item);
    if (context.mounted) Navigator.pop(context);
  }

  Future<void> _openScanner(BuildContext context) async {
    final result = await Navigator.push<String>(
      context,
      MaterialPageRoute(builder: (_) => const _BarcodeScannerPage()),
    );
    if (result != null) _barcodeCtrl.text = result;
  }
}

class _BarcodeScannerPage extends StatefulWidget {
  const _BarcodeScannerPage();

  @override
  State<_BarcodeScannerPage> createState() => _BarcodeScannerPageState();
}

class _BarcodeScannerPageState extends State<_BarcodeScannerPage> {
  bool _detected = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Scan Barcode')),
      body: MobileScanner(
        onDetect: (capture) {
          if (_detected) return;
          final barcode = capture.barcodes.firstOrNull;
          if (barcode?.rawValue != null) {
            _detected = true;
            Navigator.pop(context, barcode!.rawValue);
          }
        },
      ),
    );
  }
}

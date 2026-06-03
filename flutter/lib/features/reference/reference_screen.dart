import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/reference_item.dart';
import 'reference_notifier.dart';

class ReferenceScreen extends ConsumerWidget {
  const ReferenceScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final filter = ref.watch(referenceFilterProvider);
    final filtered = ref.watch(filteredReferenceProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Reference')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(12, 8, 12, 0),
            child: SearchBar(
              hintText: 'Search reference…',
              leading: const Icon(Icons.search),
              onChanged: (q) => ref
                  .read(referenceFilterProvider.notifier)
                  .update((s) => s.copyWith(query: q)),
            ),
          ),
          _CategoryChips(selected: filter.category),
          const SizedBox(height: 4),
          Expanded(
            child: filtered.when(
              data: (items) => items.isEmpty
                  ? const Center(child: Text('No items found'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      itemCount: items.length,
                      itemBuilder: (_, i) =>
                          _ReferenceCard(item: items[i]),
                    ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (e, _) => Center(child: Text('Error: $e')),
            ),
          ),
        ],
      ),
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
                  .read(referenceFilterProvider.notifier)
                  .update((s) => s.copyWith(category: null)),
            ),
          ),
          ...referenceCategories.map(
            (cat) => Padding(
              padding: const EdgeInsets.only(right: 8),
              child: FilterChip(
                label: Text(cat),
                selected: selected == cat,
                onSelected: (_) => ref
                    .read(referenceFilterProvider.notifier)
                    .update((s) => s.copyWith(category: cat)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _ReferenceCard extends StatefulWidget {
  final ReferenceItem item;
  const _ReferenceCard({required this.item});

  @override
  State<_ReferenceCard> createState() => _ReferenceCardState();
}

class _ReferenceCardState extends State<_ReferenceCard> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    final item = widget.item;
    final theme = Theme.of(context);
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: InkWell(
        onTap: () => setState(() => _expanded = !_expanded),
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  if (item.wikipediaTitle.isNotEmpty)
                    Padding(
                      padding: const EdgeInsets.only(right: 8),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(4),
                        child: CachedNetworkImage(
                          imageUrl:
                              'https://en.wikipedia.org/wiki/Special:FilePath/${Uri.encodeComponent(item.wikipediaTitle)}?width=60',
                          width: 48,
                          height: 48,
                          fit: BoxFit.cover,
                          errorWidget: (_, __, ___) => const SizedBox.shrink(),
                        ),
                      ),
                    ),
                  Expanded(
                    child: Text(item.name,
                        style: theme.textTheme.titleMedium
                            ?.copyWith(fontWeight: FontWeight.bold)),
                  ),
                  _MethodBadge(item.method),
                  Icon(_expanded ? Icons.expand_less : Icons.expand_more),
                ],
              ),
              if (_expanded) ...[
                const SizedBox(height: 8),
                if (item.type == 'lookup')
                  _LookupTable(item: item)
                else
                  _GuideContent(item: item),
                if (item.safetyNotes.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: theme.colorScheme.errorContainer,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Icon(Icons.warning,
                            color: theme.colorScheme.onErrorContainer,
                            size: 16),
                        const SizedBox(width: 6),
                        Expanded(
                          child: Text(
                            item.safetyNotes,
                            style: TextStyle(
                                color: theme.colorScheme.onErrorContainer),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
                if (item.source.isNotEmpty) ...[
                  const SizedBox(height: 4),
                  Text('Source: ${item.source}',
                      style: theme.textTheme.bodySmall
                          ?.copyWith(fontStyle: FontStyle.italic)),
                ],
              ],
            ],
          ),
        ),
      ),
    );
  }
}

class _MethodBadge extends StatelessWidget {
  final String method;
  const _MethodBadge(this.method);

  @override
  Widget build(BuildContext context) {
    final label = switch (method) {
      'water_bath' => 'WB',
      'pressure' => 'PC',
      'either' => 'WB/PC',
      _ => '',
    };
    if (label.isEmpty) return const SizedBox.shrink();
    return Container(
      margin: const EdgeInsets.only(right: 8),
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.primaryContainer,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Text(label,
          style: TextStyle(
              fontSize: 11,
              color: Theme.of(context).colorScheme.onPrimaryContainer,
              fontWeight: FontWeight.bold)),
    );
  }
}

class _LookupTable extends StatelessWidget {
  final ReferenceItem item;
  const _LookupTable({required this.item});

  @override
  Widget build(BuildContext context) {
    if (item.entries.isEmpty) return const SizedBox.shrink();
    return Table(
      border: TableBorder.all(
          color: Theme.of(context).dividerColor, borderRadius: BorderRadius.circular(4)),
      columnWidths: const {
        0: FlexColumnWidth(2),
        1: FlexColumnWidth(1),
        2: FlexColumnWidth(2),
      },
      children: [
        TableRow(
          decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surfaceContainerHighest),
          children: const [
            _Cell('Jar / Pack', header: true),
            _Cell('Mins', header: true),
            _Cell('Pressure PSI', header: true),
          ],
        ),
        ...item.entries.map(
          (e) => TableRow(children: [
            _Cell('${_jarLabel(e.jarSize)} / ${e.pack}'),
            _Cell('${e.processingTimeMinutes}'),
            _Cell(_psiLabel(e)),
          ]),
        ),
      ],
    );
  }

  static String _jarLabel(String s) => switch (s) {
        'half_pint' => '½ pt',
        'pint' => 'Pint',
        'quart' => 'Quart',
        _ => s,
      };

  static String _psiLabel(e) {
    if (e.pressurePsiWeighted == null && e.pressurePsiDial == null) return '—';
    if (e.pressurePsiWeighted != null && e.pressurePsiDial != null) {
      return '${e.pressurePsiWeighted}W / ${e.pressurePsiDial}D';
    }
    return '${e.pressurePsiWeighted ?? e.pressurePsiDial} psi';
  }
}

class _Cell extends StatelessWidget {
  final String text;
  final bool header;
  const _Cell(this.text, {this.header = false});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(6),
      child: Text(text,
          style: header
              ? const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)
              : const TextStyle(fontSize: 12)),
    );
  }
}

class _GuideContent extends StatelessWidget {
  final ReferenceItem item;
  const _GuideContent({required this.item});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (item.summary.isNotEmpty) Text(item.summary),
        ...item.sections.map(
          (s) => Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 10),
              Text(s.heading,
                  style: theme.textTheme.titleSmall
                      ?.copyWith(fontWeight: FontWeight.bold)),
              const SizedBox(height: 4),
              Text(s.body),
              ...s.tips.map(
                (tip) => Padding(
                  padding: const EdgeInsets.only(left: 12, top: 4),
                  child: Text('💡 $tip',
                      style: theme.textTheme.bodySmall),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

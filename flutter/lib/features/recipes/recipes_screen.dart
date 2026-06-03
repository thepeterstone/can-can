import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/recipe_item.dart';
import 'recipes_notifier.dart';

class RecipesScreen extends ConsumerWidget {
  const RecipesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final filter = ref.watch(recipesFilterProvider);
    final filtered = ref.watch(filteredRecipesProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Recipes')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(12, 8, 12, 0),
            child: SearchBar(
              hintText: 'Search recipes…',
              leading: const Icon(Icons.search),
              onChanged: (q) => ref
                  .read(recipesFilterProvider.notifier)
                  .update((s) => s.copyWith(query: q)),
            ),
          ),
          _CategoryChips(selected: filter.category),
          const SizedBox(height: 4),
          Expanded(
            child: filtered.when(
              data: (items) => items.isEmpty
                  ? const Center(child: Text('No recipes found'))
                  : ListView.builder(
                      padding: const EdgeInsets.symmetric(horizontal: 12),
                      itemCount: items.length,
                      itemBuilder: (_, i) => _RecipeCard(recipe: items[i]),
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
                  .read(recipesFilterProvider.notifier)
                  .update((s) => s.copyWith(category: null)),
            ),
          ),
          ...recipeCategories.map(
            (cat) => Padding(
              padding: const EdgeInsets.only(right: 8),
              child: FilterChip(
                label: Text(cat),
                selected: selected == cat,
                onSelected: (_) => ref
                    .read(recipesFilterProvider.notifier)
                    .update((s) => s.copyWith(category: cat)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _RecipeCard extends StatefulWidget {
  final RecipeItem recipe;
  const _RecipeCard({required this.recipe});

  @override
  State<_RecipeCard> createState() => _RecipeCardState();
}

class _RecipeCardState extends State<_RecipeCard> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    final r = widget.recipe;
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
                  Expanded(
                    child: Text(r.name,
                        style: theme.textTheme.titleMedium
                            ?.copyWith(fontWeight: FontWeight.bold)),
                  ),
                  if (r.difficulty.isNotEmpty) _DifficultyBadge(r.difficulty),
                  Icon(_expanded ? Icons.expand_less : Icons.expand_more),
                ],
              ),
              if (r.timeEstimate.isNotEmpty || r.recipeYield.isNotEmpty)
                Padding(
                  padding: const EdgeInsets.only(top: 4),
                  child: Text(
                    [
                      if (r.timeEstimate.isNotEmpty) r.timeEstimate,
                      if (r.recipeYield.isNotEmpty) r.recipeYield,
                    ].join(' · '),
                    style: theme.textTheme.bodySmall,
                  ),
                ),
              if (_expanded) ...[
                if (r.summary.isNotEmpty) ...[
                  const SizedBox(height: 8),
                  Text(r.summary),
                ],
                if (r.ingredients.isNotEmpty) ...[
                  const SizedBox(height: 12),
                  Text('Ingredients',
                      style: theme.textTheme.titleSmall
                          ?.copyWith(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  ...r.ingredients.map(
                    (ing) => Padding(
                      padding: const EdgeInsets.only(bottom: 2),
                      child: Text(
                          '• ${ing.quantity.isNotEmpty ? "${ing.quantity} " : ""}${ing.name}${ing.notes.isNotEmpty ? " (${ing.notes})" : ""}'),
                    ),
                  ),
                ],
                if (r.steps.isNotEmpty) ...[
                  const SizedBox(height: 12),
                  Text('Instructions',
                      style: theme.textTheme.titleSmall
                          ?.copyWith(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  ...r.steps.asMap().entries.map(
                        (e) => Padding(
                          padding: const EdgeInsets.only(bottom: 6),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('${e.key + 1}. ${e.value.instruction}'),
                              ...e.value.tips.map(
                                (tip) => Padding(
                                  padding: const EdgeInsets.only(left: 16, top: 2),
                                  child: Text('💡 $tip',
                                      style: theme.textTheme.bodySmall),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                ],
                if (r.safetyNotes.isNotEmpty) ...[
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
                            color: theme.colorScheme.onErrorContainer, size: 16),
                        const SizedBox(width: 6),
                        Expanded(
                          child: Text(
                            r.safetyNotes,
                            style: TextStyle(
                                color: theme.colorScheme.onErrorContainer),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
                if (r.source.isNotEmpty) ...[
                  const SizedBox(height: 4),
                  Text('Source: ${r.source}',
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

class _DifficultyBadge extends StatelessWidget {
  final String difficulty;
  const _DifficultyBadge(this.difficulty);

  @override
  Widget build(BuildContext context) {
    final color = switch (difficulty.toLowerCase()) {
      'beginner' => Colors.green,
      'intermediate' => Colors.orange,
      'advanced' => Colors.red,
      _ => Colors.grey,
    };
    return Container(
      margin: const EdgeInsets.only(right: 8),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.15),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withValues(alpha: 0.5)),
      ),
      child: Text(
        difficulty,
        style: TextStyle(color: color, fontSize: 11, fontWeight: FontWeight.w600),
      ),
    );
  }
}

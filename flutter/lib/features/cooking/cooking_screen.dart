import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:wakelock_plus/wakelock_plus.dart';
import '../../data/models/recipe_item.dart';
import '../recipes/recipes_notifier.dart';
import 'cooking_notifier.dart';

class CookingScreen extends ConsumerStatefulWidget {
  const CookingScreen({super.key});

  @override
  ConsumerState<CookingScreen> createState() => _CookingScreenState();
}

class _CookingScreenState extends ConsumerState<CookingScreen> {
  @override
  void dispose() {
    WakelockPlus.disable();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final cookState = ref.watch(cookingProvider);
    final hasActivity =
        cookState.session != null || cookState.timers.any((t) => t.running);

    if (hasActivity) {
      WakelockPlus.enable();
    } else {
      WakelockPlus.disable();
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Cooking')),
      body: ListView(
        padding: const EdgeInsets.all(12),
        children: [
          if (cookState.session != null)
            _SessionCard(session: cookState.session!),
          if (cookState.session == null)
            _StartSessionCard(),
          const SizedBox(height: 16),
          ...cookState.timers.map((t) => _TimerCard(timer: t)),
          OutlinedButton.icon(
            icon: const Icon(Icons.timer_outlined),
            label: const Text('Add Timer'),
            onPressed: () => _showAddTimer(context),
          ),
        ],
      ),
    );
  }

  void _showAddTimer(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (_) => _AddTimerSheet(),
    );
  }
}

class _StartSessionCard extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final recipes = ref.watch(recipesProvider);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Text('No active recipe session.',
                style: TextStyle(fontSize: 16)),
            const SizedBox(height: 12),
            FilledButton.icon(
              icon: const Icon(Icons.restaurant_menu),
              label: const Text('Start Recipe'),
              onPressed: () => recipes.whenData((list) =>
                  _showRecipePicker(context, ref, list)),
            ),
          ],
        ),
      ),
    );
  }

  void _showRecipePicker(
      BuildContext context, WidgetRef ref, List<RecipeItem> recipes) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (_) => DraggableScrollableSheet(
        expand: false,
        initialChildSize: 0.6,
        maxChildSize: 0.9,
        builder: (_, ctrl) => ListView.builder(
          controller: ctrl,
          itemCount: recipes.length,
          itemBuilder: (_, i) => ListTile(
            title: Text(recipes[i].name),
            subtitle: Text(recipes[i].category),
            onTap: () {
              ref.read(cookingProvider.notifier).startSession(recipes[i]);
              Navigator.pop(context);
            },
          ),
        ),
      ),
    );
  }
}

class _SessionCard extends ConsumerWidget {
  final CookingSession session;
  const _SessionCard({required this.session});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final notifier = ref.read(cookingProvider.notifier);
    final theme = Theme.of(context);
    final step = session.step;
    final progress = (session.currentStep + 1) / session.recipe.steps.length;

    final timerMatch = RegExp(r'(\d+)\s*minute').firstMatch(step.instruction);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(children: [
              Expanded(
                child: Text(session.recipe.name,
                    style: theme.textTheme.titleMedium
                        ?.copyWith(fontWeight: FontWeight.bold)),
              ),
              TextButton(
                  onPressed: notifier.endSession,
                  child: const Text('End')),
            ]),
            const SizedBox(height: 4),
            Text(
                'Step ${session.currentStep + 1} of ${session.recipe.steps.length}',
                style: theme.textTheme.bodySmall),
            const SizedBox(height: 6),
            LinearProgressIndicator(value: progress),
            const SizedBox(height: 12),
            Text(step.instruction, style: theme.textTheme.bodyLarge),
            ...step.tips.map(
              (tip) => Padding(
                padding: const EdgeInsets.only(top: 6),
                child: Text('💡 $tip',
                    style: theme.textTheme.bodySmall),
              ),
            ),
            if (timerMatch != null) ...[
              const SizedBox(height: 8),
              OutlinedButton.icon(
                icon: const Icon(Icons.timer),
                label: Text('Start ${timerMatch.group(1)} min timer'),
                onPressed: () => ref
                    .read(cookingProvider.notifier)
                    .addTimer('Step ${session.currentStep + 1}',
                        Duration(minutes: int.parse(timerMatch.group(1)!))),
              ),
            ],
            const SizedBox(height: 12),
            Row(children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: session.isFirst ? null : notifier.prevStep,
                  child: const Text('← Prev'),
                ),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: FilledButton(
                  onPressed: session.isLast ? null : notifier.nextStep,
                  child: const Text('Next →'),
                ),
              ),
            ]),
          ],
        ),
      ),
    );
  }
}

class _TimerCard extends ConsumerWidget {
  final TimerItem timer;
  const _TimerCard({required this.timer});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final notifier = ref.read(cookingProvider.notifier);
    final theme = Theme.of(context);
    final remaining = timer.remaining;
    final progress = timer.total.inSeconds == 0
        ? 0.0
        : (timer.total.inSeconds - remaining.inSeconds) /
            timer.total.inSeconds;

    Color timeColor = theme.colorScheme.onSurface;
    if (timer.isDone) {
      timeColor = theme.colorScheme.primary;
    } else if (remaining.inSeconds < 60) {
      timeColor = theme.colorScheme.error;
    } else if (timer.running) {
      timeColor = theme.colorScheme.tertiary;
    }

    String timeStr;
    if (remaining.inHours > 0) {
      timeStr =
          '${remaining.inHours}:${(remaining.inMinutes % 60).toString().padLeft(2, '0')}:${(remaining.inSeconds % 60).toString().padLeft(2, '0')}';
    } else {
      timeStr =
          '${remaining.inMinutes.toString().padLeft(2, '0')}:${(remaining.inSeconds % 60).toString().padLeft(2, '0')}';
    }

    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(children: [
              Expanded(
                  child: Text(timer.name.isNotEmpty ? timer.name : 'Timer',
                      style: const TextStyle(fontWeight: FontWeight.w600))),
              IconButton(
                icon: const Icon(Icons.close, size: 18),
                onPressed: () => notifier.removeTimer(timer.id),
              ),
            ]),
            Text(
              timer.isDone ? 'Done!' : timeStr,
              style: theme.textTheme.displaySmall
                  ?.copyWith(color: timeColor, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 6),
            LinearProgressIndicator(value: progress),
            const SizedBox(height: 8),
            Row(children: [
              Expanded(
                child: FilledButton.icon(
                  icon: Icon(timer.running ? Icons.pause : Icons.play_arrow),
                  label: Text(timer.running ? 'Pause' : 'Start'),
                  onPressed:
                      timer.isDone ? null : () => notifier.toggleTimer(timer.id),
                ),
              ),
              const SizedBox(width: 8),
              OutlinedButton(
                onPressed: () => notifier.resetTimer(timer.id),
                child: const Text('Reset'),
              ),
            ]),
          ],
        ),
      ),
    );
  }
}

class _AddTimerSheet extends ConsumerStatefulWidget {
  @override
  ConsumerState<_AddTimerSheet> createState() => _AddTimerSheetState();
}

class _AddTimerSheetState extends ConsumerState<_AddTimerSheet> {
  final _nameCtrl = TextEditingController();
  final _customCtrl = TextEditingController();
  int? _presetMinutes;

  static const presets = [5, 10, 15, 20, 30, 45, 60];

  @override
  void dispose() {
    _nameCtrl.dispose();
    _customCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(
        left: 16, right: 16, top: 16,
        bottom: MediaQuery.of(context).viewInsets.bottom + 16,
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text('Add Timer', style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 12),
          TextField(
            controller: _nameCtrl,
            decoration: const InputDecoration(
                labelText: 'Name (optional)', border: OutlineInputBorder()),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            children: presets
                .map(
                  (m) => ChoiceChip(
                    label: Text('$m min'),
                    selected: _presetMinutes == m,
                    onSelected: (_) =>
                        setState(() => _presetMinutes = m == _presetMinutes ? null : m),
                  ),
                )
                .toList(),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _customCtrl,
            keyboardType: TextInputType.number,
            decoration: const InputDecoration(
                labelText: 'Custom minutes', border: OutlineInputBorder()),
            onChanged: (_) => setState(() => _presetMinutes = null),
          ),
          const SizedBox(height: 16),
          FilledButton(
            onPressed: _canAdd ? _add : null,
            child: const Text('Add Timer'),
          ),
        ],
      ),
    );
  }

  bool get _canAdd =>
      _presetMinutes != null || (int.tryParse(_customCtrl.text) ?? 0) > 0;

  void _add() {
    final minutes =
        _presetMinutes ?? int.tryParse(_customCtrl.text) ?? 0;
    if (minutes <= 0) return;
    ref.read(cookingProvider.notifier).addTimer(
          _nameCtrl.text.trim(),
          Duration(minutes: minutes),
        );
    Navigator.pop(context);
  }
}

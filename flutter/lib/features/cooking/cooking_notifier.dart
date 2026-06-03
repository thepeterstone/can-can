import 'dart:async';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/recipe_item.dart';

class TimerItem {
  final String id;
  final String name;
  final Duration total;
  final Duration remaining;
  final bool running;

  const TimerItem({
    required this.id,
    required this.name,
    required this.total,
    required this.remaining,
    this.running = false,
  });

  bool get isDone => remaining == Duration.zero;

  TimerItem copyWith({
    String? name,
    Duration? total,
    Duration? remaining,
    bool? running,
  }) =>
      TimerItem(
        id: id,
        name: name ?? this.name,
        total: total ?? this.total,
        remaining: remaining ?? this.remaining,
        running: running ?? this.running,
      );
}

class CookingSession {
  final RecipeItem recipe;
  final int currentStep;

  const CookingSession({required this.recipe, this.currentStep = 0});

  CookingSession copyWith({int? currentStep}) =>
      CookingSession(recipe: recipe, currentStep: currentStep ?? this.currentStep);

  bool get isFirst => currentStep == 0;
  bool get isLast => currentStep >= recipe.steps.length - 1;
  RecipeStep get step => recipe.steps[currentStep];
}

class CookingState {
  final CookingSession? session;
  final List<TimerItem> timers;

  const CookingState({this.session, this.timers = const []});

  CookingState copyWith({
    Object? session = _sentinel,
    List<TimerItem>? timers,
  }) =>
      CookingState(
        session: session == _sentinel ? this.session : session as CookingSession?,
        timers: timers ?? this.timers,
      );
}

const Object _sentinel = Object();

class CookingNotifier extends StateNotifier<CookingState> {
  Timer? _ticker;

  CookingNotifier() : super(const CookingState());

  void startSession(RecipeItem recipe) {
    state = state.copyWith(session: CookingSession(recipe: recipe));
  }

  void endSession() {
    state = state.copyWith(session: null);
  }

  void nextStep() {
    final s = state.session;
    if (s == null || s.isLast) return;
    state = state.copyWith(session: s.copyWith(currentStep: s.currentStep + 1));
  }

  void prevStep() {
    final s = state.session;
    if (s == null || s.isFirst) return;
    state = state.copyWith(session: s.copyWith(currentStep: s.currentStep - 1));
  }

  void addTimer(String name, Duration duration) {
    final id = DateTime.now().millisecondsSinceEpoch.toString();
    final timers = [...state.timers, TimerItem(id: id, name: name, total: duration, remaining: duration)];
    state = state.copyWith(timers: timers);
    _ensureTicker();
  }

  void toggleTimer(String id) {
    final timers = state.timers.map((t) {
      if (t.id != id) return t;
      return t.copyWith(running: !t.running);
    }).toList();
    state = state.copyWith(timers: timers);
    _ensureTicker();
  }

  void resetTimer(String id) {
    final timers = state.timers.map((t) {
      if (t.id != id) return t;
      return t.copyWith(remaining: t.total, running: false);
    }).toList();
    state = state.copyWith(timers: timers);
  }

  void removeTimer(String id) {
    final timers = state.timers.where((t) => t.id != id).toList();
    state = state.copyWith(timers: timers);
    if (!timers.any((t) => t.running)) _ticker?.cancel();
  }

  void _ensureTicker() {
    if (_ticker?.isActive == true) return;
    _ticker = Timer.periodic(const Duration(seconds: 1), (_) => _tick());
  }

  void _tick() {
    if (!state.timers.any((t) => t.running && !t.isDone)) {
      _ticker?.cancel();
      return;
    }
    final timers = state.timers.map((t) {
      if (!t.running || t.isDone) return t;
      final next = t.remaining - const Duration(seconds: 1);
      return t.copyWith(
          remaining: next < Duration.zero ? Duration.zero : next,
          running: next > Duration.zero);
    }).toList();
    state = state.copyWith(timers: timers);
  }

  @override
  void dispose() {
    _ticker?.cancel();
    super.dispose();
  }
}

final cookingProvider =
    StateNotifierProvider<CookingNotifier, CookingState>((_) => CookingNotifier());

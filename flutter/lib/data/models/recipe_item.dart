class Ingredient {
  final String name;
  final String quantity;
  final String notes;

  const Ingredient({required this.name, this.quantity = '', this.notes = ''});

  factory Ingredient.fromJson(Map<String, dynamic> j) => Ingredient(
        name: j['name'] as String,
        quantity: (j['quantity'] as String?) ?? '',
        notes: (j['notes'] as String?) ?? '',
      );
}

class RecipeStep {
  final String instruction;
  final List<String> tips;

  const RecipeStep({required this.instruction, this.tips = const []});

  factory RecipeStep.fromJson(Map<String, dynamic> j) => RecipeStep(
        instruction: j['instruction'] as String,
        tips: (j['tips'] as List<dynamic>?)?.cast<String>() ?? [],
      );
}

class RecipeItem {
  final String id;
  final String name;
  final String category;
  final String summary;
  final String difficulty;
  final String timeEstimate;
  final String recipeYield;
  final List<Ingredient> ingredients;
  final List<RecipeStep> steps;
  final String safetyNotes;
  final String source;

  const RecipeItem({
    required this.id,
    required this.name,
    required this.category,
    this.summary = '',
    this.difficulty = '',
    this.timeEstimate = '',
    this.recipeYield = '',
    this.ingredients = const [],
    this.steps = const [],
    this.safetyNotes = '',
    this.source = '',
  });

  factory RecipeItem.fromJson(Map<String, dynamic> j) => RecipeItem(
        id: j['id'] as String,
        name: j['name'] as String,
        category: j['category'] as String,
        summary: (j['summary'] as String?) ?? '',
        difficulty: (j['difficulty'] as String?) ?? '',
        timeEstimate: (j['time_estimate'] as String?) ?? '',
        recipeYield: (j['yield'] as String?) ?? '',
        ingredients: (j['ingredients'] as List<dynamic>?)
                ?.map((e) => Ingredient.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        steps: (j['steps'] as List<dynamic>?)
                ?.map((e) => RecipeStep.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        safetyNotes: (j['safety_notes'] as String?) ?? '',
        source: (j['source'] as String?) ?? '',
      );
}

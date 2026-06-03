import 'dart:convert';
import 'package:flutter/services.dart';
import '../models/recipe_item.dart';

class RecipeRepository {
  List<RecipeItem>? _cache;

  Future<List<RecipeItem>> getAll() async {
    if (_cache != null) return _cache!;
    final raw = await rootBundle.loadString('assets/recipes/recipes.json');
    final json = jsonDecode(raw) as Map<String, dynamic>;
    _cache = (json['items'] as List<dynamic>)
        .map((e) => RecipeItem.fromJson(e as Map<String, dynamic>))
        .toList();
    return _cache!;
  }
}

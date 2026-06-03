import 'dart:convert';
import 'package:flutter/services.dart';
import '../models/reference_item.dart';

class ReferenceRepository {
  List<ReferenceItem>? _cache;

  static const _assetPaths = [
    'assets/reference/canning_guide.json',
    'assets/reference/fermentation_guide.json',
    'assets/reference/fishing_guide.json',
    'assets/reference/foraging_guide.json',
    'assets/reference/preservation_guide.json',
  ];

  Future<List<ReferenceItem>> getAll() async {
    if (_cache != null) return _cache!;
    final items = <ReferenceItem>[];
    for (final path in _assetPaths) {
      final raw = await rootBundle.loadString(path);
      final json = jsonDecode(raw) as Map<String, dynamic>;
      final parsed = (json['items'] as List<dynamic>)
          .map((e) => ReferenceItem.fromJson(e as Map<String, dynamic>))
          .toList();
      items.addAll(parsed);
    }
    _cache = items;
    return _cache!;
  }
}

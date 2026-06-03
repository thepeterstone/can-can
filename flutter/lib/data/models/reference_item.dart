class ProcessingEntry {
  final String jarSize;
  final String pack;
  final int processingTimeMinutes;
  final int? pressurePsiWeighted;
  final int? pressurePsiDial;

  const ProcessingEntry({
    required this.jarSize,
    required this.pack,
    required this.processingTimeMinutes,
    this.pressurePsiWeighted,
    this.pressurePsiDial,
  });

  factory ProcessingEntry.fromJson(Map<String, dynamic> j) => ProcessingEntry(
        jarSize: j['jar_size'] as String,
        pack: j['pack'] as String,
        processingTimeMinutes: j['processing_time_minutes'] as int,
        pressurePsiWeighted: j['pressure_psi_weighted'] as int?,
        pressurePsiDial: j['pressure_psi_dial'] as int?,
      );
}

class GuideSection {
  final String heading;
  final String body;
  final List<String> tips;

  const GuideSection({
    required this.heading,
    required this.body,
    this.tips = const [],
  });

  factory GuideSection.fromJson(Map<String, dynamic> j) => GuideSection(
        heading: j['heading'] as String,
        body: j['body'] as String,
        tips: (j['tips'] as List<dynamic>?)?.cast<String>() ?? [],
      );
}

class ReferenceItem {
  final String id;
  final String name;
  final String category;
  final String type; // "lookup" or "guide"

  // Lookup fields
  final String method;
  final String altitudeNote;
  final List<ProcessingEntry> entries;

  // Guide fields
  final String summary;
  final String difficulty;
  final String timeEstimate;
  final List<GuideSection> sections;

  // Shared
  final String safetyNotes;
  final String source;
  final String wikipediaTitle;

  const ReferenceItem({
    required this.id,
    required this.name,
    required this.category,
    this.type = 'lookup',
    this.method = '',
    this.altitudeNote = '',
    this.entries = const [],
    this.summary = '',
    this.difficulty = '',
    this.timeEstimate = '',
    this.sections = const [],
    this.safetyNotes = '',
    this.source = '',
    this.wikipediaTitle = '',
  });

  factory ReferenceItem.fromJson(Map<String, dynamic> j) => ReferenceItem(
        id: j['id'] as String,
        name: j['name'] as String,
        category: j['category'] as String,
        type: (j['type'] as String?) ?? 'lookup',
        method: (j['method'] as String?) ?? '',
        altitudeNote: (j['altitude_note'] as String?) ?? '',
        entries: (j['entries'] as List<dynamic>?)
                ?.map((e) => ProcessingEntry.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        summary: (j['summary'] as String?) ?? '',
        difficulty: (j['difficulty'] as String?) ?? '',
        timeEstimate: (j['time_estimate'] as String?) ?? '',
        sections: (j['sections'] as List<dynamic>?)
                ?.map((e) => GuideSection.fromJson(e as Map<String, dynamic>))
                .toList() ??
            [],
        safetyNotes: (j['safety_notes'] as String?) ?? '',
        source: (j['source'] as String?) ?? '',
        wikipediaTitle: (j['wikipedia_title'] as String?) ?? '',
      );
}

import 'package:flutter/material.dart';

final canCanTheme = ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: const Color(0xFF2E7D32), // dark green — canning aesthetic
    brightness: Brightness.light,
  ),
);

final canCanDarkTheme = ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: const Color(0xFF2E7D32),
    brightness: Brightness.dark,
  ),
);

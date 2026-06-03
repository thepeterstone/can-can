import 'dart:developer' as developer;

class CanCanLogger {
  static void d(String tag, String message) =>
      developer.log(message, name: tag, level: 500);

  static void i(String tag, String message) =>
      developer.log(message, name: tag, level: 800);

  static void w(String tag, String message) =>
      developer.log(message, name: tag, level: 900);

  static void e(String tag, String message, [Object? error, StackTrace? stack]) =>
      developer.log(message, name: tag, level: 1000, error: error, stackTrace: stack);
}

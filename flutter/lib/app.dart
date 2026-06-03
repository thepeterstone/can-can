import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'core/theme.dart';
import 'features/cooking/cooking_screen.dart';
import 'features/inventory/inventory_screen.dart';
import 'features/library/library_screen.dart';
import 'features/library/pdf_viewer_screen.dart';
import 'features/recipes/recipes_screen.dart';
import 'features/reference/reference_screen.dart';

final _router = GoRouter(
  initialLocation: '/recipes',
  routes: [
    ShellRoute(
      builder: (context, state, child) => _Shell(child: child),
      routes: [
        GoRoute(
          path: '/recipes',
          builder: (_, __) => const RecipesScreen(),
        ),
        GoRoute(
          path: '/cooking',
          builder: (_, __) => const CookingScreen(),
        ),
        GoRoute(
          path: '/inventory',
          builder: (_, __) => const InventoryScreen(),
        ),
        GoRoute(
          path: '/reference',
          builder: (_, __) => const ReferenceScreen(),
        ),
        GoRoute(
          path: '/library',
          builder: (_, __) => const LibraryScreen(),
          routes: [
            GoRoute(
              path: 'pdf/:documentId',
              builder: (_, state) => PdfViewerScreen(
                documentId: state.pathParameters['documentId']!,
              ),
            ),
          ],
        ),
      ],
    ),
  ],
);

class CanCanApp extends StatelessWidget {
  const CanCanApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Can Can',
      theme: canCanTheme,
      darkTheme: canCanDarkTheme,
      routerConfig: _router,
    );
  }
}

class _Shell extends StatelessWidget {
  final Widget child;
  const _Shell({required this.child});

  @override
  Widget build(BuildContext context) {
    final location = GoRouterState.of(context).uri.path;
    final showBar = !location.startsWith('/library/pdf');

    return Scaffold(
      body: child,
      bottomNavigationBar: showBar
          ? NavigationBar(
              selectedIndex: _indexFor(location),
              onDestinationSelected: (i) =>
                  context.go(_routes[i]),
              destinations: const [
                NavigationDestination(
                    icon: Icon(Icons.menu_book_outlined),
                    selectedIcon: Icon(Icons.menu_book),
                    label: 'Recipes'),
                NavigationDestination(
                    icon: Icon(Icons.soup_kitchen_outlined),
                    selectedIcon: Icon(Icons.soup_kitchen),
                    label: 'Cooking'),
                NavigationDestination(
                    icon: Icon(Icons.inventory_2_outlined),
                    selectedIcon: Icon(Icons.inventory_2),
                    label: 'Inventory'),
                NavigationDestination(
                    icon: Icon(Icons.science_outlined),
                    selectedIcon: Icon(Icons.science),
                    label: 'Reference'),
                NavigationDestination(
                    icon: Icon(Icons.library_books_outlined),
                    selectedIcon: Icon(Icons.library_books),
                    label: 'Library'),
              ],
            )
          : null,
    );
  }

  static const _routes = [
    '/recipes',
    '/cooking',
    '/inventory',
    '/reference',
    '/library',
  ];

  static int _indexFor(String path) {
    for (var i = 0; i < _routes.length; i++) {
      if (path.startsWith(_routes[i])) return i;
    }
    return 0;
  }
}

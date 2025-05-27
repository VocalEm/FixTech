<?php

namespace App\Core;

class Router
{
    protected $routes = [];

    public function get($route, $controllerAction)
    {
        $this->addRoute('GET', $route, $controllerAction);
    }

    public function post($route, $controllerAction)
    {
        $this->addRoute('POST', $route, $controllerAction);
    }

    protected function addRoute($method, $route, $controllerAction)
    {
        // Convertimos las llaves {param} a una expresión regular con nombres
        preg_match_all('#\{([a-zA-Z_][a-zA-Z0-9_]*)\}#', $route, $paramNames);
        $paramOrder = $paramNames[1] ?? [];

        // Convertimos la ruta a una regex para extraer los valores
        $pattern = preg_replace('#\{[a-zA-Z_][a-zA-Z0-9_]*\}#', '([^/]+)', $route);
        $pattern = "#^" . rtrim($pattern, '/') . "$#";

        $this->routes[] = [
            'method' => $method,
            'pattern' => $pattern,
            'controllerAction' => $controllerAction,
            'paramOrder' => $paramOrder
        ];
    }

    public function dispatch()
    {
        $uri = rtrim(parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH), '/');
        $method = $_SERVER['REQUEST_METHOD'];

        foreach ($this->routes as $route) {
            if ($method === $route['method'] && preg_match($route['pattern'], $uri, $matches)) {
                // Extraer controlador y método
                [$controller, $action] = explode('@', $route['controllerAction']);
                $controllerClass = 'App\\Controllers\\' . $controller;
                $controllerInstance = new $controllerClass();

                // Extraer los parámetros en orden (omitimos el match completo)
                array_shift($matches); // quitamos el índice 0 (match completo)
                $params = $matches;

                // Llamar al método pasando los parámetros ordenados
                call_user_func_array([$controllerInstance, $action], $params);
                return;
            }
        }

        // Si no se encontró ruta
        http_response_code(404);
        echo "Página no encontrada ";
        Header('Location: /');
    }
}

<?php

// Iniciar la sesión si no está iniciada
session_start();

// Cargar el autoload de Composer para las clases
require_once __DIR__ . '/../vendor/autoload.php';

// Cargar el archivo de rutas y el enrutador
$router = require_once __DIR__ . '/../app/routes.php';

// Ejecutar la ruta
$router->dispatch();

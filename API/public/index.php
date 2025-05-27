<?php
require_once __DIR__ . '/../vendor/autoload.php';

$dotenv = Dotenv\Dotenv::createImmutable(__DIR__ . '/../');
$dotenv->load();

// Cargar el archivo de rutas y el enrutador
$router = require_once __DIR__ . '/../app/routes.php';

// Ejecutar la ruta
$router->dispatch();

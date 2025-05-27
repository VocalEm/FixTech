<?php

use App\Core\Router;

$router = new Router();


$router->post('/registro', 'InicioSesionController@registroUsuario'); // muestra pagina de producto
$router->post('/login', 'InicioSesionController@loginUsuario');
$router->post('/crearpublicacion', 'PublicacionController@crear');
$router->get('/publicaciones', 'PublicacionController@obtenerTodas');
$router->get('/publicaciones/usuario/{id}', 'PublicacionController@obtenerPorUsuario');
$router->get('/borradores/usuario/{id}', 'PublicacionController@obtenerBorradoresPorUsuario');
$router->post('/borrador/actualizar', 'PublicacionController@actualizar');
$router->get('/usuario/{id}', 'UsuarioController@obtener');
$router->post('/usuario/actualizar', 'UsuarioController@actualizar');
$router->get('/publicaciones/buscar/{query}', 'PublicacionController@buscar');
$router->post('/publicacion/eliminar', 'PublicacionController@eliminar');






return $router;

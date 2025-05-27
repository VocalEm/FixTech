<?php

namespace App\Controllers;

use App\Models\Publicacion;

class PublicacionController
{
    public function crear()
    {
        // Validar campos
        $campos = ['titulo', 'descripcion', 'id_usuario', 'is_borrador'];
        foreach ($campos as $campo) {
            if (!isset($_POST[$campo])) {
                http_response_code(400);
                echo json_encode(['error' => "Falta el campo: $campo"]);
                return;
            }
        }

        // Validar imagen
        if (!isset($_FILES['imagen']) || $_FILES['imagen']['error'] !== UPLOAD_ERR_OK) {
            http_response_code(400);
            echo json_encode(['error' => 'No se envió una imagen válida']);
            return;
        }

        // Guardar imagen
        $nombreOriginal = basename($_FILES['imagen']['name']);
        $nombreFinal = uniqid('pub_') . "_" . $nombreOriginal;
        $rutaDestino = __DIR__ . '/../../public/uploads/' . $nombreFinal;
        $rutaRelativa = 'uploads/' . $nombreFinal;

        if (!move_uploaded_file($_FILES['imagen']['tmp_name'], $rutaDestino)) {
            http_response_code(500);
            echo json_encode(['error' => 'No se pudo guardar la imagen']);
            return;
        }

        // Preparar datos
        $datos = [
            'titulo' => $_POST['titulo'],
            'descripcion' => $_POST['descripcion'],
            'id_usuario' => $_POST['id_usuario'],
            'imagen' => $rutaRelativa,
            'is_borrador' => $_POST['is_borrador'],
        ];

        $modelo = new Publicacion();
        $exito = $modelo->crear($datos);

        if ($exito) {
            http_response_code(201);
            echo json_encode(['mensaje' => 'Publicación creada correctamente']);
        } else {
            http_response_code(500);
            echo json_encode(['error' => 'Error al guardar en la base de datos']);
        }
    }

    public function obtenerTodas()
    {
        $modelo = new \App\Models\Publicacion();
        $publicaciones = $modelo->obtenerPublicacionesReales();

        header('Content-Type: application/json');
        echo json_encode($publicaciones);
    }

    public function obtenerPorUsuario($id)
    {
        $modelo = new \App\Models\Publicacion();
        $publicaciones = $modelo->obtenerPorUsuario($id);

        header('Content-Type: application/json');
        echo json_encode($publicaciones);
    }

    public function obtenerBorradoresPorUsuario($id)
    {
        $modelo = new \App\Models\Publicacion();
        $borradores = $modelo->obtenerBorradoresPorUsuario($id);

        header('Content-Type: application/json');
        echo json_encode($borradores);
    }

    public function actualizar()
    {
        $campos = ['id', 'titulo', 'descripcion', 'id_usuario', 'is_borrador'];

        foreach ($campos as $campo) {
            if (!isset($_POST[$campo])) {
                http_response_code(400);
                echo json_encode(['error' => "Falta el campo: $campo"]);
                return;
            }
        }

        $id = $_POST['id'];

        // Manejo de imagen
        $imagenFinal = $_POST['imagen_actual'] ?? null;

        if (isset($_FILES['imagen']) && $_FILES['imagen']['error'] === UPLOAD_ERR_OK) {
            $nombreOriginal = basename($_FILES['imagen']['name']);
            $nombreFinal = uniqid('pub_') . "_" . $nombreOriginal;
            $rutaDestino = __DIR__ . '/../../public/uploads/' . $nombreFinal;
            $imagenFinal = 'uploads/' . $nombreFinal;

            if (!move_uploaded_file($_FILES['imagen']['tmp_name'], $rutaDestino)) {
                http_response_code(500);
                echo json_encode(['error' => 'No se pudo guardar la nueva imagen']);
                return;
            }
        }

        if (empty($imagenFinal)) {
            http_response_code(400);
            echo json_encode(['error' => 'No se proporcionó imagen']);
            return;
        }

        $datos = [
            'id' => $id,
            'titulo' => $_POST['titulo'],
            'descripcion' => $_POST['descripcion'],
            'imagen' => $imagenFinal,
            'id_usuario' => $_POST['id_usuario'],
            'is_borrador' => $_POST['is_borrador']
        ];

        $modelo = new \App\Models\Publicacion();
        $exito = $modelo->actualizar($datos);

        if ($exito) {
            http_response_code(200);
            echo json_encode(['mensaje' => 'Borrador actualizado correctamente']);
        } else {
            http_response_code(500);
            echo json_encode(['error' => 'Error al actualizar en la base de datos']);
        }
    }
    public function buscar($query)
    {
        $modelo = new \App\Models\Publicacion();
        $resultados = $modelo->buscar($query);

        header('Content-Type: application/json');
        echo json_encode($resultados);
    }

    public function eliminar()
    {
        if (!isset($_POST['id'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Falta el ID de la publicación']);
            return;
        }

        $id = $_POST['id'];
        $modelo = new \App\Models\Publicacion();

        $exito = $modelo->eliminar($id);
        if ($exito) {
            http_response_code(200);
            echo json_encode(['mensaje' => 'Publicación eliminada']);
        } else {
            http_response_code(500);
            echo json_encode(['error' => 'Error al eliminar']);
        }
    }
}

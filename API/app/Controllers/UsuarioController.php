<?php

namespace App\Controllers;

use App\Models\Usuario;

class UsuarioController
{
    public function obtener($id)
    {
        $modelo = new \App\Models\Usuario();
        $usuario = $modelo->obtenerPorId($id);

        if ($usuario) {
            header('Content-Type: application/json');
            echo json_encode($usuario);
        } else {
            http_response_code(404);
            echo json_encode(['error' => 'Usuario no encontrado']);
        }
    }
    public function actualizar()
    {
        $campos = ['id', 'nickname', 'password'];
        foreach ($campos as $campo) {
            if (!isset($_POST[$campo])) {
                http_response_code(400);
                echo json_encode(['error' => "Falta el campo: $campo"]);
                return;
            }
        }

        $datos = [
            'id' => $_POST['id'],
            'nickname' => $_POST['nickname'],
            'password' => $_POST['password'],
            'foto_perfil' => null
        ];

        if (isset($_FILES['foto_perfil']) && $_FILES['foto_perfil']['error'] === UPLOAD_ERR_OK) {
            $nombreOriginal = basename($_FILES['foto_perfil']['name']);
            $nombreFinal = uniqid('foto_') . "_" . $nombreOriginal;
            $rutaDestino = __DIR__ . '/../../public/uploads/' . $nombreFinal;
            $rutaRelativa = 'uploads/' . $nombreFinal;

            if (!move_uploaded_file($_FILES['foto_perfil']['tmp_name'], $rutaDestino)) {
                http_response_code(500);
                echo json_encode(['error' => 'No se pudo guardar la imagen']);
                return;
            }

            $datos['foto_perfil'] = $rutaRelativa;
        }

        $modelo = new \App\Models\Usuario();
        $exito = $modelo->actualizar($datos);

        if ($exito) {
            http_response_code(200);
            echo json_encode(['mensaje' => 'Perfil actualizado']);
        } else {
            http_response_code(500);
            echo json_encode(['error' => 'Error al actualizar usuario']);
        }
    }
}

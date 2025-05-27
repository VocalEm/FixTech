<?php

namespace App\Controllers;

use App\Models\Usuario;

class InicioSesionController
{
    public function registroUsuario()
    {
        // Leer campos enviados por Retrofit como form-data
        $input = $_POST;

        $campos = ['nombre', 'apellido', 'email', 'username', 'password', 'telefono'];
        foreach ($campos as $campo) {
            if (empty($input[$campo])) {
                http_response_code(400);
                echo json_encode(['error' => "Falta el campo: $campo"]);
                return;
            }
        }

        // Validar que venga un archivo
        if (!isset($_FILES['imagen']) || $_FILES['imagen']['error'] !== UPLOAD_ERR_OK) {
            http_response_code(400);
            echo json_encode(['error' => 'Imagen no enviada o con error']);
            return;
        }

        // Preparar imagen
        $imagenTmp = $_FILES['imagen']['tmp_name'];
        $nombreOriginal = basename($_FILES['imagen']['name']);
        $nombreImagenFinal = uniqid('img_') . "_" . $nombreOriginal;

        $rutaDestino = __DIR__ . '/../../public/uploads/' . $nombreImagenFinal;
        $rutaRelativa = 'uploads/' . $nombreImagenFinal;

        // Mover la imagen al directorio
        if (!move_uploaded_file($imagenTmp, $rutaDestino)) {
            http_response_code(500);
            echo json_encode(['error' => 'No se pudo guardar la imagen']);
            return;
        }

        // Agregar el nombre de la imagen al array de datos
        $input['foto_perfil'] = $rutaRelativa;

        // Crear usuario
        $usuarioModel = new Usuario();
        $resultado = $usuarioModel->crearUsuario($input);

        if ($resultado) {
            http_response_code(201);
            echo json_encode(['mensaje' => 'Usuario registrado correctamente']);
        } else {
            http_response_code(500);
            echo json_encode(['error' => 'Error al registrar usuario']);
        }
    }

    public function loginUsuario()
    {
        // Verifica que se envíen los campos requeridos
        if (empty($_POST['email']) || empty($_POST['password'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Faltan campos obligatorios']);
            return;
        }

        $email = $_POST['email'];
        $password = $_POST['password'];

        $usuarioModel = new \App\Models\Usuario();
        $usuario = $usuarioModel->obtenerUsuarioPorCorreo($email);

        if (!$usuario) {
            http_response_code(401);
            echo json_encode(['error' => 'Correo no registrado']);
            return;
        }

        if (!password_verify($password, $usuario['PASSW'])) {
            http_response_code(401);
            echo json_encode(['error' => 'Contraseña incorrecta']);
            return;
        }

        // Eliminar el hash de la contraseña antes de enviarlo de vuelta
        unset($usuario['PASSW']);

        http_response_code(200);
        echo json_encode([
            'mensaje' => 'Inicio de sesión exitoso',
            'usuario' => $usuario
        ]);
    }
}

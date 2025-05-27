<?php

namespace App\Models;

class Usuario extends BaseModel
{

    public function encontrarPorCorreoONickname($identificador)
    {
        $query = "SELECT * FROM USUARIO WHERE CORREO = :identificador OR USERNAME = :identificador LIMIT 1";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':identificador', $identificador);
        $stmt->execute();
        return $stmt->fetch(\PDO::FETCH_ASSOC);
    }

    public function registrar($datos)
    {
        // Asegurar que privacidad tenga un valor válido
        $datos['privacidad'] = isset($datos['privacidad']) ? (int)$datos['privacidad'] : 0;

        $query = "INSERT INTO USUARIO (
            NOMBRE, APELLIDO_P, APELLIDO_M, SEXO, CORREO, USERNAME, PASSW,
            ROL, IMAGEN, FECHA_NACIMIENTO, PRIVACIDAD, ES_SUPERADMIN
        ) VALUES (
            :nombre, :apellido_p, :apellido_m, :sexo, :correo, :username, :passw,
            :rol, :imagen, :fecha_nacimiento, :privacidad, 0
        )";

        $stmt = $this->db->prepare($query);

        $stmt->bindParam(':nombre', $datos['nombre']);
        $stmt->bindParam(':apellido_p', $datos['apellido_p']);
        $stmt->bindParam(':apellido_m', $datos['apellido_m']);
        $stmt->bindParam(':sexo', $datos['sexo']);
        $stmt->bindParam(':correo', $datos['correo']);
        $stmt->bindParam(':username', $datos['username']);
        $stmt->bindParam(':passw', $datos['passw']);
        $stmt->bindParam(':rol', $datos['rol']);
        $stmt->bindParam(':imagen', $datos['imagen']);
        $stmt->bindParam(':fecha_nacimiento', $datos['fecha_nacimiento']);
        $stmt->bindParam(':privacidad', $datos['privacidad'], \PDO::PARAM_BOOL);

        return $stmt->execute();
    }

    public function obtenerPorId($id) {}
}

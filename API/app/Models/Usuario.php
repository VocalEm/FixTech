<?php

namespace App\Models;

class Usuario extends BaseModel
{
    public function crearUsuario($datos)
    {
        try {
            // Vaciar datos
            $nombre = $datos['nombre'];
            $apellidos = $datos['apellido'];
            $nickname = $datos['username'];
            $correo = $datos['email'];
            $passw = password_hash($datos['password'], PASSWORD_DEFAULT);
            $telefono = $datos['telefono'];
            $fotoPerfil = $datos['foto_perfil']; // nuevo campo

            $query = "INSERT INTO tbl_usuarios 
                        (NOMBRE, APELLIDOS, NICKNAME, CORREO, PASSW, TELEFONO, FOTO_PERFIL)
                      VALUES 
                        (:nombre, :apellidos, :nickname, :correo, :passw, :telefono, :foto_perfil)";

            $stmt = $this->db->prepare($query);
            $stmt->bindParam(':nombre', $nombre);
            $stmt->bindParam(':apellidos', $apellidos);
            $stmt->bindParam(':nickname', $nickname);
            $stmt->bindParam(':correo', $correo);
            $stmt->bindParam(':passw', $passw);
            $stmt->bindParam(':telefono', $telefono);
            $stmt->bindParam(':foto_perfil', $fotoPerfil); // nuevo bind

            return $stmt->execute();
        } catch (\PDOException $e) {
            error_log("Error al crear usuario: " . $e->getMessage());
            return false;
        }
    }

    public function obtenerUsuarioPorCorreo($correo)
    {
        $query = "SELECT * FROM tbl_usuarios WHERE CORREO = :correo LIMIT 1";
        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':correo', $correo);
        $stmt->execute();
        return $stmt->fetch(\PDO::FETCH_ASSOC);
    }

    public function obtenerPorId($id)
    {
        $query = "SELECT ID as id, NOMBRE as nombre,
         APELLIDOS as apellidos, CORREO as correo,
        TELEFONO as telefono, FOTO_PERFIL as foto_perfil, NICKNAME as nickname
              FROM tbl_usuarios
              WHERE ID = :id";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        return $stmt->fetch(\PDO::FETCH_ASSOC);
    }

    public function actualizar($datos)
    {
        try {
            $query = "UPDATE tbl_usuarios 
                  SET NICKNAME = :nickname,
                      PASSW = :password"
                . ($datos['foto_perfil'] ? ", FOTO_PERFIL = :foto_perfil" : "") . "
                  WHERE ID = :id";

            $stmt = $this->db->prepare($query);
            $stmt->bindParam(':id', $datos['id']);
            $stmt->bindParam(':nickname', $datos['nickname']);
            $stmt->bindParam(':password', password_hash($datos['password'], PASSWORD_DEFAULT));
            if ($datos['foto_perfil']) {
                $stmt->bindParam(':foto_perfil', $datos['foto_perfil']);
            }

            return $stmt->execute();
        } catch (\PDOException $e) {
            error_log("Error al actualizar usuario: " . $e->getMessage());
            return false;
        }
    }
}

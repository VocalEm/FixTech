<?php

namespace App\Models;

class Publicacion extends BaseModel
{
    public function crear($datos)
    {
        try {
            $titulo = $datos['titulo'];
            $descripcion = $datos['descripcion'];
            $imagen = $datos['imagen'];
            $id_usuario = $datos['id_usuario'];
            $is_borrador = $datos['is_borrador'];

            $query = "INSERT INTO tbl_publicaciones 
                        (TITULO, DESCRIPCION, IMAGEN, ID_USUARIO, IS_BORRADOR)
                        VALUES (:titulo, :descripcion, :imagen, :id_usuario, :is_borrador)";

            $stmt = $this->db->prepare($query);
            $stmt->bindParam(':titulo', $titulo);
            $stmt->bindParam(':descripcion', $descripcion);
            $stmt->bindParam(':imagen', $imagen);
            $stmt->bindParam(':id_usuario', $id_usuario);
            $stmt->bindParam(':is_borrador', $is_borrador);

            return $stmt->execute();
        } catch (\PDOException $e) {
            error_log("Error al crear publicación: " . $e->getMessage());
            return false;
        }
    }

    public function obtenerPublicacionesReales()
    {
        $query = "SELECT p.ID as id, p.TITULO as titulo, p.DESCRIPCION as descripcion,
        p.IMAGEN as imagen, p.FECHA_CREACION as fecha_creacion, u.NICKNAME  AS autor, 
        u.ID as id_usuario, u.FOTO_PERFIL as foto_perfil
              FROM tbl_publicaciones p
              INNER JOIN tbl_usuarios u ON u.ID = p.ID_USUARIO
              WHERE IS_BORRADOR = 0
              ORDER BY p.FECHA_CREACION DESC";

        $stmt = $this->db->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(\PDO::FETCH_ASSOC);
    }

    public function obtenerPorUsuario($idUsuario)
    {
        $query = "SELECT 
                p.ID AS id,
                p.TITULO AS titulo,
                p.DESCRIPCION AS descripcion,
                p.IMAGEN AS imagen,
                p.FECHA_CREACION AS fecha_creacion,
                p.ID_USUARIO AS id_usuario,
                u.NICKNAME AS autor,
                u.FOTO_PERFIL AS foto_perfil
              FROM tbl_publicaciones p
              INNER JOIN tbl_usuarios u ON u.ID = p.ID_USUARIO
              WHERE p.ID_USUARIO = :id AND p.IS_BORRADOR = 0
              ORDER BY p.FECHA_CREACION DESC";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':id', $idUsuario);
        $stmt->execute();
        return $stmt->fetchAll(\PDO::FETCH_ASSOC);
    }

    public function obtenerBorradoresPorUsuario($idUsuario)
    {
        $query = "SELECT 
                p.ID AS id,
                p.TITULO AS titulo,
                p.DESCRIPCION AS descripcion,
                p.IMAGEN AS imagen,
                p.FECHA_CREACION AS fecha_creacion,
                u.NICKNAME AS autor
              FROM tbl_publicaciones p JOIN tbl_usuarios u ON u.ID = p.ID_USUARIO
              WHERE p.ID_USUARIO = :id AND p.IS_BORRADOR = 1
              ORDER BY p.FECHA_CREACION DESC";

        $stmt = $this->db->prepare($query);
        $stmt->bindParam(':id', $idUsuario);
        $stmt->execute();
        return $stmt->fetchAll(\PDO::FETCH_ASSOC);
    }

    public function actualizar($datos)
    {
        try {
            $query = "UPDATE tbl_publicaciones
                  SET TITULO = :titulo,
                      DESCRIPCION = :descripcion,
                      IMAGEN = :imagen,
                      ID_USUARIO = :id_usuario,
                      IS_BORRADOR = :is_borrador,
                      FECHA_CREACION = NOW()
                  WHERE ID = :id";

            $stmt = $this->db->prepare($query);
            $stmt->bindParam(':titulo', $datos['titulo']);
            $stmt->bindParam(':descripcion', $datos['descripcion']);
            $stmt->bindParam(':imagen', $datos['imagen']);
            $stmt->bindParam(':id_usuario', $datos['id_usuario']);
            $stmt->bindParam(':is_borrador', $datos['is_borrador']);
            $stmt->bindParam(':id', $datos['id']);

            return $stmt->execute();
        } catch (\PDOException $e) {
            error_log("Error al actualizar publicación: " . $e->getMessage());
            return false;
        }
    }
    public function buscar($query)
    {
        $query = '%' . $query . '%';
        $sql = "SELECT p.ID as id, p.TITULO as titulo, p.DESCRIPCION as descripcion, p.IMAGEN as imagen, 
        p.FECHA_CREACION as fecha_creacion, u.NICKNAME AS autor, u.FOTO_PERFIL AS foto_perfil, u.ID AS id_usuario
            FROM tbl_publicaciones p
            INNER JOIN tbl_usuarios u ON p.ID_USUARIO = u.ID
            WHERE IS_BORRADOR = 0 AND (p.TITULO LIKE :query OR p.DESCRIPCION LIKE :query)
            ORDER BY p.FECHA_CREACION DESC";

        $stmt = $this->db->prepare($sql);
        $stmt->bindParam(':query', $query);
        $stmt->execute();
        return $stmt->fetchAll(\PDO::FETCH_ASSOC);
    }

    public function eliminar($id)
    {
        $sql = "DELETE FROM tbl_publicaciones WHERE ID = :id";
        $stmt = $this->db->prepare($sql);
        $stmt->bindParam(':id', $id);
        return $stmt->execute();
    }
}

<?php

namespace App\Models;

use App\Core\Database;

class BaseModel
{
    protected $db;

    public function __construct()
    {
        $this->db = Database::getInstance()->getConnection();
    }
}

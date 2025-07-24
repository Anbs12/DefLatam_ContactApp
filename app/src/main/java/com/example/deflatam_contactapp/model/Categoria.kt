package com.example.deflatam_contactapp.model


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una categoría para organizar los contactos.
 * Cada instancia es una fila en la tabla 'categorias'.
 */
@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
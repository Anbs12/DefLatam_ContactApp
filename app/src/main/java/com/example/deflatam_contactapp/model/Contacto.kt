package com.example.deflatam_contactapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Representa la entidad de un contacto en la base de datos.
 * Cada instancia de esta clase es una fila en la tabla 'contactos'.
 */
@Entity(
    tableName = "contactos",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["categoriaId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nombre: String,
    var telefono: String,
    var email: String?,
    var categoriaId: Int?
)
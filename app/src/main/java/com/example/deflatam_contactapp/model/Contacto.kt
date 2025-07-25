package com.example.deflatam_contactapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Representa un contacto en la base de datos.
 */
@Entity(
    tableName = "contactos",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["categoria_id"],
        onDelete = ForeignKey.SET_NULL // Si se borra una categoría, el campo en contacto se pone a null
    )]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,

    val telefono: String,

    val email: String?,

    @ColumnInfo(name = "categoria_id", index = true)
    val categoriaId: Int?
)
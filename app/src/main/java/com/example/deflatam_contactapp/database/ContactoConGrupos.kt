package com.example.deflatam_contactapp.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.model.Grupo

/**
 * Representa un Contacto y la lista de Grupos a los que pertenece.
 */
data class ContactoConGrupos(
    @Embedded val contacto: Contacto,
    @Relation(
        parentColumn = "id",
        entity = Grupo::class,
        entityColumn = "id",
        associateBy = Junction(
            value = ContactoGrupoCrossRef::class,
            parentColumn = "contactoId",
            entityColumn = "grupoId"
        )
    )
    val grupos: List<Grupo>
)
package com.example.deflatam_contactapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.deflatam_contactapp.model.Contacto


/**
 * Objeto de Acceso a Datos (DAO) para la entidad Contacto.
 */
@Dao
interface ContactoDao {

    /**
     * Inserta un nuevo contacto en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contacto: Contacto)

    /**
     * Actualiza un contacto existente en la base de datos.
     */
    @Update
    suspend fun update(contacto: Contacto)

    /**
     * Elimina un contacto de la base de datos.
     */
    @Delete
    suspend fun delete(contacto: Contacto)

    /**
     * Obtiene todos los contactos ordenados por nombre.
     */
    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun getAllContactos(): LiveData<List<Contacto>>

    /**
     * Busca contactos cuyo nombre o teléfono coincidan con la consulta.
     */
    @Query("SELECT * FROM contactos WHERE nombre LIKE :searchQuery OR telefono LIKE :searchQuery ORDER BY nombre ASC")
    fun searchContactos(searchQuery: String): LiveData<List<Contacto>>

    /**
     * Obtiene todos los contactos para una copia de seguridad (no como LiveData).
     */
    @Query("SELECT * FROM contactos")
    suspend fun getAllContactosForBackup(): List<Contacto>

    /**
     * Obtiene un contacto unico por su ID.
     */
    @Query("SELECT * FROM contactos WHERE id = :contactoId")
    fun getContactoById(contactoId: Int): LiveData<Contacto>

    /** Method para filtrar contactos por el ID de la categoría ---*/
    @Query("SELECT * FROM contactos WHERE categoria_id = :categoriaId ORDER BY nombre ASC")
    fun getContactosPorCategoria(categoriaId: Int): LiveData<List<Contacto>>
}


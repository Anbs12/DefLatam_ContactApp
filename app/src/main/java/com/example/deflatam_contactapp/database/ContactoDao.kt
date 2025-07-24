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
 * Define las operaciones de base de datos para la entidad Contacto.
 * Permite realizar consultas, inserciones, actualizaciones y eliminaciones.
 */
@Dao
interface ContactoDao {

    /**
     * Inserta un nuevo contacto en la base de datos.
     * Si el contacto ya existe, la operación se ignora.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarContacto(contacto: Contacto)

    /**
     * Actualiza un contacto existente en la base de datos.
     * La búsqueda del contacto se hace por su clave primaria.
     */
    @Update
    suspend fun actualizarContacto(contacto: Contacto)

    /**
     * Elimina un contacto de la base de datos.
     * La búsqueda del contacto se hace por su clave primaria.
     */
    @Delete
    suspend fun eliminarContacto(contacto: Contacto)

    /**
     * Obtiene todos los contactos ordenados alfabéticamente.
     * Retorna un LiveData para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodosLosContactos(): LiveData<List<Contacto>>

    /**
     * Busca contactos cuyo nombre coincida con la consulta.
     * La búsqueda no distingue mayúsculas de minúsculas.
     */
    @Query("SELECT * FROM contactos WHERE nombre LIKE :query ORDER BY nombre ASC")
    fun buscarContactos(query: String): LiveData<List<Contacto>>
}

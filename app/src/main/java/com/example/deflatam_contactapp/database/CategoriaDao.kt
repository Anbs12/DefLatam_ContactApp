package com.example.deflatam_contactapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deflatam_contactapp.model.Categoria

/**
 * Define las operaciones de base de datos para la entidad Categoria.
 * Permite realizar consultas e inserciones de categorías.
 */
@Dao
interface CategoriaDao {

    /**
     * Inserta una nueva categoría en la base de datos.
     * Si la categoría ya existe, se ignora.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCategoria(categoria: Categoria)

    /**
     * Obtiene todas las categorías ordenadas alfabéticamente.
     * Retorna un LiveData para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodasLasCategorias(): LiveData<List<Categoria>>
}
package com.example.deflatam_contactapp.repository


import androidx.lifecycle.LiveData
import com.example.deflatam_contactapp.database.CategoriaDao
import com.example.deflatam_contactapp.database.ContactoDao
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto

/**
 * Repositorio que maneja el acceso a los datos de contactos y categorías.
 * Abstrae las fuentes de datos del resto de la aplicación.
 */
class ContactosRepository(
    private val contactoDao: ContactoDao,
    private val categoriaDao: CategoriaDao
) {

    // LiveData que expone la lista de todos los contactos.
    val todosLosContactos: LiveData<List<Contacto>> = contactoDao.obtenerTodosLosContactos()

    // LiveData que expone la lista de todas las categorías.
    val todasLasCategorias: LiveData<List<Categoria>> = categoriaDao.obtenerTodasLasCategorias()

    /**
     * Inserta un nuevo contacto en la base de datos.
     * Se ejecuta en una corrutina para no bloquear el hilo principal.
     */
    suspend fun insertarContacto(contacto: Contacto) {
        contactoDao.insertarContacto(contacto)
    }

    /**
     * Actualiza un contacto existente.
     * Se ejecuta en una corrutina.
     */
    suspend fun actualizarContacto(contacto: Contacto) {
        contactoDao.actualizarContacto(contacto)
    }

    /**
     * Elimina un contacto de la base de datos.
     * Se ejecuta en una corrutina.
     */
    suspend fun eliminarContacto(contacto: Contacto) {
        contactoDao.eliminarContacto(contacto)
    }

    /**
     * Inserta una nueva categoría.
     * Se ejecuta en una corrutina.
     */
    suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insertarCategoria(categoria)
    }

    /**
     * Realiza una búsqueda de contactos por nombre.
     * Retorna un LiveData con los resultados de la búsqueda.
     */
    fun buscarContactos(query: String): LiveData<List<Contacto>> {
        return contactoDao.buscarContactos("%$query%")
    }
}

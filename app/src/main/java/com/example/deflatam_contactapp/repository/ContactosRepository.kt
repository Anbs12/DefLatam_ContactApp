package com.example.deflatam_contactapp.repository


import androidx.lifecycle.LiveData
import com.example.deflatam_contactapp.database.CategoriaDao
import com.example.deflatam_contactapp.database.ContactoDao
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto


/**
 * Repositorio que maneja las operaciones de datos para los contactos y categorías.
 */
class ContactosRepository(
    private val contactoDao: ContactoDao,
    private val categoriaDao: CategoriaDao
) {

    /**
     * Obtiene todos los contactos como un objeto LiveData.
     */
    val todosLosContactos: LiveData<List<Contacto>> = contactoDao.getAllContactos()

    /**
     * Obtiene todas las categorías como un objeto LiveData.
     */
    val todasLasCategorias: LiveData<List<Categoria>> = categoriaDao.getAllCategorias()

    /**
     * Busca contactos que coincidan con la consulta de búsqueda.
     */
    fun buscarContactos(query: String): LiveData<List<Contacto>> {
        return contactoDao.searchContactos("%$query%")
    }

    /**
     * Inserta un nuevo contacto en la base de datos.
     */
    suspend fun insertarContacto(contacto: Contacto) {
        contactoDao.insert(contacto)
    }

    /**
     * Actualiza un contacto existente en la base de datos.
     */
    suspend fun actualizarContacto(contacto: Contacto) {
        contactoDao.update(contacto)
    }

    /**
     * Elimina un contacto de la base de datos.
     */
    suspend fun eliminarContacto(contacto: Contacto) {
        contactoDao.delete(contacto)
    }

    /**
     * Inserta una nueva categoría en la base de datos.
     */
    suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insert(categoria)
    }


    /**
     * Obtiene todos los contactos para realizar una copia de seguridad.
     */
    suspend fun obtenerContactosParaBackup(): List<Contacto> {
        return contactoDao.getAllContactosForBackup()
    }

    /**
     * Restaura los contactos a partir de una lista.
     */
    suspend fun restaurarContactos(contactos: List<Contacto>) {
        contactos.forEach { contacto ->
            contactoDao.insert(contacto)
        }
    }

    /**
     * Obtiene un contacto por su ID desde la Bd.
     */
    fun getContactoById(contactoId: Int): LiveData<Contacto> {
        return contactoDao.getContactoById(contactoId)
    }

}
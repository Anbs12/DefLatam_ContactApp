package com.example.deflatam_contactapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.deflatam_contactapp.database.ContactosDatabase
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal que muestra la lista de contactos.
 * Gestiona la lógica de negocio, los estados de UI y la comunicación con el repositorio.
 */
class ContactosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactosRepository

    // LiveData privado para manejar la fuente de datos (búsqueda o lista completa)
    private val _fuenteDeContactos = MutableLiveData<LiveData<List<Contacto>>>()

    // LiveData público que la UI observará.
    // Usamos switchMap para reaccionar a los cambios en la fuente de datos.
    val contactos: LiveData<List<Contacto>> = _fuenteDeContactos.switchMap { it }

    // LiveData para manejar el estado de la búsqueda (si está activa o no)
    private val _busquedaActiva = MutableLiveData<Boolean>(false)
    val busquedaActiva: LiveData<Boolean> get() = _busquedaActiva

    init {
        val contactoDao = ContactosDatabase.getDatabase(application).contactoDao()
        val categoriaDao = ContactosDatabase.getDatabase(application).categoriaDao()
        repository = ContactosRepository(contactoDao, categoriaDao)

        // Al iniciar, mostramos todos los contactos
        _fuenteDeContactos.value = repository.todosLosContactos
    }

    /**
     * Elimina un contacto de la base de datos.
     * La operación se realiza en una corrutina en segundo plano.
     */
    fun eliminarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }

    /**
     * Filtra la lista de contactos según una consulta de búsqueda.
     * Actualiza la fuente de datos que `contactos` está observando.
     */
    fun buscarContactos(query: String) {
        if (query.isBlank()) {
            _fuenteDeContactos.value = repository.todosLosContactos
            _busquedaActiva.value = false
        } else {
            _fuenteDeContactos.value = repository.buscarContactos(query)
            _busquedaActiva.value = true
        }
    }
}

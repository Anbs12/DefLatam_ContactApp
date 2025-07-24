package com.example.deflatam_contactapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.deflatam_contactapp.database.ContactosDatabase
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de agregar o editar un contacto.
 * Gestiona la lógica para guardar y notifica a la UI cuando ha terminado.
 */
class AgregarContactoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactosRepository

    // LiveData para notificar a la Activity que la operación terminó y puede cerrarse.
    private val _operacionFinalizada = MutableLiveData<Boolean>()
    val operacionFinalizada: LiveData<Boolean> get() = _operacionFinalizada

    init {
        val contactoDao = ContactosDatabase.getDatabase(application).contactoDao()
        val categoriaDao = ContactosDatabase.getDatabase(application).categoriaDao()
        repository = ContactosRepository(contactoDao, categoriaDao)
        _operacionFinalizada.value = false // Inicializamos en falso
    }

    /**
     * Inserta un nuevo contacto en la base de datos.
     * Al finalizar, notifica a la UI.
     */
    fun insertarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.insertarContacto(contacto)
        _operacionFinalizada.postValue(true)
    }

    /**
     * Actualiza un contacto existente en la base de datos.
     * Al finalizar, notifica a la UI.
     */
    fun actualizarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.actualizarContacto(contacto)
        _operacionFinalizada.postValue(true)
    }
}

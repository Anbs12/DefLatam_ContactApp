package com.example.deflatam_contactapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import kotlinx.coroutines.launch


/**
 * ViewModel para la pantalla de agregar o editar un contacto.
 */
class AgregarContactoViewModel(private val repository: ContactosRepository) : ViewModel() {

    /**
     * LiveData que expone la lista de todas las categorías disponibles.
     */
    val todasLasCategorias: LiveData<List<Categoria>> = repository.todasLasCategorias

    private val _estadoGuardado = MutableLiveData<Result<Unit>>()
    /**
     * LiveData para observar el resultado de la operación de guardado.
     */
    val estadoGuardado: LiveData<Result<Unit>> = _estadoGuardado


    /**
     * Inserta un nuevo contacto en la base de datos.
     */
    fun insertarContacto(contacto: Contacto) = viewModelScope.launch {
        try {
            repository.insertarContacto(contacto)
            _estadoGuardado.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    /**
     * Actualiza un contacto existente en la base de datos.
     */
    fun actualizarContacto(contacto: Contacto) = viewModelScope.launch {
        try {
            repository.actualizarContacto(contacto)
            _estadoGuardado.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    /**
     * Obtiene un contacto por su ID.
     */
    fun getContactoById(contactoId: Int): LiveData<Contacto> {
        return repository.getContactoById(contactoId)
    }
}

/**
 * Fábrica para crear una instancia de AgregarContactoViewModel con dependencias.
 */
class AgregarContactoViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarContactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarContactoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

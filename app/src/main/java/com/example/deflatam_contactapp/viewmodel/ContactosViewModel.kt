package com.example.deflatam_contactapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import kotlinx.coroutines.launch


/**
 * ViewModel para gestionar la lista principal de contactos.
 */
class ContactosViewModel(private val repository: ContactosRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>("")

    /**
     * LiveData que expone la lista de contactos, se actualiza según la búsqueda.
     */
    val contactos: LiveData<List<Contacto>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.todosLosContactos
        } else {
            repository.buscarContactos(query)
        }
    }

    /**
     * Inicia una búsqueda de contactos.
     */
    fun buscarContacto(query: String) {
        _searchQuery.value = query
    }

    /**
     * Elimina un contacto de la base de datos.
     */
    fun eliminarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }
}

/**
 * Fábrica para crear una instancia de ContactosViewModel con dependencias.
 */
class ContactosViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
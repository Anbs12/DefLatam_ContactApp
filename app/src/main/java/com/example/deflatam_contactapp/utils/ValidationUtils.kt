package com.example.deflatam_contactapp.utils


import android.util.Patterns

/**
 * Objeto de utilidad para validar campos de formulario.
 * Contiene funciones estáticas para validar diferentes tipos de datos.
 */
object ValidationUtils {

    /**
     * Valida que el nombre no esté vacío.
     * Retorna true si el nombre es válido, de lo contrario false.
     */
    fun isNombreValido(nombre: String): Boolean {
        return nombre.isNotBlank()
    }

    /**
     * Valida que el teléfono no esté vacío y sea un número de teléfono válido.
     * Retorna true si el teléfono es válido.
     */
    fun isTelefonoValido(telefono: String): Boolean {
        return telefono.isNotBlank() && Patterns.PHONE.matcher(telefono).matches()
    }

    /**
     * Valida que el email tenga un formato de correo electrónico correcto.
     * Retorna true si el email es válido o si está vacío (opcional).
     */
    fun isEmailValido(email: String): Boolean {
        // El email puede ser opcional, por lo que si está vacío, es válido.
        return email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

package com.example.deflatam_contactapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.deflatam_contactapp.databinding.ActivityAgregarContactoBinding
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.utils.ValidationUtils
import com.example.deflatam_contactapp.viewmodel.AgregarContactoViewModel

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarContactoBinding
    private val agregarViewModel: AgregarContactoViewModel by viewModels()
    private var contactoActual: Contacto? = null

    companion object {
        const val EXTRA_CONTACTO_ID = "extra_contacto_id"
        // Deberías implementar la lógica para obtener el contacto por ID
        // en tu ViewModel o Repositorio si vas a editar.
        // Por simplicidad aquí, asumiremos que se pasa el objeto completo.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Por ahora, creamos un nuevo objeto Contacto para el binding.
        // En un caso real de "editar", buscarías el contacto por ID
        // y lo asignarías a `contactoActual`.
        contactoActual = Contacto(nombre = "", telefono = "", email = "", categoriaId = null)
        binding.contacto = contactoActual

        binding.buttonGuardar.setOnClickListener {
            guardarContacto()
        }

        // Observar si la operación de guardado finalizó para cerrar la actividad
        agregarViewModel.operacionFinalizada.observe(this) { finalizada ->
            if (finalizada) {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad y vuelve a la lista
            }
        }
    }

    private fun guardarContacto() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val telefono = binding.editTextTelefono.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()

        if (!ValidationUtils.isNombreValido(nombre)) {
            binding.editTextNombre.error = "El nombre es obligatorio"
            return
        }
        if (!ValidationUtils.isTelefonoValido(telefono)) {
            binding.editTextTelefono.error = "El teléfono no es válido"
            return
        }
        if (!ValidationUtils.isEmailValido(email)) {
            binding.editTextEmail.error = "El email no es válido"
            return
        }

        // Actualizar el objeto `contactoActual` con los datos del formulario
        contactoActual?.apply {
            this.nombre = nombre
            this.telefono = telefono
            this.email = email
        }

        // Llamar al ViewModel para insertar o actualizar
        // (Aquí faltaría la lógica para diferenciar si es nuevo o existente)
        agregarViewModel.insertarContacto(contactoActual!!)
    }
}

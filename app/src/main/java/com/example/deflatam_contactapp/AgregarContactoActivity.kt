package com.example.deflatam_contactapp

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.deflatam_contactapp.database.ContactosDatabase
import com.example.deflatam_contactapp.databinding.ActivityAgregarContactoBinding
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import com.example.deflatam_contactapp.utils.ValidationUtils
import com.example.deflatam_contactapp.viewmodel.AgregarContactoViewModel
import com.example.deflatam_contactapp.viewmodel.AgregarContactoViewModelFactory

/**
 * Actividad para crear o editar un contacto.
 */
class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarContactoBinding
    private var contactoId: Int? = null
    private var contactoExistente : Contacto? = null

    private var listaCategorias = listOf<Categoria>()
    private var categoriaSeleccionadaId: Int = -1

    private val viewModel: AgregarContactoViewModel by viewModels {
        val database = ContactosDatabase.getDatabase(context = applicationContext, coroutineScope = lifecycleScope)
        val repository = ContactosRepository(database.contactoDao(), database.categoriaDao())
        AgregarContactoViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactoId = intent.getIntExtra(EXTRA_CONTACTO_ID, -1).takeIf { it != -1 }

        setupViews()
        setupListeners()
        observeViewModel()
        setupSpinner()
        observarCategorias()
    }

    /**
     * Configura las vistas, cargando datos si se está editando un contacto.
     */
    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        if (contactoId != null) {
            title = "Editar Contacto"
            binding.textViewTitulo.text = title
            binding.buttonGuardar.text = "Actualizar contacto"
            //Observamos el contacto
            viewModel.getContactoById(contactoId!!).observe(this) { contacto ->
                contactoExistente = contacto //Actualizamos var externa

                if (contacto != null) {
                    binding.editTextNombre.setText(contacto.nombre)
                    binding.editTextTelefono.setText(contacto.telefono)
                    binding.editTextEmail.setText(contacto.email)
                } else {
                    Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } else {
            title = "Agregar Contacto"
            binding.textViewTitulo.text = title
        }
    }

    /**
     * Configura el listener del botón de guardar.
     */
    private fun setupListeners() {
        binding.buttonGuardar.setOnClickListener {
            guardarContacto()
        }
    }

    /**
     * Observa los LiveData del ViewModel.
     */
    private fun observeViewModel() {
        viewModel.estadoGuardado.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Valida los campos y guarda el contacto.
     */
    private fun guardarContacto() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val telefono = binding.editTextTelefono.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val categoriaId = categoriaSeleccionadaId

        // Limpia errores previos
        binding.textFieldNombre.error = null
        binding.textFieldTelefono.error = null
        binding.textFieldEmail.error = null

        // Realiza la validación
        if (!ValidationUtils.isNombreValido(nombre)) {
            binding.textFieldNombre.error = "El nombre es obligatorio"
            return
        }
        if (!ValidationUtils.isTelefonoValido(telefono)) {
            binding.textFieldTelefono.error = "El teléfono es obligatorio"
            return
        }
        if (!ValidationUtils.isEmailValido(email)) {
            binding.textFieldEmail.error = "Email no válido"
            return
        }

        // Crea el objeto Contacto y lo guarda
        val contacto = Contacto(
            id = contactoId ?: 0, // Si es nuevo, el id es 0 y Room lo autogenera
            nombre = nombre,
            telefono = telefono,
            email = email,
            categoriaId = categoriaId
        )

        if (contactoId == null) {
            viewModel.insertarContacto(contacto)
        } else {
            viewModel.actualizarContacto(contacto)
        }
    }

    /**
     * Obtiene las categorías y las muestra en el Spinner.
     */
    private fun observarCategorias() {
        viewModel.todasLasCategorias.observe(this) { categorias ->
            categorias?.let {
                listaCategorias = it
                val nombresCategorias = it.map { cat -> cat.nombre }
                val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, nombresCategorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = adapter

                // Si estamos editando, pre-seleccionar la categoria correcta
                contactoExistente?.let { contacto ->
                    val categoriaDelContacto = listaCategorias.find { c -> c.id == contacto.categoriaId }
                    val posicion = listaCategorias.indexOf(categoriaDelContacto)
                    if (posicion != -1) {
                        binding.spinnerCategoria.setSelection(posicion)
                    }
                }
            }
        }
    }

    /**
     * Configura el listener del Spinner para obtener la categoría seleccionada.
     */
    private fun setupSpinner() {
        binding.spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaCategorias.isNotEmpty()) {
                    categoriaSeleccionadaId = listaCategorias[position].id
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Obtiene el ID del contacto de la actividad anterior.
     */
    companion object {
        const val EXTRA_CONTACTO_ID = "EXTRA_CONTACTO_ID"
    }
}

package com.example.deflatam_contactapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deflatam_contactapp.adapter.ContactosAdapter
import com.example.deflatam_contactapp.databinding.ActivityMainBinding
import com.example.deflatam_contactapp.viewmodel.ContactosViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contactosViewModel: ContactosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Asignar el viewModel a la variable de DataBinding en el XML
        binding.viewModel = contactosViewModel
        // Hacer que el binding observe el ciclo de vida de la Activity
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)

        // Configurar el RecyclerView y su adaptador
        val adapter = ContactosAdapter { contacto ->
            // Acción al hacer clic en un contacto: abrir para editar
            val intent = Intent(this, AgregarContactoActivity::class.java)
            intent.putExtra(AgregarContactoActivity.EXTRA_CONTACTO_ID, contacto.id)
            // Aquí deberías pasar el objeto completo o su ID para recuperarlo
            // en la siguiente actividad. Pasar el ID es más eficiente.
            startActivity(intent)
        }
        binding.recyclerViewContactos.adapter = adapter
        binding.recyclerViewContactos.layoutManager = LinearLayoutManager(this)

        // Observar la lista de contactos del ViewModel
        contactosViewModel.contactos.observe(this) { contactos ->
            contactos?.let {
                adapter.submitList(it)
            }
        }

        // Configurar el botón flotante
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, AgregarContactoActivity::class.java)
            startActivity(intent)
        }

        // Configurar la barra de búsqueda
        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactosViewModel.buscarContactos(newText.orEmpty())
                return true
            }
        })
    }
}

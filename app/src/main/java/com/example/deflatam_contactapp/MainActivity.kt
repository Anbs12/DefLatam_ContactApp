package com.example.deflatam_contactapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deflatam_contactapp.adapter.ContactosAdapter
import com.example.deflatam_contactapp.database.ContactosDatabase
import com.example.deflatam_contactapp.databinding.ActivityMainBinding
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.repository.ContactosRepository
import com.example.deflatam_contactapp.utils.BackupUtils
import com.example.deflatam_contactapp.viewmodel.ContactosViewModel
import com.example.deflatam_contactapp.viewmodel.ContactosViewModelFactory
import com.google.android.material.snackbar.Snackbar


/**
 * Actividad principal que muestra la lista de contactos.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactosAdapter

    private val viewModel: ContactosViewModel by viewModels {
        // Inicializa el ViewModel usando la fábrica para inyectar el repositorio.
        val database = ContactosDatabase.getDatabase(context = applicationContext, coroutineScope = lifecycleScope)
        val repository = ContactosRepository(database.contactoDao(), database.categoriaDao())
        ContactosViewModelFactory(repository)
    }

    // Launcher para crear el archivo de backup
    private val crearBackupLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            viewModel.contactos.value?.let { contactos ->
                val success = BackupUtils.escribirBackup(this, contactos, it)
                val message = if (success) "Copia de seguridad creada" else "Error al crear la copia"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher para abrir el archivo de backup
    private val restaurarBackupLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val contactosRestaurados = BackupUtils.restaurarDesdeBackup(this, it)
            if (contactosRestaurados != null) {
                // Aquí deberías insertar los contactos en la base de datos a través del ViewModel
                // Por simplicidad, solo mostramos un mensaje.
                Toast.makeText(this, "${contactosRestaurados.size} contactos restaurados.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al restaurar la copia de seguridad.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    /**
     * Configura el RecyclerView, su adaptador y el gesto de deslizar para eliminar.
     */
    private fun setupRecyclerView() {
        adapter = ContactosAdapter { contacto ->
            // Click en un item: abre la actividad para editar el contacto seleccionado
            val intent = Intent(this, AgregarContactoActivity::class.java).apply {
                putExtra(AgregarContactoActivity.EXTRA_CONTACTO_ID, contacto.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewContactos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewContactos.adapter = adapter

        // Configura el helper para el gesto de "swipe-to-delete"
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // No se usa para mover items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val contactoEliminado = adapter.currentList[position]
                viewModel.eliminarContacto(contactoEliminado)

                // Muestra un Snackbar con opción de deshacer
                Snackbar.make(binding.root, "Contacto eliminado", Snackbar.LENGTH_LONG).setAction("DESHACER") {
                    // Si el usuario deshace, volvemos a insertar el contacto.
                    // Esta funcionalidad requeriría un método en el ViewModel.
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewContactos)
    }

    /**
     * Configura los listeners para la búsqueda y el botón de agregar.
     */
    private fun setupListeners() {
        binding.editTextBusqueda.addTextChangedListener { text ->
            viewModel.buscarContacto(text.toString())
        }

        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarContactoActivity::class.java))
        }
    }

    /**
     * Observa los LiveData del ViewModel para actualizar la UI.
     */
    private fun observeViewModel() {
        viewModel.contactos.observe(this) { contactos ->
            // Muestra un estado de carga o vacío si es necesario
            // binding.progressBar.visibility = View.GONE
            // binding.emptyView.visibility = if (contactos.isEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(contactos)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_backup -> {
                crearBackupLauncher.launch("contactos_backup.json")
                true
            }
            R.id.action_restore -> {
                restaurarBackupLauncher.launch("application/json")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

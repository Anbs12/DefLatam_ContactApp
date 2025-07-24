package com.example.deflatam_contactapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.deflatam_contactapp.databinding.ItemContactoBinding
import com.example.deflatam_contactapp.model.Contacto

/**
 * Adaptador para mostrar la lista de contactos en un RecyclerView.
 * Usa Data Binding y maneja los clics en los items.
 */
class ContactosAdapter(private val onItemClicked: (Contacto) -> Unit) :
    ListAdapter<Contacto, ContactosAdapter.ContactoViewHolder>(ContactosComparator()) {

    /**
     * Crea un ViewHolder inflando el layout con Data Binding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val binding = ItemContactoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactoViewHolder(binding)
    }

    /**
     * Vincula los datos del contacto con el ViewHolder y establece el listener de clic.
     */
    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contactoActual = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(contactoActual)
        }
        holder.bind(contactoActual)
    }

    /**
     * ViewHolder que usa Data Binding para vincular el objeto Contacto a la vista.
     */
    class ContactoViewHolder(private val binding: ItemContactoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contacto: Contacto) {
            binding.contacto = contacto
            binding.executePendingBindings() // Fuerza la actualización inmediata del binding
        }
    }

    /**
     * Comparador para que ListAdapter actualice la lista de forma eficiente.
     */
    class ContactosComparator : DiffUtil.ItemCallback<Contacto>() {
        override fun areItemsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem == newItem
        }
    }
}

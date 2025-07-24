package com.example.deflatam_contactapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto

/**
 * Clase principal de la base de datos de la aplicación.
 * Define las entidades y provee acceso a los DAOs.
 */
@Database(entities = [Contacto::class, Categoria::class], version = 1, exportSchema = false)
abstract class ContactosDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        // La instancia volátil asegura que sea actualizada para todos los hilos.
        @Volatile
        private var INSTANCE: ContactosDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         * Crea la base de datos si no existe.
         */
        fun getDatabase(context: Context): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_database"
                )
                    .fallbackToDestructiveMigration() // Usar con precaución en producción
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

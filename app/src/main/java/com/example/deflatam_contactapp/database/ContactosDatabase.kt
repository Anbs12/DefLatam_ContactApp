package com.example.deflatam_contactapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Clase principal de la base de datos Room para la aplicación.
 */
@Database(entities = [Contacto::class, Categoria::class], version = 1, exportSchema = false)
abstract class ContactosDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao
    abstract fun categoriaDao(): CategoriaDao

    private class ContactosDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        /**
         * Se llama cuando la base de datos es creada por primera vez.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.categoriaDao())
                }
            }
        }

        /**
         * Inserta categorías por defecto en la base de datos.
         */
        suspend fun populateDatabase(categoriaDao: CategoriaDao) {
            categoriaDao.insert(Categoria(nombre = "Familia"))
            categoriaDao.insert(Categoria(nombre = "Trabajo"))
            categoriaDao.insert(Categoria(nombre = "Amigos"))
            categoriaDao.insert(Categoria(nombre = "General"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ContactosDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         */
        fun getDatabase(context: Context, coroutineScope: CoroutineScope): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_database"
                )
                    .addCallback(ContactosDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


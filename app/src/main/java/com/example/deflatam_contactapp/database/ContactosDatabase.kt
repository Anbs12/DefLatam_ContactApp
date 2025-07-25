package com.example.deflatam_contactapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.deflatam_contactapp.model.Categoria
import com.example.deflatam_contactapp.model.Contacto
import com.example.deflatam_contactapp.model.Grupo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Clase principal de la base de datos Room para la aplicación.
 */
@Database(
    entities = [Contacto::class, Categoria::class, Grupo::class, ContactoGrupoCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class ContactosDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun grupoDao(): GrupoDao

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
                    populateDbGroups(database.grupoDao())
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

        /**
         * Inserta grupos por defecto en la base de datos.
         */
        suspend fun populateDbGroups(grupoDao: GrupoDao) {
            grupoDao.crearGrupo(Grupo(nombre = "Grupo 1"))
            grupoDao.crearGrupo(Grupo(nombre = "Grupo 2"))
            grupoDao.crearGrupo(Grupo(nombre = "Grupo 3"))
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

/*
    // Ejemplo Migración de la versión 2 a la 3 ---
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Crear la tabla de grupos
            database.execSQL("CREATE TABLE IF NOT EXISTS `grupos_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL)")
            // Crear la tabla de unión
            database.execSQL("CREATE TABLE IF NOT EXISTS `contacto_grupo_cross_ref` (`contactoId` INTEGER NOT NULL, `grupoId` INTEGER NOT NULL, PRIMARY KEY(`contactoId`, `grupoId`))")
        }
    }*/


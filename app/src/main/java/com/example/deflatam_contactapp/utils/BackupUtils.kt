package com.example.deflatam_contactapp.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.deflatam_contactapp.database.ContactosDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Objeto de utilidad para gestionar la copia de seguridad y restauración
 * de la base de datos Room de la aplicación.
 */
object BackupUtils {

    // El nombre de tu base de datos definido en ContactosDatabase
    private const val DATABASE_NAME = "contactos_database"
    private const val BACKUP_FOLDER_NAME = "ContactosAppBackup"

    /**
     * Crea una copia de seguridad de la base de datos de Room.
     * La copia se guarda en el directorio público de Descargas.
     * NOTA: Requiere permisos de almacenamiento en versiones antiguas de Android.
     *
     * @param context El contexto de la aplicación.
     */
    fun backupDatabase(context: Context) {
        // Cerramos la instancia actual de la base de datos para asegurar que todos los datos
        // pendientes se escriban en el archivo principal (.db).
        ContactosDatabase.getDatabase(context).close()

        val dbFile = context.getDatabasePath(DATABASE_NAME)
        val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BACKUP_FOLDER_NAME)

        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }

        val backupFile = File(backupDir, dbFile.name)

        try {
            copyFile(FileInputStream(dbFile), FileOutputStream(backupFile))
            Toast.makeText(context, "Copia de seguridad creada en: ${backupFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al crear la copia de seguridad", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Restaura la base de datos desde una copia de seguridad.
     * Sobrescribe la base de datos actual con la del archivo de backup.
     * NOTA: La aplicación DEBE reiniciarse para que los cambios surtan efecto.
     *
     * @param context El contexto de la aplicación.
     */
    fun restoreDatabase(context: Context) {
        ContactosDatabase.getDatabase(context).close()

        val dbFile = context.getDatabasePath(DATABASE_NAME)
        val backupDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BACKUP_FOLDER_NAME)
        val backupFile = File(backupDir, dbFile.name)

        if (!backupFile.exists()) {
            Toast.makeText(context, "No se encontró el archivo de copia de seguridad", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            copyFile(FileInputStream(backupFile), FileOutputStream(dbFile))
            Toast.makeText(context, "Restauración completa. Reinicia la aplicación.", Toast.LENGTH_LONG).show()
            // ¡Importante! Se debe forzar el cierre de la app para que la nueva DB se cargue.
            // System.exit(0)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al restaurar la base de datos", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Función de ayuda para copiar un archivo de un origen a un destino.
     */
    @Throws(IOException::class)
    private fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        fromFile.channel.use { fromChannel ->
            toFile.channel.use { toChannel ->
                fromChannel.transferTo(0, fromChannel.size(), toChannel)
            }
        }
    }
}

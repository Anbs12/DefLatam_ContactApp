package com.example.deflatam_contactapp.utils

import com.example.deflatam_contactapp.model.Contacto


/**
 * Objeto de utilidad para exportar contactos al formato VCard (.vcf).
 */
object VCardUtils {

    /**
     * Convierte una lista de contactos a un único string en formato VCard.
     * @param contactos La lista de contactos a exportar.
     * @return Un String que contiene todos los contactos en formato .vcf.
     */
    fun exportToVCard(contactos: List<Contacto>): String {
        val vcardBuilder = StringBuilder()
        for (contacto in contactos) {
            vcardBuilder.append("BEGIN:VCARD\n")
            vcardBuilder.append("VERSION:3.0\n")

            // Nombre formateado (obligatorio)
            vcardBuilder.append("FN:${contacto.nombre}\n")

            // Teléfono
            if (contacto.telefono.isNotBlank()) {
                vcardBuilder.append("TEL;TYPE=CELL:${contacto.telefono}\n")
            }

            // Email
            if (contacto.email != null) {
                if (contacto.email.isNotBlank()) {
                    vcardBuilder.append("EMAIL:${contacto.email}\n")
                }
            }else{
                vcardBuilder.append("EMAIL;\n")
            }

            //LInkedin
            if (contacto.linkedin != null) {
                vcardBuilder.append("Linkedin:https://www.linkedin.com/in/${contacto.linkedin}\n")
            }else{
                vcardBuilder.append("Linkedin;\n")
            }

            //Web site
            if (contacto.website != null){
                vcardBuilder.append("website:${contacto.website}\n")
            }else{
                vcardBuilder.append("website;\n")
            }

            vcardBuilder.append("END:VCARD\n\n") // Doble salto de línea para separar entradas
        }
        return vcardBuilder.toString()
    }
}

package com.ges.vehiclegate.util.pdf

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object PdfOpener {

    fun ouvrir(context: Context, fichier: File) {

        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            fichier
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        context.startActivity(intent)
    }
}

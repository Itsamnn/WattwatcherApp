package com.wattswatcher.app.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.io.FileWriter

object BillDownloadHelper {
    
    /**
     * Save bill content to a file and show download notification
     */
    fun saveBillToFile(context: Context, billContent: String): Boolean {
        return try {
            val fileName = "WattsWatcher_Bill_${System.currentTimeMillis()}.txt"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                writer.write(billContent)
            }
            
            // Show success toast
            Toast.makeText(
                context, 
                "Bill saved to: ${file.absolutePath}", 
                Toast.LENGTH_LONG
            ).show()
            
            // Optionally share the file
            shareFile(context, file)
            
            true
        } catch (e: Exception) {
            Toast.makeText(
                context, 
                "Failed to save bill: ${e.message}", 
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }
    
    /**
     * Share the bill file
     */
    private fun shareFile(context: Context, file: File) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "WattsWatcher Electricity Bill")
                putExtra(Intent.EXTRA_TEXT, "Please find attached electricity bill from WattsWatcher app.")
                putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                ))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "Share Bill"))
        } catch (e: Exception) {
            // Fallback to just copying to clipboard
            Toast.makeText(context, "Bill copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}
package cz.skaut.srs.ticketsreader.processor

import android.app.AlertDialog
import android.content.Context

abstract class QrProcessor(
    protected val context: Context,
    var dialogActive: Boolean = false,
) {
    abstract fun process(value: String)

    fun showAlertDialog(message: String?) {
        AlertDialog.Builder(context).setMessage(message).setTitle("Chyba")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss(); dialogActive = false; }.show()
    }
}
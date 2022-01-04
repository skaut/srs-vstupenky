package cz.skaut.srs.ticketsreader.processor

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cz.skaut.srs.ticketsreader.R

abstract class QrProcessor(
    protected val context: Context,
    var processingActive: Boolean = false,
) {
    abstract fun process(value: String)

    protected fun showErrorDialog(message: String?) {
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setTitle(R.string.ticket_check_dialog_title_error)
            .setPositiveButton(R.string.common_ok) { dialog, _ ->
                dialog.dismiss(); processingActive = false;
            }
            .setCancelable(false)
            .show()
    }
}
package cz.skaut.srs.ticketsreader.processor

import android.app.Dialog
import android.view.Window
import androidx.fragment.app.FragmentActivity
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import kotlinx.coroutines.runBlocking

class TicketQrProcessor(context: FragmentActivity) : QrProcessor(context) {
    val apiClient = ApiClient()

    override fun process(value: String) {
        val ticketInfo: TicketCheckInfo
        runBlocking {
            ticketInfo = apiClient.checkTicket(value.toInt())
        }

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_ticket)
        dialog.show()
    }
}
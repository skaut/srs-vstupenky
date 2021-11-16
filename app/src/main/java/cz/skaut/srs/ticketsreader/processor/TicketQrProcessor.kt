package cz.skaut.srs.ticketsreader.processor

import android.app.AlertDialog
import android.content.Context
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.dto.TicketInfo
import kotlinx.coroutines.runBlocking

class TicketQrProcessor(context: Context) : QrProcessor(context) {
    val apiClient = ApiClient(context)

    override fun process(value: String) {
        val ticketInfo: TicketInfo
        runBlocking {
            ticketInfo = apiClient.getTicketInfo(value)
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val alert = builder.setMessage(ticketInfo.displayName).create()
        alert.show()
    }
}
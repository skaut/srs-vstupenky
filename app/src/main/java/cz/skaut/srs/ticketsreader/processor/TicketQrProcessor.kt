package cz.skaut.srs.ticketsreader.processor

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiException
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TicketQrProcessor(context: FragmentActivity) : QrProcessor(context) {

    override fun process(value: String) {
        val apiClient = ApiClient()

        try {
            val ticketInfo: TicketCheckInfo
            runBlocking {
                ticketInfo = apiClient.checkTicket(value.toInt())
            }

            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ticket, null)

            val tvName: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_name_text)
            val tvRoles: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_roles_text)
            val tvSubevents: TextView =
                dialogView.findViewById(R.id.dialog_ticket_tv_subevents_text)
            val tvChecks: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_checks_text)

            val dtf =
                DateTimeFormatter.ofPattern("d. M. yyyy H:mm:ss").withZone(ZoneId.systemDefault())

            tvName.text = ticketInfo.attendee_name
            tvRoles.text = ticketInfo.roles.joinToString(", ")
            tvSubevents.text = ticketInfo.subevents.map { it.name }.joinToString(", ")
            tvChecks.text =
                ticketInfo.subevent_checks.map { dtf.format(it.toJavaInstant()) }.joinToString("\n")
//            tlLayout.setBackgroundColor(if (ticketInfo.subevent_checks.isEmpty()) Color.GREEN else Color.RED)

            AlertDialog.Builder(context).setView(dialogView).setTitle("Info o vstupence")
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss(); dialogActive = false; }
                .show()
        } catch (e: NumberFormatException) {
            showAlertDialog("Nevalidni obsah QR")
        } catch (e: ApiException) {
            showAlertDialog(e.message)
        }
    }
}
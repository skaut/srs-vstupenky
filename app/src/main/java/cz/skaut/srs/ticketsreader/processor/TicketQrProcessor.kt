package cz.skaut.srs.ticketsreader.processor

import android.app.AlertDialog
import android.graphics.Color
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

            val tvStatus: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_status)
            val tvMessage: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_message)

            val tvName: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_name_text)
            val tvRoles: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_roles_text)
            val tvSubevents: TextView =
                dialogView.findViewById(R.id.dialog_ticket_tv_subevents_text)
            val tvChecks: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_checks_text)

            val dtf =
                DateTimeFormatter.ofPattern("d. M. yyyy H:mm:ss").withZone(ZoneId.systemDefault())

            val status: String
            val statusColor: Int
            val message: String?
            if (!ticketInfo.has_subevent) {
                status = "Neplatná"
                statusColor = Color.RED
                message = "Účastník nemá tuto podakci."
            } else if (ticketInfo.subevent_checks.isNotEmpty()) {
                status = "Použitá"
                statusColor = Color.RED
                message = "Vstupenka již byla dříve načtena."
            } else {
                status = "Platná"
                statusColor = Color.GREEN
                message = null
            }

            tvStatus.text = status
            tvStatus.setTextColor(statusColor)

            tvMessage.text = message
            tvMessage.visibility = if (message == null) TextView.GONE else TextView.VISIBLE

            tvName.text = ticketInfo.attendee_name
            tvRoles.text = ticketInfo.roles.joinToString(", ")
            tvSubevents.text = ticketInfo.subevents.map { it.name }.joinToString(", ")
            tvChecks.text =
                ticketInfo.subevent_checks.map { dtf.format(it.toJavaInstant()) }.joinToString("\n")

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
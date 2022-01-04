package cz.skaut.srs.ticketsreader.processor

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiException
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TicketQrProcessor(context: FragmentActivity) : QrProcessor(context) {
    val DATETIME_FORMATTER: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d. M. yyyy H:mm:ss").withZone(ZoneId.systemDefault())

    override fun process(value: String) {
        try {
            val apiClient = ApiClient()
            val ticketCheckInfo: TicketCheckInfo
            runBlocking {
                ticketCheckInfo = apiClient.checkTicket(value.toInt())
            }
            showTicketCheckInfoDialog(ticketCheckInfo)
        } catch (e: NumberFormatException) {
            showErrorDialog(context.getString(R.string.ticket_check_error_invalid_qr_code))
        } catch (e: ApiConfigException) {
            // todo: hlaska o chybejicim nastaveni
        } catch (e: ApiException) {
            showErrorDialog(e.message) // todo: extrakce z JSON
        }
    }

    private fun showTicketCheckInfoDialog(ticketInfo: TicketCheckInfo) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ticket, null)

        val tvStatus: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_status)
        val tvMessage: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_message)
        val tvName: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_name_text)
        val tvRoles: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_roles_text)
        val tvSubevents: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_subevents_text)
        val tvChecks: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_checks_text)

        if (!ticketInfo.has_subevent) {
            tvStatus.text = context.getString(R.string.ticket_check_status_invalid)
            tvStatus.setTextColor(Color.RED)
            tvMessage.text = context.getString(R.string.ticket_check_message_missing_subevent)
        } else if (ticketInfo.subevent_checks.isNotEmpty()) {
            tvStatus.text = context.getString(R.string.ticket_check_status_used)
            tvStatus.setTextColor(Color.RED)
            tvMessage.text = context.getString(R.string.ticket_check_message_used)
        } else {
            tvStatus.text = context.getString(R.string.ticket_check_status_valid)
            tvStatus.setTextColor(Color.GREEN)
            tvMessage.text = null
        }

        tvMessage.visibility = if (tvMessage.text == null) TextView.GONE else TextView.VISIBLE

        tvName.text = ticketInfo.attendee_name
        tvRoles.text = ticketInfo.roles.joinToString(", ")
        tvSubevents.text = ticketInfo.subevents
            .map { it.name }
            .joinToString(", ")
        tvChecks.text = ticketInfo.subevent_checks
            .map { DATETIME_FORMATTER.format(it.toJavaInstant()) }
            .joinToString("\n")

        MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setTitle(R.string.ticket_check_dialog_title_info)
            .setPositiveButton(R.string.common_ok) { dialog, _ ->
                dialog.dismiss(); processingActive = false;
            }
            .setCancelable(false)
            .show()
    }
}

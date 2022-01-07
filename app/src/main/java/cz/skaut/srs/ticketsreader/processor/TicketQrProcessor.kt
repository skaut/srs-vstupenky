package cz.skaut.srs.ticketsreader.processor

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiConnectionException
import cz.skaut.srs.ticketsreader.api.ApiErrorResponseException
import cz.skaut.srs.ticketsreader.api.ApiSerializationException
import cz.skaut.srs.ticketsreader.api.ApiUnknownErrorException
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant

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
            showErrorDialog(context.getString(R.string.dialog_error_message_invalid_ticket_qr))
        } catch (e: ApiConfigException) {
            showErrorDialog(R.string.dialog_error_message_api_config_error)
        } catch (e: ApiConnectionException) {
            showErrorDialog(R.string.dialog_error_message_api_connection_error)
        } catch (e: ApiUnknownErrorException) {
            showErrorDialog(R.string.dialog_error_message_api_unknown_error)
        } catch (e: ApiSerializationException) {
            showErrorDialog(R.string.dialog_error_message_api_serialization_error)
        } catch (e: ApiErrorResponseException) {
            showErrorDialog(e.message)
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
            tvStatus.text = context.getString(R.string.dialog_ticket_status_invalid)
            tvStatus.setTextColor(Color.RED)
            tvMessage.text = context.getString(R.string.dialog_ticket_message_missing_subevent)
        } else if (ticketInfo.subevent_checks.isNotEmpty()) {
            tvStatus.text = context.getString(R.string.dialog_ticket_status_used)
            tvStatus.setTextColor(Color.RED)
            tvMessage.text = context.getString(R.string.dialog_ticket_message_used)
        } else {
            tvStatus.text = context.getString(R.string.dialog_ticket_status_valid)
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
            .setTitle(R.string.dialog_ticket_title)
            .setPositiveButton(R.string.common_ok) { dialog, _ ->
                dialog.dismiss(); processingActive = false
            }
            .setCancelable(false)
            .show()
    }
}

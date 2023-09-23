package cz.skaut.srs.ticketsreader.processor

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cz.skaut.srs.ticketsreader.Preferences
import cz.skaut.srs.ticketsreader.R
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiConnectionException
import cz.skaut.srs.ticketsreader.api.ApiErrorResponseException
import cz.skaut.srs.ticketsreader.api.ApiSerializationException
import cz.skaut.srs.ticketsreader.api.ApiUnknownErrorException
import cz.skaut.srs.ticketsreader.api.dto.TicketCheckInfo
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.Locale

private const val COLOR_ORANGE = 0xffffa500;

class TicketQrProcessor(context: FragmentActivity) : QrProcessor(context) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val dateTimeFormatter: SimpleDateFormat =
        SimpleDateFormat("d. M. yyyy H:mm:ss", Locale.getDefault())

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
            log.debug(e.message)
        } catch (e: ApiConfigException) {
            showErrorDialog(R.string.dialog_error_message_api_config_error)
            log.debug(e.message)
        } catch (e: ApiConnectionException) {
            showErrorDialog(R.string.dialog_error_message_api_connection_error)
            log.debug(e.message)
        } catch (e: ApiUnknownErrorException) {
            showErrorDialog(R.string.dialog_error_message_api_unknown_error)
            log.debug(e.message)
        } catch (e: ApiSerializationException) {
            showErrorDialog(R.string.dialog_error_message_api_serialization_error)
            log.debug(e.message)
        } catch (e: ApiErrorResponseException) {
            showErrorDialog(e.message)
            log.debug(e.message)
        }
    }

    private fun showTicketCheckInfoDialog(ticketInfo: TicketCheckInfo) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ticket, null)

        val tvStatus: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_status)
        val tvMessage: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_message)
        val tvName: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_name_text)
        val tvAge: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_age_text)
        val ivPhoto: ImageView = dialogView.findViewById(R.id.dialog_ticket_iv_photo)
        val tvRoles: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_roles_text)
        val tvSubevents: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_subevents_text)
        val tvChecks: TextView = dialogView.findViewById(R.id.dialog_ticket_tv_checks_text)

        if (!ticketInfo.hasSubevent) {
            tvStatus.text = context.getString(R.string.dialog_ticket_status_invalid)
            tvStatus.setTextColor(Color.RED)
            tvMessage.text = context.getString(R.string.dialog_ticket_message_missing_subevent)
        } else if (ticketInfo.subeventChecks.isNotEmpty()) {
            tvStatus.text = context.getString(R.string.dialog_ticket_status_used)
            tvStatus.setTextColor(COLOR_ORANGE.toInt())
            tvMessage.text = context.getString(R.string.dialog_ticket_message_used)
        } else {
            tvStatus.text = context.getString(R.string.dialog_ticket_status_valid)
            tvStatus.setTextColor(Color.GREEN)
            tvMessage.text = null
        }

        tvMessage.visibility = if (tvMessage.text == null) TextView.GONE else TextView.VISIBLE

        tvName.text = ticketInfo.attendeeName
        tvAge.text = ticketInfo.attendeeAge.toString()

        if (ticketInfo.attendeePhoto != null) {
            ivPhoto.load("${Preferences.srsUrl}${ticketInfo.attendeePhoto}")
        }

        tvRoles.text = ticketInfo.roles.joinToString(", ")
        tvSubevents.text = ticketInfo.subevents
            .map { it.name }
            .joinToString(", ")
        tvChecks.text = ticketInfo.subeventChecks
            .map { dateTimeFormatter.format(it.toEpochMilliseconds()) }
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

package cz.skaut.srs.ticketsreader

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.ApiConfigException
import cz.skaut.srs.ticketsreader.api.ApiConnectionException
import cz.skaut.srs.ticketsreader.api.ApiErrorResponseException
import cz.skaut.srs.ticketsreader.api.ApiSerializationException
import cz.skaut.srs.ticketsreader.api.ApiUnknownErrorException
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.scanner.ScannerActivity
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class MainActivity : AppCompatActivity() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var btnConnectSrs: Button
    private lateinit var btnDisconnectSrs: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnScanTickets: Button
    private lateinit var spnSubevent: Spinner
    private lateinit var tvSeminarName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnectSrs = findViewById(R.id.activity_main_btn_connect_srs)
        btnDisconnectSrs = findViewById(R.id.activity_main_btn_disconnect_srs)
        btnRefresh = findViewById(R.id.activity_main_btn_refresh)
        btnScanTickets = findViewById(R.id.activity_main_btn_scan_tickets)
        spnSubevent = findViewById(R.id.activity_main_spn_subevent)
        tvSeminarName = findViewById(R.id.activity_main_tv_seminar_name_text)

        Preferences.init(this)
        updateUI()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        if (Preferences.connected) {
            btnConnectSrs.visibility = Button.GONE
            btnDisconnectSrs.visibility = Button.VISIBLE
            btnRefresh.visibility = Button.VISIBLE
            btnScanTickets.visibility = Button.VISIBLE
            tvSeminarName.text = Preferences.seminarName
        } else {
            btnConnectSrs.visibility = Button.VISIBLE
            btnDisconnectSrs.visibility = Button.GONE
            btnRefresh.visibility = Button.GONE
            btnScanTickets.visibility = Button.GONE
            tvSeminarName.text = getString(R.string.activity_main_tv_seminar_name_text_default)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_item, Preferences.subevents)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnSubevent.adapter = adapter
        spnSubevent.setSelection(Preferences.selectedSubeventPosition)
    }

    private fun setListeners() {
        btnConnectSrs.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("connection_mode", true)
            startActivity(intent)
        }

        btnDisconnectSrs.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.activity_main_btn_disconnect_srs_confirm_message)
                .setTitle(R.string.activity_main_btn_disconnect_srs_confirm_title)
                .setPositiveButton(R.string.common_yes) { dialog, _ ->
                    dialog.dismiss()
                    Preferences.removeConnectionInfo()
                    updateUI()
                }
                .setNegativeButton(R.string.common_no) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        btnRefresh.setOnClickListener {
            try {
                val apiClient = ApiClient()
                val seminarInfo: SeminarInfo
                runBlocking {
                    seminarInfo = apiClient.getSeminarInfo()
                }
                Preferences.setSeminarInfo(seminarInfo)
                updateUI()
            } catch (e: ApiConfigException) {
                showToast(R.string.dialog_error_message_api_config_error)
                log.debug(e.message)
            } catch (e: ApiConnectionException) {
                showToast(R.string.dialog_error_message_api_connection_error)
                log.debug(e.message)
            } catch (e: ApiUnknownErrorException) {
                showToast(R.string.dialog_error_message_api_unknown_error)
                log.debug(e.message)
            } catch (e: ApiSerializationException) {
                showToast(R.string.dialog_error_message_api_serialization_error)
                log.debug(e.message)
            } catch (e: ApiErrorResponseException) {
                showToast(e.message)
                log.debug(e.message)
            }
        }

        btnScanTickets.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("connection_mode", false)
            startActivity(intent)
        }

        spnSubevent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Preferences.setSelectedSubevent(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Preferences.setSelectedSubevent(0)
            }
        }
    }

    private fun showToast(message: Int) {
        showToast(getString(message))
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

package cz.skaut.srs.ticketsreader

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cz.skaut.srs.ticketsreader.api.ApiClient
import cz.skaut.srs.ticketsreader.api.dto.SeminarInfo
import cz.skaut.srs.ticketsreader.scanner.ScannerActivity
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
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

        btnConnectSrs.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("connection_mode", true)
            startActivity(intent)
        }

        btnDisconnectSrs.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val alert = builder.setMessage("Opravdu odpojit?")
                .setTitle("Odpojeni od SRS")
                .setPositiveButton("Ano") { dialog, _ ->
                    dialog.dismiss()
                    Preferences.removeConnectionInfo()
                    Preferences.removeSeminarInfo()
                    updateUI()
                }
                .setNegativeButton("Ne") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alert.show()
        }

        btnRefresh.setOnClickListener {
            val apiClient = ApiClient()

            val seminarInfo: SeminarInfo
            runBlocking {
                seminarInfo = apiClient.getSeminarInfo()
            }

            Preferences.setSeminarInfo(seminarInfo)

            updateUI()
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

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    fun updateUI() {
        btnConnectSrs.visibility = if (Preferences.connected) Button.GONE else Button.VISIBLE
        btnDisconnectSrs.visibility = if (Preferences.connected) Button.VISIBLE else Button.GONE
        btnRefresh.visibility = if (Preferences.connected) Button.VISIBLE else Button.GONE
        btnScanTickets.visibility = if (Preferences.connected) Button.VISIBLE else Button.GONE

        val adapter = ArrayAdapter(this, R.layout.spinner_item, Preferences.subevents)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spnSubevent.adapter = adapter
        spnSubevent.setSelection(Preferences.selectedSubeventPosition)

        tvSeminarName.text = Preferences.seminarName
            ?: getString(R.string.activity_main_tv_seminar_name_text_default)
    }
}
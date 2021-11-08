package cz.skaut.srs.ticketsreader

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cz.skaut.srs.ticketsreader.scanner.ScannerActivity

import android.app.AlertDialog


class MainActivity : AppCompatActivity() {
    private lateinit var btnConnectSrs: Button
    private lateinit var btnDisconnectSrs: Button
    private lateinit var btnScanTickets: Button
    private lateinit var tvConnectedEventName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnectSrs = findViewById(R.id.activity_main_btn_connect_srs)
        btnDisconnectSrs = findViewById(R.id.activity_main_btn_disconnect_srs)
        btnScanTickets = findViewById(R.id.activity_main_btn_scan_tickets)
        tvConnectedEventName = findViewById(R.id.activity_main_tv_connected_srs_name)

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
                    updateUI()
                }
                .setNegativeButton("Ne") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            alert.show()
        }

        btnScanTickets.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("connection_mode", false)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    fun updateUI() {
        btnConnectSrs.visibility = if (Preferences.srsConnected) Button.GONE else Button.VISIBLE
        btnDisconnectSrs.visibility = if (Preferences.srsConnected) Button.VISIBLE else Button.GONE
        btnScanTickets.visibility = if (Preferences.srsConnected) Button.VISIBLE else Button.GONE

        tvConnectedEventName.text = if (Preferences.srsConnected) Preferences.srsName else R.string.activity_main_tv_connected_srs_name_default.toString()
    }
}
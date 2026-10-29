package com.example.localisationapp

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigationevent.NavigationEventDispatcher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var txtLocalisation: TextView? = null
    private var btnLocalisation: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtLocalisation =
            findViewById<TextView?>(R.id.txtLocalisation)

        btnLocalisation =
            findViewById<Button?>(R.id.btnLocalisation)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        btnLocalisation!!.setOnClickListener(View.OnClickListener { v: View? ->
            verifierPermission()
        })
    }

    private fun verifierPermission() {
        val finePermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (finePermission || coarsePermission) {
            obtenirLocalisation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun obtenirLocalisation(getLatitude: Any.() -> Double) {
        val finePermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!finePermission && !coarsePermission) {
            verifierPermission()
            return
        }

        txtLocalisation!!.setText(
            "Recherche de la position..."
        )

        val cancellationTokenSource: CancellationTokenSource =
            CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            NavigationEventDispatcher.Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.getToken()
        ).addOnSuccessListener({ location ->
            if (location != null) {
                val latitude: Double = location.getLatitude()
                val longitude: Double = location.getLongitude()

                txtLocalisation!!.setText(
                    ("Latitude : " + latitude
                            + "\nLongitude : " + longitude)
                )
            } else {
                txtLocalisation!!.setText(
                    "Position indisponible."
                )

                Toast.makeText(
                    this,
                    "Activez la localisation et réessayez.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }).addOnFailureListener({ e ->
            txtLocalisation!!.setText(
                "Erreur lors de la récupération."
            )
            Toast.makeText(
                this,
                "Impossible de récupérer la position.",
                Toast.LENGTH_LONG
            ).show()
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == LOCATION_PERMISSION_CODE) {
            val finePermission =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            val coarsePermission =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (finePermission || coarsePermission) {
                obtenirLocalisation()
            } else {
                Toast.makeText(
                    this,
                    "Permission de localisation refusée.",
                    Toast.LENGTH_LONG
                ).show()

                txtLocalisation!!.setText(
                    "Autorisez la localisation pour continuer."
                )
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_CODE = 100
    }
}
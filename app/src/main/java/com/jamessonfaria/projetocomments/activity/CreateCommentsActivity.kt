package com.jamessonfaria.projetocomments.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.EditText
import com.github.rodlibs.persistencecookie.PersistentCookieStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.model.Comentario
import com.jamessonfaria.projetocomments.util.Network
import com.jamessonfaria.projetocomments.util.Util
import kotlinx.android.synthetic.main.activity_create_comments.*
import org.jetbrains.anko.*
import org.json.JSONArray

class CreateCommentsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    var mGoogleMap: GoogleMap? = null
    private var net: Network? = null
    protected var locationManager: LocationManager? = null
    private var map: SupportMapFragment? = null
    private val INITIAL_REQUEST = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comments)

        loadMap()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left_animation, R.anim.slide_out_right_animation)
    }

    fun postComment(view: View) {
        if(Util.isNetworkAvaliabe(this)) {

            var progress = indeterminateProgressDialog("Salvando Comentário...", null)
            val net = Network(this@CreateCommentsActivity)

            val comentario: Comentario = Comentario(0, txtNome.text.toString(), txtDescricao.text.toString(),
                    "","","","","")

            alert("Deseja criar o comentário ?", "Informação") {
                yesButton {

                    net.postComment(comentario, object : Network.HttpCallback {
                        override fun onSuccess(response: String) {
                            runOnUiThread {
                                progress.cancel()
                                alert("Comentário criado com sucesso.", null) {
                                    yesButton { finish() }
                                }.show()
                            }
                        }

                        override fun onFailure(response: String?, throwable: Throwable?) {
                            progress.cancel()
                            toast("ERRO")
                        }

                    })
                }

                noButton { progress.cancel() }
            }.show()
        }
    }

    // ############## Metodos do Mapa

    private fun loadMap() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager // pra obter a localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), INITIAL_REQUEST)
            }
            return
        }
        map = supportFragmentManager.findFragmentById(R.id.fragmentMapa) as SupportMapFragment
        map!!.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap?) {

        mGoogleMap = googleMap
        net = Network(this@CreateCommentsActivity)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val it = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(it, 851)
        } else {
            @SuppressLint("MissingPermission") val location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))

                mGoogleMap!!.addMarker(MarkerOptions()
                        .position(latLng))

            }
        }

        mGoogleMap!!.setMyLocationEnabled(true)
        mGoogleMap!!.getUiSettings().isMyLocationButtonEnabled = false

        mGoogleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)


//        if(comentario?.lat != null && comentario?.lng != null && !comentario?.lat.equals("") && !comentario?.lng.equals("")){
//
//            val latLng = LatLng(comentario!!.lat.toDouble(), comentario!!.lng.toDouble())
//
//            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
//            mGoogleMap!!.addMarker(MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)))
//            //mGoogleMap!!.setMyLocationEnabled(true)
//            mGoogleMap!!.getUiSettings().isMyLocationButtonEnabled = false
//            mGoogleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)
//        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            200 -> {
                if (grantResults.size == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    map = supportFragmentManager.findFragmentById(R.id.fragmentMapa) as SupportMapFragment
                    map!!.getMapAsync(this)
                }
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

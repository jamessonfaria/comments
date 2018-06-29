package com.jamessonfaria.projetocomments.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.rodlibs.persistencecookie.PersistentCookieStore
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
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
import com.kbeanie.imagechooser.api.*
import com.kbeanie.imagechooser.exceptions.ChooserException
import kotlinx.android.synthetic.main.activity_create_comments.*
import org.jetbrains.anko.*
import org.json.JSONArray
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

class CreateCommentsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, ImageChooserListener {

    var mGoogleMap: GoogleMap? = null
    private var net: Network? = null
    protected var locationManager: LocationManager? = null
    private var map: SupportMapFragment? = null
    private val INITIAL_REQUEST = 200
    var latLng: LatLng? = null
    private val REQUEST_PLACE_PICKER = 2

    internal var imageChooserManager: ImageChooserManager? = null
    internal var imgPhoto: ImageView? = null
    internal var img1Path = ""
    private val INTENT_CAMERA = 19
    internal var path_image: String? = null
    internal var dataFoto: String? = null
    internal var nameFoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comments)

        loadMap()
        imgPhoto = imageViewePhoto
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
                    "","","", latLng!!.latitude.toString(), latLng!!.longitude.toString())

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

    // ############## Metodos da Camera

    fun takePicture(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        } else {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            val folder = File(Environment.getExternalStorageDirectory().toString() + "/bichooser")
            if (!folder.exists()) {
                folder.mkdir()
            }

            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
            val dtt = java.util.Date()
            dataFoto = dateFormat.format(dtt)
            path_image = folder.absoluteFile.toString() + "/" + "Item" + "-" + dataFoto + ".jpg"

            val f = File(Environment.getExternalStorageDirectory(), "temp.jpg")
            val intent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent2.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
            startActivityForResult(intent2, INTENT_CAMERA)
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == REQUEST_PLACE_PICKER) { // MAPA
            if (resultCode == Activity.RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(this, data)
                //                this.onPlaceSelected(place);

                latLng = LatLng(place.latLng.latitude, place.latLng.longitude)

                mGoogleMap!!.addMarker(MarkerOptions()
                        .position(latLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)))
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(this, data)
            }
        } else if (requestCode == INTENT_CAMERA && resultCode == Activity.RESULT_OK) { // CAMERA

            val settings = getSharedPreferences("image", 0)
            nameFoto = settings.getString("nameFoto", "")
            //            alterou = true;

            try {
                val f = File(Environment.getExternalStorageDirectory(), "temp.jpg")

                var ei: ExifInterface? = null
                ei = ExifInterface(f.absolutePath)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                var cameraBitmapOrig: Bitmap? = rotateImage(f.absolutePath, orientation)
                System.gc()

                val originalHeight = cameraBitmapOrig!!.height.toDouble()
                val scale = originalHeight / 600

                val wi = (cameraBitmapOrig.width / scale).toInt()
                val hey = (cameraBitmapOrig.height / scale).toInt()
                var cameraBitmapResized: Bitmap? = Bitmap.createScaledBitmap(cameraBitmapOrig, wi, hey, true)


                try {
                    f.delete()
                    val folder = File(Environment.getExternalStorageDirectory().toString() + "/bichooser")
                    if (!folder.exists()) {
                        folder.mkdir()
                    }

                    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
                    val dtt = java.util.Date()
                    dataFoto = dateFormat.format(dtt)
                    path_image = folder.absoluteFile.toString() + "/" + "Item" + "-" + dataFoto + ".jpg"

                    var fos: FileOutputStream? = FileOutputStream(path_image)
                    cameraBitmapResized!!.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                    fos!!.flush()
                    fos.close()
                    fos = null
                    cameraBitmapResized.recycle()
                    cameraBitmapResized = null
                    cameraBitmapOrig.recycle()
                    cameraBitmapOrig = null


                    System.gc()
                    addImageToGallery(path_image!!, this@CreateCommentsActivity)



                    imgPhoto!!.setVisibility(View.VISIBLE)
                    img1Path = path_image as String
                    Glide.with(this@CreateCommentsActivity).load(File(path_image)).into(imgPhoto)


                } catch (e: FileNotFoundException) {

                } catch (e: IOException) {

                }

            } catch (e: Exception) {

            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == ChooserType.REQUEST_PICK_PICTURE) {
            imageChooserManager!!.submit(requestCode, data)
        }

    }


    fun rotateImage(photoPath: String, orientation: Int): Bitmap {
        var angle = 90f

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270f
            ExifInterface.ORIENTATION_UNDEFINED -> return BitmapFactory.decodeFile(photoPath)
        }

        val retVal: Bitmap
        val source = BitmapFactory.decodeFile(photoPath)

        val matrix = Matrix()
        matrix.postRotate(angle)
        retVal = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return retVal
    }

    fun addImageToGallery(filePath: String, context: Context) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.MediaColumns.DATA, filePath)
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun gallery(view: View) {
        try {
            imageChooserManager = ImageChooserManager(this@CreateCommentsActivity, ChooserType.REQUEST_PICK_PICTURE)
            imageChooserManager!!.setImageChooserListener(this@CreateCommentsActivity)
            imageChooserManager!!.choose()
        } catch (e: ChooserException) {
            e.printStackTrace()
        }

    }

    override fun onImageChosen(chosenImage: ChosenImage?) {
        runOnUiThread {
            if (chosenImage != null) {
                imgPhoto!!.setVisibility(View.VISIBLE)
                img1Path = chosenImage.getFilePathOriginal()
                Glide.with(this@CreateCommentsActivity).load(File(chosenImage.getFilePathOriginal())).into(imgPhoto)
            }
        }
    }

    override fun onImagesChosen(p0: ChosenImages?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                latLng = LatLng(location.latitude, location.longitude)
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))

                mGoogleMap!!.addMarker(MarkerOptions()
                        .position(latLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)))

            }
        }

        mGoogleMap!!.setMyLocationEnabled(true)
        mGoogleMap!!.getUiSettings().isMyLocationButtonEnabled = false

        mGoogleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL)

        mGoogleMap!!.setOnMapClickListener(GoogleMap.OnMapClickListener { latLnNew ->
            mGoogleMap!!.addMarker(MarkerOptions()
                    .position(latLnNew)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)))

            latLng = latLnNew

        })

    }

    fun callPlaces(view: View) {
        val intent: Intent
        try {
            intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this@CreateCommentsActivity)
            startActivityForResult(intent, REQUEST_PLACE_PICKER)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_PLACE_PICKER) {
//            if (resultCode == Activity.RESULT_OK) {
//                val place = PlaceAutocomplete.getPlace(this, data)
//                //                this.onPlaceSelected(place);
//
//                latLng = LatLng(place.latLng.latitude, place.latLng.longitude)
//
//                mGoogleMap!!.addMarker(MarkerOptions()
//                        .position(latLng!!)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)))
//                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f))
//
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                val status = PlaceAutocomplete.getStatus(this, data)
//            }
//        }
//    }

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

    override fun onLocationChanged(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

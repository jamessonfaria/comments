package com.jamessonfaria.projetocomments.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.adapter.AdapterComentarios
import com.jamessonfaria.projetocomments.model.Comentario
import com.jamessonfaria.projetocomments.util.Network
import com.jamessonfaria.projetocomments.util.Util
import kotlinx.android.synthetic.main.activity_list_comments.*
import org.jetbrains.anko.*
import org.json.JSONArray


class ListCommentsActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private var listaComentarios: List<Comentario> = ArrayList<Comentario>()
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_comments)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val refreshedToken = FirebaseInstanceId.getInstance().token

        MobileAds.initialize(this, "ca-app-pub-2262809297014272~8826799325")

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        mAdView.loadAd(adRequest)

        getComentarios()

        fab.setOnClickListener { view ->
            startActivity(Intent(this@ListCommentsActivity, CreateCommentsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right_animation, R.anim.slide_out_left_animation)
        }
    }

    override fun onRestart() {
        super.onRestart()
        getComentarios()
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAnalytics!!.setCurrentScreen(this@ListCommentsActivity,
                "MainActivity", null)
    }

    fun getComentarios(){
        if(Util.isNetworkAvaliabe(this)){

            var progress = indeterminateProgressDialog("Coletando Comentários...", null)
            var net = Network(this)
            net.getComments(object: Network.HttpCallback {

                override fun onSuccess(response: String) {
                    runOnUiThread {
                        progress.cancel()

                        var gsonBuilder: GsonBuilder = GsonBuilder()
                        var gson: Gson = gsonBuilder.create()

                        listaComentarios = gson.fromJson(response, Array<Comentario>::class.java).toList()

                        val rv: RecyclerView = findViewById(R.id.rvListaComentarios)
                        rv.layoutManager = LinearLayoutManager(this@ListCommentsActivity) as RecyclerView.LayoutManager?
                        rv.hasFixedSize()
                        rv.adapter = AdapterComentarios(this@ListCommentsActivity, listaComentarios, { comentarioItem: Comentario -> rvItemClicked(comentarioItem)})

                    }

                }

                override fun onFailure(response: String?, throwable: Throwable?) {
                    runOnUiThread {
                        progress.cancel()
                        toast("ERRO")
                    }
                }

            })
        }else{
            alert("Problema na Conexão com a Internet.", null) {
                yesButton { finish() }
            }.show()
        }
    }

    private fun rvItemClicked(comentario : Comentario) {

        val gson: Gson = Gson()
        val type = object : TypeToken<Comentario>() {}.type
        val comentarioJson = gson.toJson(comentario, type)

        var intent: Intent = Intent(this@ListCommentsActivity, DetailCommentActivity::class.java)
        intent.putExtra("COMENTARIO", comentarioJson)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right_animation, R.anim.slide_out_left_animation)
    }
}

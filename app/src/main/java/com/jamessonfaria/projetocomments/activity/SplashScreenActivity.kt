package com.jamessonfaria.projetocomments.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.view.View
import com.crashlytics.android.Crashlytics
import com.github.rodlibs.persistencecookie.PersistentCookieStore
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.util.Util
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    private var timer: Timer? = null
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())
        setContentView(R.layout.splash_screen_main)

        val handler: Handler = Handler()

        handler.postDelayed(Runnable {
            run {
                if(Util.isNetworkAvaliabe(this@SplashScreenActivity)) {

                    if(existCookies(this@SplashScreenActivity)){
                        startActivity(Intent(this@SplashScreenActivity, ListCommentsActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right_animation, R.anim.slide_out_left_animation)
                        finish()
                    }else {
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right_animation, R.anim.slide_out_left_animation)
                        finish()
                    }
                }else {
                    alert("Problema na Conexão com a Internet.", null) {
                        yesButton { finish() }
                    }.show()
                }
            }
        },2000)

    }

    private fun existCookies(context: Context): Boolean {
        var myCookieStore = PersistentCookieStore(context)
        return !myCookieStore.getCookies().isEmpty() && myCookieStore.getCookies().get(0).getDomain().equals("teste-aula-ios.herokuapp.com") ; true ; false

    }
}

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
import com.github.rodlibs.persistencecookie.PersistentCookieStore
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.util.Util
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_main)

        val handler: Handler = Handler()

        handler.postDelayed(Runnable {
            run {
                if(Util.isNetworkAvaliabe(this@SplashScreenActivity)) {

                    if(existCookies(this@SplashScreenActivity)){
                        startActivity(Intent(this@SplashScreenActivity, ListCommentsActivity::class.java))
                        finish()
                    }else {
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
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

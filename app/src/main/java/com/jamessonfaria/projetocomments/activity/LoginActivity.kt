package com.jamessonfaria.projetocomments.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jamessonfaria.projetocomments.R
import com.jamessonfaria.projetocomments.util.Network
import com.jamessonfaria.projetocomments.util.Util
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun login(view: View){
        if(Util.isNetworkAvaliabe(this@LoginActivity)){

            var progress = indeterminateProgressDialog("Carregando...", null)
            var net = Network(this@LoginActivity)
            net.login(edtEmail.text.toString(), edtSenha.text.toString(), object: Network.HttpCallback {

                override fun onSuccess(response: String) {
                    runOnUiThread {
                        progress.cancel()
                        startActivity(Intent(this@LoginActivity, ListCommentsActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right_animation, R.anim.slide_out_left_animation)
                        finish()
                    }

                }

                override fun onFailure(response: String?, throwable: Throwable?) {
                    runOnUiThread {
                        progress.cancel()
                        alert("Usuário ou Senha Inválidos.", null) {
                            yesButton {  }
                        }.show()
                    }
                }

            })
        }else{
            alert("Problema na Conexão com a Internet.", null) {
                yesButton { finish() }
            }.show()
        }
    }
}

package com.jamessonfaria.projetocomments.util

import android.content.Context
import android.net.ConnectivityManager
import com.github.rodlibs.persistencecookie.PersistentCookieStore
import com.squareup.okhttp.*
import org.json.JSONArray
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit


class Network (var context: Context) {

    val URL_APPLICATION: String = "http://teste-aula-ios.herokuapp.com/"
    val QUERY_LOGIN: String = "users/sign_in.json"
    val QUERY_COMMENTS: String = "comments.json"
    val QUERY_DELETE: String = "comments/"
    val TAG_JSON: String = ".json"

    var myCookie: PersistentCookieStore

    init {
        myCookie = PersistentCookieStore(context)
    }

    fun delete(id: Int, cb: HttpCallback) {

        if (myCookie == null) {
            myCookie = PersistentCookieStore(context)
        }

        val client = OkHttpClient()
        client.setConnectTimeout(30, TimeUnit.SECONDS)
        client.setWriteTimeout(30, TimeUnit.SECONDS)
        client.cookieHandler = CookieManager(myCookie, CookiePolicy.ACCEPT_ALL)

        val request = Request.Builder()
                .url(URL_APPLICATION + QUERY_DELETE + id + TAG_JSON)
                .delete()
                .build()


        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                if (!call.isCanceled) {
                    cb.onFailure(null, e)
                }
            }

            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                if (response.isSuccessful) {
                    cb.onSuccess(response.body().string())
                } else {
                    cb.onFailure(response.body().string(), null)
                }
            }
        })
    }

    fun login(email: String, password: String, cb: HttpCallback){
        val json: String = "{\"user\":{" +
                "\"email\":\""+ email +"\"," +
                "\"password\":\""+ password +"\"}}";

        if (myCookie == null) {
            myCookie = PersistentCookieStore(context);
        }

        val client = OkHttpClient()
        client.setConnectTimeout(20, TimeUnit.SECONDS)
        client.setWriteTimeout(20, TimeUnit.SECONDS)
        client.cookieHandler = CookieManager(myCookie, CookiePolicy.ACCEPT_ALL)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(URL_APPLICATION + QUERY_LOGIN)
                .post(body)
                .build()


        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                if (!call.isCanceled) {
                    (PersistentCookieStore(context)).removeAll()
                    cb.onFailure(null, e)
                }
            }

            override fun onResponse(response: Response) {
                val value = response.body().string()
                if (response.isSuccessful()) {
                    cb.onSuccess(value)
                } else {
                    cb.onFailure(value, null)
                }
            }
        })

    }

    fun getComments(cb: HttpCallback){

        if (myCookie == null) {
            myCookie = PersistentCookieStore(context);
        }

        val client = OkHttpClient()
        client.setConnectTimeout(20, TimeUnit.SECONDS)
        client.setWriteTimeout(20, TimeUnit.SECONDS)
        client.cookieHandler = CookieManager(myCookie, CookiePolicy.ACCEPT_ALL)

//        val JSON = MediaType.parse("application/json; charset=utf-8")
//        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(URL_APPLICATION + QUERY_COMMENTS)
                .get()
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {

            override fun onResponse(response: Response) {

                val value = response.body().string()
                if (response.isSuccessful()) {
                    cb.onSuccess(value)
                } else {
                    cb.onFailure(value, null)
                }
            }

            override fun onFailure(request: Request, e: IOException) {
                if (!call.isCanceled) {
                    cb.onFailure(null, e)
                }
            }

        })

    }

    interface HttpCallback {
        fun onSuccess(response: String)
        fun onFailure(response: String?, throwable: Throwable?)
    }

}
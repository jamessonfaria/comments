package com.jamessonfaria.projetocomments.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jamessonfaria.projetocomments.R

class CreateCommentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comments)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left_animation, R.anim.slide_out_right_animation)
    }
}

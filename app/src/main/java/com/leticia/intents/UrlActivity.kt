package com.leticia.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.leticia.intents.Constants.URL
import com.leticia.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {

    private val aub: ActivityUrlBinding by lazy {
        ActivityUrlBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aub.root)
        supportActionBar?.subtitle = "UrlActivity"
        val urlAnterior = intent.getStringExtra(URL) ?: ""
//        if(urlAnterior.isNotEmpty()){
//            aub.urlEt.setText(urlAnterior)
//        }
        urlAnterior.takeIf { it.isNotEmpty() }.also {
            aub.urlEt.setText(it)
        }

        aub.entrarUrlBt.setOnClickListener {
            val retornoIntent: Intent = Intent()
            retornoIntent.putExtra(URL, aub.urlEt.text.toString())
            setResult(RESULT_OK, retornoIntent)
            finish()
        }
    }
}
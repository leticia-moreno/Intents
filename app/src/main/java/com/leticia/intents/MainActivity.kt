package com.leticia.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.leticia.intents.Constants.URL
import com.leticia.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var urlArl: ActivityResultLauncher<Intent>

    private lateinit var permissaoChamadaArl: ActivityResultLauncher<String>

    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        supportActionBar?.subtitle = "MainActivity"

        urlArl = registerForActivityResult( //versão lambda
            ActivityResultContracts.StartActivityForResult() //resultado da activity (quando fechar)
        ) { resultado: ActivityResult -> //implementacao da interface que retorna o resultado da segunda tela (quando fecha)
            if(resultado.resultCode == RESULT_OK){
                val urlRetornada = resultado.data?.getStringExtra(URL) ?: ""
                amb.urlTv.text = urlRetornada
            }
        }

        permissaoChamadaArl = registerForActivityResult( //versão "java"
            ActivityResultContracts.RequestPermission(),
            object: ActivityResultCallback<Boolean>{ //crtl + o = overrides disponíveis
                override fun onActivityResult(concedida: Boolean?) {
                    if(concedida!!){
                        chamarNumero(chamar = true)
                    }
                    else{
                        Toast.makeText(
                            this@MainActivity,
                            "Permissão necessária",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        )

        pegarImagemArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado: ActivityResult ->
            if(resultado.resultCode == RESULT_OK){
                val imagemUri = resultado.data?.data //path completo da imagem
                imagemUri?.let {
                    amb.urlTv.text = it.toString()
                }
                val visualizarImagemIntent = Intent(ACTION_VIEW, imagemUri)
                startActivity(visualizarImagemIntent)
            }
        }

        amb.entrarUrlBt.setOnClickListener {
//            val urlActivityInent = Intent(this, UrlActivity::class.java)
            val urlActivityInent = Intent("SEGUNDA_TELA_DO_PROJETO_INTENTS") //essa action pode ser de outro aplicativo. O android procura em todos os manifestos dos aplicativos instalados
            urlActivityInent.putExtra(URL, amb.urlTv.text.toString())
            urlArl.launch(urlActivityInent)
        }
    }

    private fun chamarNumero(chamar: Boolean) {
        if(chamar){
            val uri = Uri.parse("tel: ${amb.urlTv.text}")
            val chamarIntent = Intent(ACTION_CALL, uri)
            startActivity(chamarIntent)
        }
        else{
            val uri = Uri.parse("tel: ${amb.urlTv.text}")
            val chamarIntent = Intent(ACTION_DIAL, uri)
            startActivity(chamarIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //coloca menu na action bar
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //trata das escolhas das opções de menu
        return when(item.itemId){
            R.id.viewMi -> { //Abre o navegador na url digitada
                //ultima linha é o que vai ser retornado.
                val url = Uri.parse(amb.urlTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }
            R.id.callMi -> { //fazer chamada
                //verificar versão do android
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){//caso seja superior ou igual a marshmallow
                    if(checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED){// se tem permissão, faz chamada
                        chamarNumero(chamar = true)
                    }
                    else{//solicita permissão
                        permissaoChamadaArl.launch(CALL_PHONE)
                    }
                }
                //caso contrário fazer chamada
                else{
                    chamarNumero(chamar = true)
                }
                true
            }
            R.id.chooserMi -> {
                val escolherAppIntent = Intent(ACTION_CHOOSER)
                val informacoesIntent = Intent(ACTION_VIEW, Uri.parse(amb.urlTv.text.toString())) //tipo de app que quero abrir
                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha o navegador :)")
                escolherAppIntent.putExtra(EXTRA_INTENT, informacoesIntent)
                startActivity(escolherAppIntent)
                true
            }
            R.id.dialMi -> { //abrir discador com número
                chamarNumero(chamar = false)
                true
            }
            R.id.pickMi -> { //selecionar arquivo
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorioImagens = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                pegarImagemArl.launch(pegarImagemIntent)
                true
            }
            else -> {false}
        }
    }
}
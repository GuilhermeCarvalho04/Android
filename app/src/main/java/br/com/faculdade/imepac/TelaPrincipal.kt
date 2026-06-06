package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TelaPrincipal : AppCompatActivity() {

    private lateinit var text_boas_vindas: TextView
    private lateinit var btn_perfil: FloatingActionButton

    // Botões das novas funcionalidades exigidas pelo professor
    private lateinit var btn_novo_gasto: Button
    private lateinit var btn_historico: Button
    private lateinit var btn_sobre: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)
        supportActionBar?.hide()

        // Inicializar componentes
        text_boas_vindas = findViewById(R.id.text_bem_vindo)
        btn_perfil = findViewById(R.id.btn_perfil)
        btn_novo_gasto = findViewById(R.id.btn_novo_gasto)
        btn_historico = findViewById(R.id.btn_historico)
        btn_sobre = findViewById(R.id.btn_sobre)

        // Buscar nome do usuário para deixar o painel personalizado
        buscarNomeUsuario()

        // Navegação para as novas telas do CRUD
        btn_novo_gasto.setOnClickListener {
            val intent = Intent(this, FormNovoGasto::class.java)
            startActivity(intent)
        }

        btn_historico.setOnClickListener {
            val intent = Intent(this, TelaHistoricoPaginado::class.java)
            startActivity(intent)
        }

        btn_sobre.setOnClickListener {
            val intent = Intent(this, TelaSobre::class.java)
            startActivity(intent)
        }

        btn_perfil.setOnClickListener {
            val intent = Intent(this, TelaPerfil::class.java)
            startActivity(intent)
        }

        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun buscarNomeUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nome = document.getString("nome")
                        text_boas_vindas.text = "Olá, $nome!\nSeja bem-vindo."
                    }
                }
        }
    }
}
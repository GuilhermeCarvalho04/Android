package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TelaPerfil : AppCompatActivity() {

    private lateinit var emailUser: EditText
    private lateinit var usuarioUser: EditText
    private lateinit var bt_sair: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_perfil)

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()

        IniciarComponentes()

        bt_sair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        val email = usuarioAtual?.email
        val uid = usuarioAtual?.uid

        if (email != null && uid != null) {
            emailUser.setText(email)
            buscarDadosUsuario(uid)
        }
    }

    // Busca otimizada diretamente pelo ID fixo do documento (UID)
    private fun buscarDadosUsuario(uid: String) {
        db.collection("Usuarios").document(uid)
            .addSnapshotListener { document, error ->
                if (document != null && document.exists()) {
                    // Pega o campo "nome" salvo no Firebase
                    val nome = document.getString("nome")
                    usuarioUser.setText(nome)
                }
            }
    }

    private fun IniciarComponentes() {
        usuarioUser = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)
        bt_sair = findViewById(R.id.bt_sair)
    }
}
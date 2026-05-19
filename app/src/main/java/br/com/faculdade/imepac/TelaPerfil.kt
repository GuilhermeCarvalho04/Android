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

    // 1. Variáveis globais conforme PDF 09
    private lateinit var emailUser: EditText
    private lateinit var usuarioUser: EditText
    private lateinit var bt_sair: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_perfil)

        // Esconde a barra superior (Página 9 do PDF)
        supportActionBar?.hide()

        // Inicializa o banco de dados
        db = FirebaseFirestore.getInstance()

        IniciarComponentes()

        // Lógica do botão Sair (Logout)
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

    // 2. Método onStart: Busca o email assim que a tela abre (Página 10 do PDF)
    override fun onStart() {
        super.onStart()

        val email = FirebaseAuth.getInstance().currentUser?.email

        if (email != null) {
            emailUser.setText(email)
            buscarDadosUsuario(email)
        }
    }

    // 3. Função de busca no Firestore (Página 10 do PDF)
    private fun buscarDadosUsuario(email: String) {
        db.collection("Usuarios")
            .whereEqualTo("email", email)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (doc in value) {
                        // Pega o campo "nome" salvo no Firebase
                        val nome = doc.getString("nome")
                        usuarioUser.setText(nome)
                    }
                }
            }
    }

    // 4. Inicialização - CORRIGIDO PARA BATER COM SEU XML
    private fun IniciarComponentes() {
        // IDs alterados para baterem com o seu arquivo XML enviado anteriormente
        usuarioUser = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)
        bt_sair = findViewById(R.id.bt_sair)
    }
}
package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica se o usuário já está logado no Firebase
        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            // Se logado, vai direto para a Tela Principal
            val intent = Intent(this, TelaPrincipal::class.java)
            startActivity(intent)
        } else {
            // Se não logado, vai para a Tela de Login
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
        }

        // Fecha a MainActivity para ela não ficar no histórico de telas
        finish()
    }
}
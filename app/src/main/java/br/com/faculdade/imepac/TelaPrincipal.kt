package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class TelaPrincipal : AppCompatActivity() {

    private lateinit var bt_deslogar: Button
    private lateinit var btn_perfil: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_principal)

        // Esconde a barra de cima com segurança
        supportActionBar?.hide()

        // Inicializar componentes
        bt_deslogar = findViewById(R.id.bt_deslogar)
        btn_perfil = findViewById(R.id.btn_perfil)

        // Clique para ir para a Tela de Perfil
        btn_perfil.setOnClickListener {
            val intent = Intent(this, TelaPerfil::class.java)
            startActivity(intent)
        }

        // Clique para deslogar
        bt_deslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        // Garante que o ajuste de tela (Padding) funcione se o ID 'main' existir
        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
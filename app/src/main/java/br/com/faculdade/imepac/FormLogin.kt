package br.com.faculdade.imepac

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class FormLogin : AppCompatActivity() {

    private lateinit var edit_email: EditText
    private lateinit var edit_senha: EditText
    private lateinit var bt_entrada: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_login)

        supportActionBar?.hide()

        edit_email = findViewById(R.id.edit_email_login)
        edit_senha = findViewById(R.id.edit_senha_login)
        bt_entrada = findViewById(R.id.bt_entrar)

        val text_tela_cadastro = findViewById<TextView>(R.id.text_tela_cadastro)
        text_tela_cadastro.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        bt_entrada.setOnClickListener { view ->
            val email = edit_email.text.toString().trim()
            val senha = edit_senha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                AutenticarUsuario(view)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun AutenticarUsuario(view: View) {
        val email = edit_email.text.toString().trim()
        val senha = edit_senha.text.toString().trim()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    IrParaTelaPrincipal()
                } else {
                    // Ajuste: Mensagem amigável de erro
                    val snackbar = Snackbar.make(view, "Usuário ou senha estão errados!", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
    }

    private fun IrParaTelaPrincipal() {
        // Ajuste: Destino agora é a TelaPrincipal
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish()
    }
}
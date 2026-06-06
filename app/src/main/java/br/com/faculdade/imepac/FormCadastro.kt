package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FormCadastro : AppCompatActivity() {

    private lateinit var edit_nome: EditText
    private lateinit var edit_email: EditText
    private lateinit var edit_senha: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_cadastro)
        supportActionBar?.hide()

        edit_nome = findViewById(R.id.edit_nome)
        edit_email = findViewById(R.id.edit_email)
        edit_senha = findViewById(R.id.edit_senha)
        btnCadastrar = findViewById(R.id.bt_cadastrar)

        btnCadastrar.setOnClickListener { view ->
            val nome = edit_nome.text.toString().trim()
            val email = edit_email.text.toString().trim()
            val senha = edit_senha.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG)
                snackbar.show()
            } else {
                cadastrarUsuario(view)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cadastrarUsuario(view: View) {
        val email = edit_email.text.toString().trim()
        val senha = edit_senha.text.toString().trim()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Salva os dados estruturados no banco de dados
                    salvarDadosUsuario(view)
                } else {
                    val erro = task.exception?.message ?: "Erro ao cadastrar usuário"
                    val snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
    }

    // Otimizado para criar o documento com o UID fixo do usuário (Evita bugs de leitura)
    private fun salvarDadosUsuario(view: View) {
        val db = FirebaseFirestore.getInstance()
        val nome = edit_nome.text.toString().trim()
        val usuarioID = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        if (usuarioID != null && email != null) {
            val usuarios = hashMapOf(
                "nome" to nome,
                "email" to email,
                "uid" to usuarioID
            )

            // Usando .document(usuarioID).set() vinculamos a conta diretamente ao ID de autenticação
            db.collection("Usuarios").document(usuarioID)
                .set(usuarios)
                .addOnSuccessListener {
                    val snackbar = Snackbar.make(view, "Cadastro realizado com sucesso!", Snackbar.LENGTH_LONG)
                    snackbar.show()

                    // Abre a Tela Principal e fecha a tela de cadastro
                    val intent = Intent(this, TelaPrincipal::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    val snackbar = Snackbar.make(view, "Erro ao salvar dados: ${e.message}", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
        } else {
            val snackbar = Snackbar.make(view, "Erro interno de autenticação.", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
    }
}
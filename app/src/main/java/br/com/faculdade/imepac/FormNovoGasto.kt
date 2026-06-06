package br.com.faculdade.imepac

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FormNovoGasto : AppCompatActivity() {

    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var btnSalvarGasto: Button
    private lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_novo_gasto)
        supportActionBar?.hide()

        editDescricao = findViewById(R.id.edit_descricao_gasto)
        editValor = findViewById(R.id.edit_valor_gasto)
        btnSalvarGasto = findViewById(R.id.btn_salvar_gasto)
        progressbar = findViewById(R.id.progressbar_gasto)

        btnSalvarGasto.setOnClickListener { view ->
            val descricao = editDescricao.text.toString().trim()
            val valorText = editValor.text.toString().trim()

            if (descricao.isEmpty() || valorText.isEmpty()) {
                Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_LONG).show()
            } else {
                salvarGastoNoFirebase(view, descricao, valorText)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // O 2º INSERT DO APP (Salva o gasto atrelando ao ID do usuário logado)
    private fun salvarGastoNoFirebase(view: View, descricao: String, valorText: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Snackbar.make(view, "Erro de autenticação. Faça login novamente.", Snackbar.LENGTH_LONG).show()
            return
        }

        progressbar.visibility = View.VISIBLE
        btnSalvarGasto.isEnabled = false

        val db = FirebaseFirestore.getInstance()

        val gasto = hashMapOf(
            "descricao" to descricao,
            "valor" to valorText.toDoubleOrNull(),
            "userId" to uid,
            "data" to com.google.firebase.Timestamp.now()
        )

        db.collection("Gastos")
            .add(gasto)
            .addOnSuccessListener {
                progressbar.visibility = View.INVISIBLE
                btnSalvarGasto.isEnabled = true

                Snackbar.make(view, "Gasto cadastrado com sucesso!", Snackbar.LENGTH_LONG).show()

                editDescricao.text.clear()
                editValor.text.clear()
            }
            .addOnFailureListener { e ->
                progressbar.visibility = View.INVISIBLE
                btnSalvarGasto.isEnabled = true
                Snackbar.make(view, "Erro ao salvar: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }
}
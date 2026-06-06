package br.com.faculdade.imepac

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class TelaDetalheGasto : AppCompatActivity() {

    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var btnAtualizar: Button
    private lateinit var btnDeletar: Button

    private val db = FirebaseFirestore.getInstance()
    private var gastoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_detalhe_gasto)
        supportActionBar?.hide()

        editDescricao = findViewById(R.id.edit_detalhe_descricao)
        editValor = findViewById(R.id.edit_detalhe_valor)
        btnAtualizar = findViewById(R.id.btn_atualizar_gasto)
        btnDeletar = findViewById(R.id.btn_deletar_gasto)

        // Pega o ID do gasto enviado pela tela anterior
        gastoId = intent.getStringExtra("GASTO_ID")

        if (gastoId != null) {
            carregarDadosGasto(gastoId!!)
        } else {
            finish() // Se não tiver ID válido, fecha a tela por segurança
        }

        // Lógica de UPDATE do CRUD
        btnAtualizar.setOnClickListener { view ->
            val novaDescricao = editDescricao.text.toString().trim()
            val novoValorText = editValor.text.toString().trim()

            if (novaDescricao.isEmpty() || novoValorText.isEmpty()) {
                Snackbar.make(view, "Campos não podem ficar vazios!", Snackbar.LENGTH_LONG).show()
            } else {
                atualizarGasto(view, novaDescricao, novoValorText)
            }
        }

        // Lógica de DELETE do CRUD
        btnDeletar.setOnClickListener { view ->
            deletarGasto(view)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun carregarDadosGasto(id: String) {
        db.collection("Gastos").document(id)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    editDescricao.setText(doc.getString("descricao"))
                    val valor = doc.getDouble("valor")
                    editValor.setText(valor?.toString() ?: "")
                }
            }
    }

    // Executa a Alteração do registro no Firebase
    private fun atualizarGasto(view: View, descricao: String, valorText: String) {
        val valor = valorText.toDoubleOrNull() ?: 0.0
        gastoId?.let { id ->
            val dadosAtualizados = mapOf(
                "descricao" to descricao,
                "valor" to valor
            )

            db.collection("Gastos").document(id)
                .update(dadosAtualizados)
                .addOnSuccessListener {
                    Snackbar.make(view, "Alterado com sucesso!", Snackbar.LENGTH_LONG).show()
                    // Dá um pequeno tempo para o usuário ler o Snackbar e volta para a lista
                    view.postDelayed({ finish() }, 1000)
                }
                .addOnFailureListener { e ->
                    Snackbar.make(view, "Erro ao atualizar: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
        }
    }

    // Executa a Exclusão do registro no Firebase
    private fun deletarGasto(view: View) {
        gastoId?.let { id ->
            db.collection("Gastos").document(id)
                .delete()
                .addOnSuccessListener {
                    Snackbar.make(view, "Gasto removido!", Snackbar.LENGTH_LONG).show()
                    view.postDelayed({ finish() }, 1000)
                }
                .addOnFailureListener { e ->
                    Snackbar.make(view, "Erro ao deletar: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
        }
    }
}
package br.com.faculdade.imepac

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TelaHistoricoPaginado : AppCompatActivity() {

    private lateinit var recyclerGastos: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val listaGastos = mutableListOf<Gasto>()
    private lateinit var adapter: GastosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_historico_paginado)
        supportActionBar?.hide()

        recyclerGastos = findViewById(R.id.recycler_gastos)
        recyclerGastos.layoutManager = LinearLayoutManager(this)

        // Configura o Adapter e a ação de clique para ir para a tela de Detalhes (CRUD Update/Delete)
        adapter = GastosAdapter(listaGastos) { gastoSelecionado ->
            val intent = Intent(this, TelaDetalheGasto::class.java)
            intent.putExtra("GASTO_ID", gastoSelecionado.id)
            startActivity(intent)
        }
        recyclerGastos.adapter = adapter

        carregarHistorico()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun carregarHistorico() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Busca paginada/ordenada por data trazendo apenas as despesas do usuário atual
        db.collection("Gastos")
            .whereEqualTo("userId", uid)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (querySnapshot != null) {
                    listaGastos.clear()
                    for (doc in querySnapshot.documents) {
                        val id = doc.id
                        val descricao = doc.getString("descricao") ?: ""
                        val valor = doc.getDouble("valor") ?: 0.0
                        listaGastos.add(Gasto(id, descricao, valor))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    // Modelo de dados local
    data class Gasto(val id: String, val descricao: String, val valor: Double)

    // Adapter interno para gerenciar a lista de forma simples e rápida
    class GastosAdapter(private val dados: List<Gasto>, private val clique: (Gasto) -> Unit) :
        RecyclerView.Adapter<GastosAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtDescricao: TextView = view.findViewById(R.id.txt_item_descricao)
            val txtValor: TextView = view.findViewById(R.id.txt_item_valor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dados[position]
            holder.txtDescricao.text = item.descricao
            holder.txtValor.text = String.format("R$ %.2f", item.valor)
            holder.itemView.setOnClickListener { clique(item) }
        }

        override fun getItemCount() = dados.size
    }
}
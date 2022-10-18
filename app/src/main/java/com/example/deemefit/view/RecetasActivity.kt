package com.example.deemefit.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityRecetasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.view.ViewGroup
import android.widget.Toast
import com.example.deemefit.model.RecetaModel
import com.example.deemefit.viewmodel.RecetasAdapter
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dialog_nuevareceta.view.*



class RecetasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRecetasBinding
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecetasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewRecetas.layoutManager= LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.deslizar3)

        swipeRefresh.setOnRefreshListener {
            mostrarRecetas()
            Toast.makeText(this, "Lista de recetas actualizada", Toast.LENGTH_SHORT).show()
        }

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        mostrarRecetas()

        binding.btnNuevaReceta.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_dialog_nuevareceta, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            if(dialog != null){
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.window!!.setLayout(width, height)
            }

            view.btnGuardarReceta.setOnClickListener {
                if (email != null && view.etIntroduceNombreReceta.text.isNotEmpty() && view.etIntroduceIngredientes.text.isNotEmpty() && view.etIntroducePreparacion.text.isNotEmpty()){
                    db.collection("Usuarios").document(email).collection("Recetas").document(view.etIntroduceNombreReceta.text.toString())
                        .set(
                            hashMapOf(
                                "nombreReceta" to view.etIntroduceNombreReceta.text.toString(),
                                "ingredientes" to view.etIntroduceIngredientes.text.toString(),
                                "preparacion" to view.etIntroducePreparacion.text.toString()
                            )
                        )
                    Toast.makeText(this,"Receta guardada correctamente", Toast.LENGTH_SHORT).show()
                    mostrarRecetas()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this,"Introduce unos datos válidos", Toast.LENGTH_SHORT).show()
                }
            }
            view.btnCerrarDialog.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    //Con esta función actualizaremos el recycler view para mostrar las recetas guardadas en la base de datos de Firebase por el usuario a través del adaptador
    private fun mostrarRecetas() {
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        if (swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = false
        }

        if (email != null) {
            db.collection("Usuarios").document(email).collection("Recetas")
                .orderBy("nombreReceta", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    val adapter = GroupAdapter<ViewHolder>()

                    for (document in it){
                        val receta = it.toObjects(RecetaModel::class.java)
                        adapter.add(RecetasAdapter(receta))
                    }
                    binding.recyclerViewRecetas.adapter = adapter
                }
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
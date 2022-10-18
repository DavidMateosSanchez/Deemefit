package com.example.deemefit.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityHabitosBinding
import com.example.deemefit.model.HabitosModel
import com.example.deemefit.viewmodel.HabitosAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.nuevohabito_layout.view.*


class HabitosActivity :AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHabitosBinding
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewHabitos.layoutManager= LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.deslizar4)

        swipeRefresh.setOnRefreshListener {
            mostrarHabitos()
            Toast.makeText(this,"Lista de hábitos actualizada", Toast.LENGTH_SHORT).show()
        }

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        mostrarHabitos()

        binding.btnNuevoHabito.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.nuevohabito_layout, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            view.btnGuardarHabito.setOnClickListener {
                if (email != null && view.etIntroduceHabito.text.isNotEmpty()){
                    db.collection("Usuarios").document(email).collection("Habitos").document(view.etIntroduceHabito.text.toString())
                        .set(
                            hashMapOf(
                                "itemHabito" to view.etIntroduceHabito.text.toString(),
                                "check" to false
                            )
                        )
                    Toast.makeText(this,"Hábito creado correctamente", Toast.LENGTH_SHORT).show()
                    mostrarHabitos()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this,"Introduce el nombre del hábito correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Con esta función actualizaremos el recycler view para mostrar los hábitos guardados en la base de datos de Firebase por el usuario a través del adaptador.
    //Gracias a filtrar de forma ascendente el parámetro "check", logramos que siempre aparezcan en primer lugar los hábitos pendientes
    private fun mostrarHabitos() {
        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }

        if (email != null) {
            db.collection("Usuarios").document(email).collection("Habitos")
                .orderBy("check", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    val adapter = GroupAdapter<ViewHolder>()

                    for (document in it) {
                        val habitos = it.toObjects(HabitosModel::class.java)
                        adapter.add(HabitosAdapter(habitos))
                    }
                    binding.recyclerViewHabitos.adapter = adapter
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
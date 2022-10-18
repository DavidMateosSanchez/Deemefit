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
import com.example.deemefit.databinding.ActivityEjerciciosBinding
import com.example.deemefit.model.EjercicioModel
import com.example.deemefit.viewmodel.EjercicioAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dialog_nuevoejercicio.view.*
import kotlinx.android.synthetic.main.activity_dialog_nuevoejercicio.view.btnGuardarEjercicio



class EjerciciosActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEjerciciosBinding
    private lateinit var swipeRefresh: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjerciciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewEjercicios.layoutManager = LinearLayoutManager(this)
        swipeRefresh = findViewById(R.id.deslizar)

        swipeRefresh.setOnRefreshListener {
            mostrarEjercicios()
            Toast.makeText(this, "Lista de ejercicios actualizada", Toast.LENGTH_SHORT).show()
        }

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        val bundle = intent.extras
        val nombreRutinaAbierta = bundle?.getString("nombreRutina")

        binding.tvEjerciciosTitulo.text = nombreRutinaAbierta

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        mostrarEjercicios()

        binding.btnNuevoEjercicio.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_dialog_nuevoejercicio, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            view.btnGuardarEjercicio.setOnClickListener {

                if (email != null && view.etIntroduceEjercicio.text.isNotEmpty() && view.etIntroduceRepeticiones.text.isNotEmpty() && view.etIntroduceSeries.text.isNotEmpty()) {
                    db.collection("Usuarios").document(email).collection("Rutinas")
                        .document(nombreRutinaAbierta.toString()).collection("Ejercicios")
                        .document(view.etIntroduceEjercicio.text.toString())
                        .set(
                            hashMapOf(
                                "nombreEjercicio" to view.etIntroduceEjercicio.text.toString(),
                                "repeticiones" to view.etIntroduceRepeticiones.text.toString(),
                                "series" to view.etIntroduceSeries.text.toString(),
                                "rutinaPadre" to nombreRutinaAbierta.toString()
                            )
                        )
                    Toast.makeText(this, "Ejercicio guardado correctamente", Toast.LENGTH_SHORT)
                        .show()
                    mostrarEjercicios()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Introduce unos datos válidos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Con esta función actualizaremos el recycler view para mostrar los ejercicios guardados en la base de datos de Firebase por el usuario a través del adaptador
    fun mostrarEjercicios() {

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email
        val bundle = intent.extras
        val nombreRutinaAbierta = bundle?.getString("nombreRutina")

        if (swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = false
        }

        if (email != null) {
            db.collection("Usuarios").document(email).collection("Rutinas")
                .document(nombreRutinaAbierta.toString()).collection("Ejercicios")
                .orderBy("nombreEjercicio", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    val adapter = GroupAdapter<ViewHolder>()

                    for (document in it) {
                        val ejercicio = it.toObjects(EjercicioModel::class.java)
                        adapter.add(EjercicioAdapter(ejercicio))
                    }
                    binding.recyclerViewEjercicios.adapter = adapter
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
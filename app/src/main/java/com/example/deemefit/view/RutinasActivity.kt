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
import com.example.deemefit.databinding.ActivityRutinasBinding
import com.example.deemefit.model.RutinaModel
import com.example.deemefit.viewmodel.RutinaAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dialog_nuevarutina.view.*


class RutinasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRutinasBinding
    private lateinit var swipeRefresh: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRutinasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewRutinas.layoutManager= LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.deslizar2)

        swipeRefresh.setOnRefreshListener {
            mostrarRutinas()
            Toast.makeText(this,"Lista de rutinas actualizada", Toast.LENGTH_SHORT).show()
        }

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        mostrarRutinas()

        binding.btnNuevaRutina.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.activity_dialog_nuevarutina, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            view.btnGuardar.setOnClickListener {
                    if (email != null && view.etIntroduceRutina.text.isNotEmpty()){
                        db.collection("Usuarios").document(email).collection("Rutinas").document(view.etIntroduceRutina.text.toString())
                            .set(
                                hashMapOf(
                                    "nombreRutina" to view.etIntroduceRutina.text.toString()
                                )
                            )
                        Toast.makeText(this,"Rutina creada correctamente", Toast.LENGTH_SHORT).show()
                        mostrarRutinas()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this,"Introduce el nombre de la rutina correctamente", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    //Con esta función actualizaremos el recycler view para mostrar las rutinas guardadas en la base de datos de Firebase por el usuario a través del adaptador
    fun mostrarRutinas(){

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        if (swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = false
        }

        if (email != null) {
            db.collection("Usuarios").document(email).collection("Rutinas")
                .orderBy("nombreRutina", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener {
                    val adapter = GroupAdapter<ViewHolder>()

                    for (document in it){
                        val rutina = it.toObjects(RutinaModel::class.java)
                        adapter.add(RutinaAdapter(rutina))
                    }
                    binding.recyclerViewRutinas.adapter = adapter
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

package com.example.deemefit.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivitySeguimientoPesoBinding
import com.example.deemefit.model.PesoModel
import com.example.deemefit.viewmodel.DatePickerFragment
import com.example.deemefit.viewmodel.PesoAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_seguimiento_peso.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class SeguimientoPesoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySeguimientoPesoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeguimientoPesoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewPeso.layoutManager = LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.deslizar5)

        swipeRefresh.setOnRefreshListener {
            mostrarPeso()
            Toast.makeText(this,"Lista de pesos actualizada", Toast.LENGTH_SHORT).show()
        }

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        mostrarPeso()

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        etFechaNueva.setOnClickListener {
            showDatePickerDialog()
        }

        btnAdd.setOnClickListener {
            if (email != null && etPesoNuevo.text.toString().isNotEmpty() && etFechaNueva.text.toString().isNotEmpty()){
                db.collection("Usuarios").document(email).collection("Pesos")
                    .document(etFechaNueva.text.toString()).set(
                        hashMapOf(
                            "peso" to etPesoNuevo.text.toString(),
                            "fecha" to etFechaNueva.text.toString()
                        )
                    )
                Toast.makeText(this,"Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                etPesoNuevo.text.clear()
                etFechaNueva.text.clear()
                mostrarPeso()
            } else{
                Toast.makeText(this, "Introduce los datos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Utilizaremos esta función para mostrar el dialog con el formato de calendario cuando el usuario haga clic en el Edit Text de fecha
    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{day, month, year -> onDateSelected(day,month+1,year)}
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day:Int, month:Int,year:Int){
        val fechaNueva:String = String.format("%02d-%02d-%d", month, day, year)
        etFechaNueva.setText(fechaNueva)
    }

    //Con esta función actualizaremos el recycler view para mostrar los pesos guardados en la base de datos de Firebase por el usuario a través del adaptador
    fun mostrarPeso(){

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        if (swipeRefresh.isRefreshing){
            swipeRefresh.isRefreshing = false
        }

        if (email!=null){
            db.collection("Usuarios").document(email).collection("Pesos")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val adapter = GroupAdapter<ViewHolder>()

                    for (document in it){
                        val pesos = it.toObjects(PesoModel::class.java)
                        adapter.add(PesoAdapter(pesos))
                    }
                    binding.recyclerViewPeso.adapter = adapter
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
package com.example.deemefit.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.deemefit.databinding.ActivityMostrarrecetaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MostrarRecetaActivity: AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMostrarrecetaBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMostrarrecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val nombreRecetaAbierta = bundle?.getString("nombreReceta")

        binding.tvRecetaGuardadaTitulo.text = nombreRecetaAbierta

        mostrarReceta()

    }


    //Con esta función actualizaremos el recycler view para mostrar las recetas guardados en la base de datos de Firebase por el usuario a través de la obtención de los datos "ingredientes"
    // y "preparación" almacenados y mostrándolos en los TextView correspondientes
    fun mostrarReceta(){

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email
        val bundle = intent.extras
        val nombreRecetaAbierta = bundle?.getString("nombreReceta")

        if (email != null) {
            db.collection("Usuarios").document(email).collection("Recetas").document(nombreRecetaAbierta.toString())
                .get()
                .addOnSuccessListener {
                    binding.tvIngredientes.setText(it.get("ingredientes") as String?)
                    binding.tvPreparacion.setText(it.get("preparacion") as String?)
                }
        }
    }
}
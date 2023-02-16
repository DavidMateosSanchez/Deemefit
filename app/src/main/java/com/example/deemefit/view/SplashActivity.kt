package com.example.deemefit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deemefit.logindeemefit.ui.login.LoginActivity
import com.example.deemefit.logindeemefit.ui.verification.VerificarEmailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    //Aprovechamos esta Activity para hacer la comprobación de si ya existe un usuario que tenga la sesión iniciada en este dispositivo,
    // si es así, no es necesario que se autentifique de nuevo

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            if (currentUser.isEmailVerified) {
                Toast.makeText(
                    this, "¡Bienvenido/a de nuevo!",
                    Toast.LENGTH_SHORT
                ).show()
                reload()
            }else{
                val intent = Intent(this, VerificarEmailActivity::class.java)
                startActivity(intent)
            }
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    private fun reload(){
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
    }
}
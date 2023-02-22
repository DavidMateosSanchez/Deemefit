package com.example.deemefit.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityHomeBinding
import com.example.deemefit.logindeemefit.ui.login.LoginActivity
import com.example.deemefit.logindeemefit.ui.verification.VerificationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, HomeActivity::class.java)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var clicked = false
    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializamos la autenticación de Firebase
        auth = Firebase.auth

        binding.btnMas.setOnClickListener{
            onAddButtonClicked()
        }

        binding.btnCerrarSesion.setOnClickListener {
            abrirDialog()
        }

        binding.btnPerfilUsuario.setOnClickListener {
            val intent = Intent(this, PerfilUsuarioActivity::class.java)
            startActivity(intent)
        }
        if (exit){
            onBackPressed()
        }

        binding.cvRutinas.setOnClickListener{
            val intent = Intent(this, RutinasActivity::class.java)
            startActivity(intent)
        }
        binding.cvRecetas.setOnClickListener {
            val intent = Intent(this, RecetasActivity::class.java)
            startActivity(intent)
        }
        binding.cvHabitos.setOnClickListener {
            val intent = Intent(this, HabitosActivity::class.java)
            startActivity(intent)
        }
        binding.cvCronometro.setOnClickListener {
            val intent = Intent(this, CronometroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onAddButtonClicked() {
        activarVisibilidad(clicked)
        iniciarAnimacion(clicked)
        activarClick(clicked)
        clicked = !clicked
    }

    private fun iniciarAnimacion(clicked : Boolean) {
        if (!clicked){
            binding.btnPerfilUsuario.startAnimation(fromBottom)
            binding.btnCerrarSesion.startAnimation(fromBottom)
            binding.btnMas.startAnimation(rotateOpen)
        }else{
            binding.btnPerfilUsuario.startAnimation(toBottom)
            binding.btnCerrarSesion.startAnimation(toBottom)
            binding.btnMas.startAnimation(rotateClose)
        }
    }

    private fun activarVisibilidad(clicked : Boolean) {
        if (!clicked){
            binding.btnPerfilUsuario.visibility = View.VISIBLE
            binding.btnCerrarSesion.visibility = View.VISIBLE
        } else{
            binding.btnPerfilUsuario.visibility = View.INVISIBLE
            binding.btnCerrarSesion.visibility = View.INVISIBLE
        }
    }

    private fun activarClick(clicked: Boolean){
        if (!clicked){
            binding.btnPerfilUsuario.isClickable = true
            binding.btnCerrarSesion.isClickable = true
        }else{
            binding.btnPerfilUsuario.isClickable = false
            binding.btnCerrarSesion.isClickable = false
        }
    }

    private fun cerrarSesion(){
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    //Cuando el usuario inicie sesión deberemos revisar si el correo electrónico está verificado correctamente, esto lo haremos al iniciar la actividad
    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            if (!currentUser.isEmailVerified) {
                val intent = Intent(this, VerificationActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    //He añadido una confirmación para salir de la aplicación en esta pantalla principal para que se cierre la aplicación al pulsar el botón de atrás 2 veces seguidas en menos de 3 segundos
    override fun onBackPressed() {
        if (exit){
            finishAffinity()
        }else{
            Toast.makeText(this, "Pulsa atrás de nuevo para salir",Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({exit=false}, 3*1000)
        }
    }

    private fun abrirDialog(){
        val duration = Toast.LENGTH_SHORT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("CERRAR SESIÓN")
        builder.setMessage("¿Quieres cerrar sessión?")
            .setPositiveButton("SI",
                DialogInterface.OnClickListener { dialog, id ->
                    Toast.makeText(this, "Cerrando sesión...", duration).show()
                    cerrarSesion()
                })
            .setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    Toast.makeText(this, "¡A seguir poniéndose fuerte!", duration).show()
                })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
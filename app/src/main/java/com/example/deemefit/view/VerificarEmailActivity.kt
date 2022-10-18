package com.example.deemefit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.example.deemefit.databinding.ActivityVerificarEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class VerificarEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityVerificarEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificarEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val currentUser= auth.currentUser

        binding.btnContinuar.setOnClickListener {
            val actualizarPerfil = userProfileChangeRequest {}

            currentUser!!.updateProfile(actualizarPerfil).addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    if (currentUser.isEmailVerified){
                        Toast.makeText(
                            this, "¡Bienvenido/a!",
                            Toast.LENGTH_SHORT
                        ).show()
                        reload()
                    }else {
                        Toast.makeText(this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnEmailVerificacion.setOnClickListener {
            val actualizarPerfil = userProfileChangeRequest {  }

            currentUser!!.updateProfile(actualizarPerfil).addOnCompleteListener{task->
                if (task.isSuccessful){
                    if (currentUser != null){
                        if(currentUser.isEmailVerified){
                            Toast.makeText(this, "El correo electrónico ya está verificado, haz clic en Continuar", Toast.LENGTH_SHORT).show()
                        }else {
                            enviarEmailVerification()
                            tiempoDeEspera()
                        }
                    }
                }
            }
        }

        binding.ivCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    //Esta es la función encargada de enviar el email de verificación al usuario. Se le informa a través de un Toast confirmando el envío del correo
    private fun enviarEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se ha enviado un correo de verificación.\nRecuerda revisar la carpeta de SPAM o correo no deseado",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun reload(){
        val intent = Intent (this, HomeActivity::class.java)
        this.startActivity(intent)
    }

    private fun cerrarSesion(){
        Firebase.auth.signOut()
        val intent = Intent(this, IniciarSesionActivity::class.java)
        startActivity(intent)
    }

    //He añadido esta función para que aparezca un tiempo de espera que bloquee el botón encargado de enviar el email de verificación y de esta forma darle un margen al
    //servidor de Firebase para enviar el correo y evitar que el usuario pueda pulsar excesivas veces seguidas el botón
    private fun tiempoDeEspera(){
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTemporizador.text = (millisUntilFinished / 1000).toString()
                binding.btnEmailVerificacion.isEnabled = false
            }
            override fun onFinish() {
                binding.tvTemporizador.text = ""
                binding.btnEmailVerificacion.isEnabled = true
            }
        }.start()
    }
}
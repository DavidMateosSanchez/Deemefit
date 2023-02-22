package com.example.deemefit.logindeemefit.ui.verification

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.deemefit.databinding.ActivityVerificationBinding
import com.example.deemefit.view.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, VerificationActivity::class.java)
    }

    private lateinit var binding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

//        binding.btnContinuar.setOnClickListener {
//            val actualizarPerfil = userProfileChangeRequest {}
//
//            currentUser!!.updateProfile(actualizarPerfil).addOnCompleteListener{ task ->
//                if (task.isSuccessful){
//                    if (currentUser.isEmailVerified){
//                        Toast.makeText(
//                            this, "¡Bienvenido/a!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        reload()
//                    }else {
//                        Toast.makeText(this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//
//        binding.btnEmailVerificacion.setOnClickListener {
//            val actualizarPerfil = userProfileChangeRequest {  }
//
//            currentUser!!.updateProfile(actualizarPerfil).addOnCompleteListener{task->
//                if (task.isSuccessful){
//                    if (currentUser != null){
//                        if(currentUser.isEmailVerified){
//                            Toast.makeText(this, "El correo electrónico ya está verificado, haz clic en Continuar", Toast.LENGTH_SHORT).show()
//                        }else {
//                            enviarEmailVerification()
//                            tiempoDeEspera()
//                        }
//                    }
//                }
//            }
//        }
//
//        binding.ivCerrarSesion.setOnClickListener {
//            cerrarSesion()
//        }
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnContinuar.setOnClickListener { verificationViewModel.onGoToDetailSelected() }
    }

    private fun initObservers() {
        verificationViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToHome()
            }
        }

        verificationViewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.btnContinuar.isVisible = true
            }
        }
    }

    private fun goToHome() {
        startActivity(HomeActivity.create(this))
    }

//
//    //Esta es la función encargada de enviar el email de verificación al usuario. Se le informa a través de un Toast confirmando el envío del correo
//    private fun enviarEmailVerification() {
//        val user = auth.currentUser
//        user!!.sendEmailVerification()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Se ha enviado un correo de verificación.\nRecuerda revisar la carpeta de SPAM o correo no deseado",
//                        Toast.LENGTH_LONG).show()
//                }
//            }
//    }
//
//    private fun reload(){
//        val intent = Intent (this, HomeActivity::class.java)
//        this.startActivity(intent)
//    }
//
//    private fun cerrarSesion(){
//        Firebase.auth.signOut()
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//    }
//
//    //He añadido esta función para que aparezca un tiempo de espera que bloquee el botón encargado de enviar el email de verificación y de esta forma darle un margen al
//    //servidor de Firebase para enviar el correo y evitar que el usuario pueda pulsar excesivas veces seguidas el botón
//    private fun tiempoDeEspera(){
//        object : CountDownTimer(30000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                binding.tvTemporizador.text = (millisUntilFinished / 1000).toString()
//                binding.btnEmailVerificacion.isEnabled = false
//            }
//            override fun onFinish() {
//                binding.tvTemporizador.text = ""
//                binding.btnEmailVerificacion.isEnabled = true
//            }
//        }.start()
//    }
}
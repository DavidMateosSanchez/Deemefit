package com.example.deemefit.logindeemefit.ui.recoveraccount

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deemefit.databinding.ActivityRecuperarCuentaBinding
import com.example.deemefit.logindeemefit.ui.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, RecoverAccountActivity::class.java)
    }

    private lateinit var binding: ActivityRecuperarCuentaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Cuando el usuario haga clic en el botón para enviarle el email de recuperación de cuenta, verificaremos que haya introducido un email que corresponda con un usuario
        registrado en Firebase, después de eso se le enviará el correo y volveremos a la pantalla de iniciar sesión para que pueda acceder una vez haya seguido
        los pasos necesarios para recuperar su cuenta y modificar su contraseña*/
        binding.btnEnviarEmailRecuperacion.setOnClickListener {
            val emailUsuario = binding.etEmailRecuperar.text.toString()
            if (emailUsuario.isNotEmpty()){
                Firebase.auth.sendPasswordResetEmail(emailUsuario).addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        val intent = Intent(this, LoginActivity::class.java)
                        this.startActivity(intent)
                        Toast.makeText(this,"Se ha enviado un correo de recuperación",
                            Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"No hay ninguna cuenta asociada al email introducido",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this,"Introduce tu correo electrónico",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
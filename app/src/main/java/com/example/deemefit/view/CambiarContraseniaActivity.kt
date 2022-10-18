package com.example.deemefit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deemefit.databinding.ActivityCambiarContraseniaBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class CambiarContraseniaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCambiarContraseniaBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarContraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //Creamos esta variable para determinar los mínimos que deberá tener la contraseña, si quisieramos añadir algún condicionante más podríamos hacerlo desde aquí
        //Es importante que los condicionantes coincidan con los establecidos para el registro de usuario
        val passwordRegex = Pattern.compile("^" +
                "(?=.*[0-9])" +         //Debe contener 1 número
                "(?=.*[a-z])" +        //Debe contener 1 letra minúscula
                "(?=.*[A-Z])" +        //Debe contener 1 letra mayúscula
                "(?=\\S+$)" +           //No puede contener espacios en blanco
                ".{4,}" +               //Debe contener mínimo 4 caracteres
                "$")

        binding.btnCambiarContrasenia.setOnClickListener {
            val contraseniaActual = binding.etContraseniaActual.text.toString()
            val nuevaContrasenia = binding.etContraseniaNueva.text.toString()
            val confirmarContrasenia = binding.etContraseniaNueva2.text.toString()

            if (nuevaContrasenia.isEmpty() || !passwordRegex.matcher(nuevaContrasenia).matches()){
                Toast.makeText(this, "La contraseña introducida es débil",Toast.LENGTH_SHORT).show()
            } else if (nuevaContrasenia != confirmarContrasenia){
                Toast.makeText(this, "Las contraseñas introducidas NO coinciden",Toast.LENGTH_SHORT).show()
            } else {
                cambiarContrasenia(contraseniaActual,nuevaContrasenia)
            }
        }
    }

    //Con esta función chequeamos primero que haya un usuario activo, después utilizamos el método updatePassword para actualizar la contraseña en el servidor de Firebase
    private  fun cambiarContrasenia(current : String, password : String) {
        val user = auth.currentUser

        if (user != null) {
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!, current)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    Toast.makeText(
                                        this, "Se ha cambiado la contraseña correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, PerfilUsuarioActivity::class.java)
                                    this.startActivity(intent)
                                }
                            }

                    } else {
                        Toast.makeText(
                            this, "La contraseña actual introducida es incorrecta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
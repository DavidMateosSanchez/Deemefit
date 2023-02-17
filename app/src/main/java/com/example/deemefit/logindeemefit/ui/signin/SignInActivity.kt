package com.example.deemefit.logindeemefit.ui.signin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.deemefit.databinding.ActivityRegistroBinding
import com.example.deemefit.logindeemefit.ui.login.LoginActivity
import com.example.deemefit.view.HomeActivity
import com.example.deemefit.logindeemefit.ui.verification.VerificarEmailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {


    companion object {
        fun create(context: Context): Intent =
            Intent(context, SignInActivity::class.java)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnRegistro.setOnClickListener {
            val emailUsuario = binding.etEmailR.text.toString()
            val passwordUsuario = binding.etContraseniaR.text.toString()
            val passwordUsuario2 = binding.etContraseniaR2.text.toString()

            //Creamos esta variable para determinar los mínimos que deberá tener la contraseña, si quisieramos añadir algún condicionante más podríamos hacerlo desde aquí
            val passwordRegex = Pattern.compile(
                "^" +
                        "(?=.*[0-9])" +         //Debe contener 1 número
                        "(?=.*[a-z])" +        //Debe contener 1 letra minúscula
                        "(?=.*[A-Z])" +        //Debe contener 1 letra mayúscula
                        "(?=\\S+$)" +           //No puede contener espacios en blanco
                        ".{4,}" +               //Debe contener mínimo 4 caracteres
                        "$"
            )

            if (emailUsuario.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailUsuario).matches()) {
                Toast.makeText(
                    this, "Ingrese un email válido",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (passwordUsuario.isEmpty() || !passwordRegex.matcher(passwordUsuario)
                    .matches()
            ) {
                Toast.makeText(
                    this,
                    "La contraseña debe contener mínimo 4 caracteres, incluyendo: Mínimo 1 letra mayúscula, 1 letra minúscula y 1 número",
                    Toast.LENGTH_LONG
                ).show()
            } else if (passwordUsuario != passwordUsuario2) {
                Toast.makeText(
                    this, "Las contraseñas introducidas NO coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                crearCuenta(emailUsuario, passwordUsuario)
            }
        }
    }

    /*Cuando el usuario quiera registrarse deberemos revisar si el usuario está verificado correctamente, esto lo haremos al iniciar la actividad
    Se accedería a la condición del IF en el momento en que el usuario ya se ha registrado y vuelve acceder de nuevo a la pantalla de registro de usuario pero sin haber
    verificado su correo electrónico*/
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                reload()
            } else {
                val intent = Intent(this, VerificarEmailActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun crearCuenta(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this, "¡Usuario creado correctamente!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, VerificarEmailActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "El correo electrónico introducido ya está en uso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun reload() {
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
    }
}
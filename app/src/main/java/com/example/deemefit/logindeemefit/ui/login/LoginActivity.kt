package com.example.deemefit.logindeemefit.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.deemefit.databinding.ActivityIniciarSesionBinding
import com.example.deemefit.view.HomeActivity
import com.example.deemefit.logindeemefit.ui.recoveraccount.RecoverAccountActivity
import com.example.deemefit.logindeemefit.ui.signin.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityIniciarSesionBinding
    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inicializamos la autenticación de Firebase
        auth = Firebase.auth

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para poder acceder a la aplicación correctamente.", Toast.LENGTH_SHORT).show()
        }

        if (exit){
            onBackPressed()
        }

        binding.btnIniciarSesion.setOnClickListener {
            val emailUsuario = binding.etEmail.text.toString()
            val passwordUsuario = binding.etContrasenia.text.toString()

            if (emailUsuario.isNotEmpty() && passwordUsuario.isNotEmpty()) {
                iniciarSesion(emailUsuario, passwordUsuario)
            } else {
                Toast.makeText(
                    this, "Debes introducir un correo y contraseña válidos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvRegistrate.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.tvOlvidasteContrasenia.setOnClickListener {
            val intent = Intent(this, RecoverAccountActivity::class.java)
            startActivity(intent)
        }

    }

    //A esta función le pasamos el email y contraseña que el usuario ha introducido para comprobar que exista dicho usuario en Firebase e iniciar sesión pasando a la actividado
    //Home. En caso de que no exista el usuario, se mostrará un Toast indicándolo
    private fun iniciarSesion (email: String, password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithEmail:success")
                    reload()
                    } else {
                        // Si el inicio de sesión ha fallado, muestra el siguiente mensaje al usuario
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this, "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    private fun reload(){
        val intent = Intent(this, HomeActivity::class.java)
        this.startActivity(intent)
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

    override fun onBackPressed() {
        if (exit){
            finishAffinity()
        }else{
            Toast.makeText(this, "Pulsa atrás de nuevo para salir",Toast.LENGTH_SHORT).show()
            exit = true
            Handler().postDelayed({exit=false}, 3*1000)
        }
    }
}
package com.example.deemefit.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deemefit.databinding.ActivityEliminarCuentaBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EliminarCuentaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityEliminarCuentaBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEliminarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnEliminarCuenta.setOnClickListener {
            val contrasenia = binding.etContraseniaEliminar.text.toString()
            eliminarCuenta(contrasenia)
        }
    }

    //Llamaremos a esta función cuando el usuario presione el botón de eliminar cuenta, de esta forma le pediremos que se reautentifique y utilizaremos el método delete() para eliminar el usuario de Firebase
    private fun eliminarCuenta(contrasenia: String) {
        val user = auth.currentUser

        if(user != null){
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!,contrasenia)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        user.delete()
                            .addOnCompleteListener { taskEliminarCuenta ->
                                if(taskEliminarCuenta.isSuccessful){
                                    Toast.makeText(this, "Cuenta eliminada correctamente",
                                        Toast.LENGTH_SHORT).show()
                                    cerrarSesion()
                                }
                            }
                    } else {
                        Toast.makeText(this, "La contraseña introducida es incorrecta",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //Utilizaremos esta función para volver a la pantalla de iniciar sesión en el caso de que se haya eliminado correctamente el usuario
    private fun cerrarSesion() {
        auth.signOut()
        val intent = Intent(this, IniciarSesionActivity::class.java)
        this.startActivity(intent)
    }
}
package com.example.deemefit.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deemefit.databinding.ActivityPerfilUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class PerfilUsuarioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityPerfilUsuarioBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        if (!checkForInternet(this)) {
            Toast.makeText(this, "Por favor, utiliza una conexión de red para visualizar los datos almacenados correctamente.", Toast.LENGTH_SHORT).show()
        }

        db.collection("Usuarios").document(email.toString()).get().addOnSuccessListener {
            binding.etNombreUsuario.setText(it.get("Nombre") as String?)
            binding.etEdadUsuario.setText((it.get("Edad").toString() as String?))
            binding.tvNombreUsuario.setText(it.get("Nombre") as String?)
        }

        actualizarDatos()

        binding.btnActualizarDatos.setOnClickListener {
            val nombre = binding.etNombreUsuario.text.toString()
            val edad = binding.etEdadUsuario.text.toString()

            if (nombre.isNotEmpty() && edad.isNotEmpty()){
                val edadInt = edad.toInt()

                db.collection("Usuarios").document(email.toString()).set(
                    hashMapOf("Nombre" to nombre,
                        "Edad" to edadInt)
                )
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                subirFoto()
                actualizarDatos()
            } else{
                Toast.makeText(this, "Datos introducidos NO válidos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnKcal.setOnClickListener {
            val intent = Intent(this, CalcularKcalActivity::class.java)
            startActivity(intent)
        }

        binding.fotoPerfil.setOnClickListener{
            seleccionarFoto()
        }

        binding.tvActualizarContrasenia.setOnClickListener {
            val intent = Intent(this, CambiarContraseniaActivity::class.java)
            startActivity(intent)
        }

        binding.tvEliminarCuenta.setOnClickListener {
            val intent = Intent(this, EliminarCuentaActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegistroPeso.setOnClickListener {
            val intent = Intent(this, SeguimientoPesoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun seleccionarFoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    private fun subirFoto(){

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email

        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Subiendo imagen...")
        progresDialog.setCancelable(false)
        progresDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReference(email.toString())

        if(::imageUri.isInitialized){
            storageReference.putFile(imageUri).
            addOnSuccessListener {
                binding.fotoPerfil.setImageURI(null)
                Toast.makeText(this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                val localfile = File.createTempFile("tempImage","jpg")
                storageReference.getFile(localfile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    binding.fotoPerfil.setImageBitmap(bitmap)
                }.addOnFailureListener{
                    Toast.makeText(this, "No ha sido posible cargar la imagen de perfil del usuario", Toast.LENGTH_SHORT).show()
                }
                if (progresDialog.isShowing) progresDialog.dismiss()
            }.addOnFailureListener{

                if (progresDialog.isShowing) progresDialog.dismiss()
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        } else if (progresDialog.isShowing){
            progresDialog.dismiss()
        }
    }


    //Utilizamos esta función para mostrar la imagen elegida por el usuario previamente a guardarla en la base de datos de Firebase
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){

            imageUri = data?.data!!
            binding.fotoPerfil.setImageURI(imageUri)

        }
    }

    private fun actualizarDatos() {

        auth = FirebaseAuth.getInstance()
        val user = Firebase.auth.currentUser
        val email = user?.email
        val nombre = binding.etNombreUsuario.text.toString()
        val edad = binding.etEdadUsuario.text.toString()
        val storageReference = FirebaseStorage.getInstance().getReference(email.toString())

        binding.tvEmailUsuario.text = email.toString()

        if (nombre.isNotEmpty() && edad.isNotEmpty()) {
            db.collection("Usuarios").document(email.toString()).get().addOnSuccessListener {
                binding.etNombreUsuario.setText(it.get("Nombre") as String?)
                binding.etEdadUsuario.setText((it.get("Edad").toString() as String?))
                binding.tvNombreUsuario.setText(it.get("Nombre") as String?)
            }
        }
        val localfile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.fotoPerfil.setImageBitmap(bitmap)
        }
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
}
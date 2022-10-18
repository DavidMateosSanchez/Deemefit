package com.example.deemefit.viewmodel

import android.content.ContentValues
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.deemefit.R
import com.example.deemefit.model.EjercicioModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.items_ejercicios.view.*


//En esta clase creamos y gestionamos el adaptador que transformará los datos de los ejercicios almacenados en Firebase y los mostrará en el recycler view correspondiente
class EjercicioAdapter (val ejercicio: MutableList<EjercicioModel>) : Item<ViewHolder>(){

    private val db = FirebaseFirestore.getInstance()


    override fun getLayout(): Int {
        return R.layout.items_ejercicios
    }
    override fun bind(viewHolder: ViewHolder, position: Int){

        val user = Firebase.auth.currentUser?.email
        val ejercicios = ejercicio[position]
        viewHolder.itemView.tvEjercicioMostrado.text = ejercicios.nombreEjercicio
        viewHolder.itemView.tvRepeticionesMostradas.text = ejercicios.repeticiones
        viewHolder.itemView.tvSeriesMostradas.text = ejercicios.series
        viewHolder.itemView.borrarEjercicio.context


        //Gestionaremos el clic del usuario en el Image Button correspondiente para eliminar el ejercicio, creando un dialog de confirmación para no borrarlo accidentalmente
        viewHolder.itemView.borrarEjercicio.setOnClickListener {
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("ELIMINAR EJERCICIO")
            builder.setMessage("¿Quieres eliminar el ejercicio?")
                .setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
                        db.collection("Usuarios").document(user.toString()).collection("Rutinas").document(ejercicios.rutinaPadre).collection("Ejercicios")
                            .document(viewHolder.itemView.tvEjercicioMostrado.text.toString())
                            .delete()
                            .addOnSuccessListener { Log.d(ContentValues.TAG,"Documento borrado correctamente") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error borrando el documento",e) }
                        Toast.makeText(viewHolder.itemView.borrarEjercicio.context, "Ejercicio eliminado correctamente", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}
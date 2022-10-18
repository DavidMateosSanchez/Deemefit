package com.example.deemefit.viewmodel

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.deemefit.R
import com.example.deemefit.model.PesoModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.items_peso.view.*

//En esta clase creamos y gestionamos el adaptador que transformará el peso almacenado en Firebase y lo mostrará en el recycler view correspondiente
class PesoAdapter (val peso: MutableList<PesoModel>) : Item<ViewHolder>(){

    private val db = FirebaseFirestore.getInstance()

    override fun getLayout(): Int {
        return R.layout.items_peso
    }

    override fun bind(viewHolder: ViewHolder, position: Int){

        val user = Firebase.auth.currentUser?.email
        val pesos = peso[position]
        viewHolder.itemView.tvPesoGuardado.text = pesos.peso
        viewHolder.itemView.tvFechaGuardada.text = pesos.fecha
        viewHolder.itemView.BorrarPeso.context

        //Gestionaremos el clic del usuario en el Image Button correspondiente para eliminar el peso, creando un dialog de confirmación para no borrarlo accidentalmente
        viewHolder.itemView.BorrarPeso.setOnClickListener {
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("ELIMINAR PESO")
            builder.setMessage("¿Quieres eliminar el peso?")
                .setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
            db.collection("Usuarios").document(user.toString()).collection("Pesos").document(viewHolder.itemView.tvFechaGuardada.text.toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG,"Documento borrado correctamente") }
                .addOnFailureListener { e -> Log.w(TAG, "Error borrando el documento",e) }
            Toast.makeText(viewHolder.itemView.BorrarPeso.context, "Peso eliminado correctamente", Toast.LENGTH_SHORT).show()
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
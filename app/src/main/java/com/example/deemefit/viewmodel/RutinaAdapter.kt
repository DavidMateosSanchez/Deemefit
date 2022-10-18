package com.example.deemefit.viewmodel

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.deemefit.R
import com.example.deemefit.model.RutinaModel
import com.example.deemefit.view.EjerciciosActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.items_rutinas.view.*

//En esta clase creamos y gestionamos el adaptador que transformará los datos de las rutinas almacenadas en Firebase y las mostrará en el recycler view correspondiente
class RutinaAdapter(val nombreRutina: MutableList<RutinaModel>) : Item<ViewHolder>(){

    private val db = FirebaseFirestore.getInstance()

    override fun getLayout(): Int {
        return R.layout.items_rutinas
    }
    override fun bind(viewHolder: ViewHolder, position: Int){

        val user = Firebase.auth.currentUser?.email
        val rutinas = nombreRutina[position]
        viewHolder.itemView.tvRutinaGuardada.text = rutinas.nombreRutina
        viewHolder.itemView.BorrarRutina.context
        viewHolder.itemView.cvRutinasGuardadas.context

        //Gestionaremos el clic del usuario en el Image Button correspondiente para eliminar la rutina, creando un dialog de confirmación para no borrarlo accidentalmente,
        //ya que en este caso al eliminar una rutina, también se eliminan todos los ejercicios creados en ella
        viewHolder.itemView.BorrarRutina.setOnClickListener {
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("ELIMINAR RUTINA")
            builder.setMessage("¿Quieres eliminar la rutina?")
                .setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
                        db.collection("Usuarios").document(user.toString()).collection("Rutinas").document(viewHolder.itemView.tvRutinaGuardada.text.toString())
                            .delete()
                            .addOnSuccessListener { Log.d(ContentValues.TAG,"Documento borrado correctamente") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error borrando el documento",e) }
                        Toast.makeText(viewHolder.itemView.BorrarRutina.context, "Rutina eliminada correctamente", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        //Cuando el usuario haga clic en el card view correspondiente a una rutina, deberemos abrir la actividad encargada de mostrar los ejercicios que componen la rutina,
        // para ello le pasamos a la nueva actividad el nombre de la rutina en la que el usuario ha hecho clic
        viewHolder.itemView.cvRutinasGuardadas.setOnClickListener {
            val nombreRutina = viewHolder.itemView.tvRutinaGuardada.text.toString()
            val intent = Intent(viewHolder.itemView.context, EjerciciosActivity::class.java)
                intent.putExtra("nombreRutina", nombreRutina)
            viewHolder.itemView.context.startActivity(intent)
        }

    }
}

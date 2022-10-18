package com.example.deemefit.viewmodel

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.deemefit.R
import com.example.deemefit.model.RecetaModel
import com.example.deemefit.view.MostrarRecetaActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.items_recetas.view.*



//En esta clase creamos y gestionamos el adaptador que transformará los datos de las recetas almacenadas en Firebase y las mostrará en el recycler view correspondiente
class RecetasAdapter (val nuevaReceta: MutableList<RecetaModel>): Item<ViewHolder>() {
    private val db = FirebaseFirestore.getInstance()

    override fun getLayout(): Int {
        return R.layout.items_recetas
    }

    override fun bind(viewHolder: ViewHolder, position: Int){
        val user = Firebase.auth.currentUser?.email
        val recetas = nuevaReceta[position]

        viewHolder.itemView.tvRecetaGuardada.text = recetas.nombreReceta

        viewHolder.itemView.BorrarReceta.context
        viewHolder.itemView.cvRecetasGuardadas.context

        //Gestionaremos el clic del usuario en el Image Button correspondiente para eliminar la receta, creando un dialog de confirmación para no borrarlo accidentalmente
        viewHolder.itemView.BorrarReceta.setOnClickListener {
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("ELIMINAR RECETA")
            builder.setMessage("¿Quieres eliminar la receta?")
                .setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
                        db.collection("Usuarios").document(user.toString()).collection("Recetas").document(viewHolder.itemView.tvRecetaGuardada.text.toString())
                            .delete()
                            .addOnSuccessListener { Log.d(ContentValues.TAG,"Documento borrado correctamente") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error borrando el documento",e) }
                        Toast.makeText(viewHolder.itemView.BorrarReceta.context, "Receta eliminada correctamente", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        //Cuando el usuario haga clic en el card view correspondiente a una receta, deberemos abrir la actividad encargada de mostrar la receta completa, para ello le
        //pasamos a la nueva actividad el nombre de la receta en la que el usuario ha hecho clic
        viewHolder.itemView.cvRecetasGuardadas.setOnClickListener {
            val nombreReceta = viewHolder.itemView.tvRecetaGuardada.text.toString()
            val intent = Intent(viewHolder.itemView.context, MostrarRecetaActivity::class.java)
            intent.putExtra("nombreReceta", nombreReceta)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

}
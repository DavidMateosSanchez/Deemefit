package com.example.deemefit.viewmodel

import android.content.ContentValues
import android.content.DialogInterface
import android.graphics.Paint
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.deemefit.R
import com.example.deemefit.model.HabitosModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.habito_item.view.*

//En esta clase creamos y gestionamos el adaptador que transformará los hábitos almacenados en Firebase y los mostrará en el recycler view correspondiente
class HabitosAdapter(val nombreHabito: MutableList<HabitosModel>) : Item<ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun getLayout(): Int {
        return R.layout.habito_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val user = Firebase.auth.currentUser?.email
        val habitos = nombreHabito[position]
        viewHolder.itemView.tvHabitosGuardados.text = habitos.itemHabito
        viewHolder.itemView.habitosCheck.isChecked = habitos.check
        viewHolder.itemView.BorrarHabito.context

        if (viewHolder.itemView.habitosCheck.isChecked){
            viewHolder.itemView.tvHabitosGuardados.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        //Gestionaremos el clic del usuario en el Image Button correspondiente para eliminar el hábito, creando un dialog de confirmación para no borrarlo accidentalmente
        viewHolder.itemView.BorrarHabito.setOnClickListener {
            val builder = AlertDialog.Builder(viewHolder.itemView.context)
            builder.setTitle("ELIMINAR HÁBITO")
            builder.setMessage("¿Quieres eliminar el hábito?")
                .setPositiveButton("SI",
                    DialogInterface.OnClickListener { dialog, id ->
                        db.collection("Usuarios").document(user.toString()).collection("Habitos").document(viewHolder.itemView.tvHabitosGuardados.text.toString())
                            .delete()
                            .addOnSuccessListener { Log.d(ContentValues.TAG,"Documento borrado correctamente") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error borrando el documento",e) }
                        Toast.makeText(viewHolder.itemView.BorrarHabito.context, "Hábito eliminado correctamente", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        //Desde el check correspondiente de cada hábito almacenaremos su estado, modificándolo cada vez que el usuario haga clic en él. De igual forma gracias a paintFlags
        //tacharemos el texto que aparece en el TextView cuando el estado del check sea true
        viewHolder.itemView.habitosCheck.setOnClickListener {
            if (viewHolder.itemView.habitosCheck.isChecked){
                db.collection("Usuarios").document(user.toString()).collection("Habitos").document(viewHolder.itemView.tvHabitosGuardados.text.toString())
                    .set(
                        hashMapOf(
                            "itemHabito" to viewHolder.itemView.tvHabitosGuardados.text.toString(),
                            "check" to true
                        )
                    )
                viewHolder.itemView.tvHabitosGuardados.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else{
                db.collection("Usuarios").document(user.toString()).collection("Habitos").document(viewHolder.itemView.tvHabitosGuardados.text.toString())
                    .set(
                        hashMapOf(
                            "itemHabito" to viewHolder.itemView.tvHabitosGuardados.text.toString(),
                            "check" to false
                        )
                    )
                viewHolder.itemView.tvHabitosGuardados.paintFlags = 0
            }
        }
    }
}
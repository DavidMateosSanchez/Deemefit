package com.example.deemefit.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deemefit.R
import com.example.deemefit.databinding.ActivityCalcularKcalBinding
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.informacion_dialog.view.*


class CalcularKcalActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    var sexo = ""
    var nivelActividad = 0.0

    private lateinit var binding: ActivityCalcularKcalBinding

    override fun onCreate (savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityCalcularKcalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sex = resources.getStringArray(R.array.sexos)
        val adapter = ArrayAdapter(
            this,
            R.layout.lista_items,
            sex
        )

        val actividad = resources.getStringArray(R.array.actividad)
        val adapter2 = ArrayAdapter(
            this,
            R.layout.lista_items,
            actividad
        )

        with(binding.tvActividad){
            setAdapter(adapter2)
            onItemClickListener = this@CalcularKcalActivity
        }

        with(binding.tvSexo){
            setAdapter(adapter)
            onItemClickListener = this@CalcularKcalActivity
        }
        binding.btnCalcularKcal.setOnClickListener {

            if (binding.etEdad.text.isNotEmpty() && binding.etAltura.text.isNotEmpty() && binding.etPeso.text.isNotEmpty() && binding.tvActividad.text.isNotEmpty() && binding.tvSexo.text.isNotEmpty()){
                var sumaTotal = Integer.parseInt(binding.etEdad.text.toString()) + Integer.parseInt(binding.etAltura.text.toString()) + Integer.parseInt(binding.etPeso.text.toString())
                if(sumaTotal < 1000) {
                    calcularKcal()
                }else {
                    Toast.makeText(this, "Los números introducidos son demasiado elevados",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Por favor, introduce los datos requeridos",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnInformacion.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.informacion_dialog, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()

            view.btnVolver.setOnClickListener{
                dialog.dismiss()
            }
        }
    }

    //Con esta función revisamos la opción elegida por el usuario para de esta forma, modificar los números utilizados en la función del cálculo de las kcal
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString()

        if (item == "Masculino"){
            sexo = "Masculino"
        }
        if (item == "Femenino"){
            sexo = "Femenino"
        }
        if (item == "Nada de ejercicio y trabajar sentado"){
            nivelActividad = 1.2
        }
        if (item == "Ejercicio ligero (+/-2 días semanales)"){
            nivelActividad = 1.375
        }
        if (item == "Ejercicio moderado (+/- 4 días semanales)"){
            nivelActividad = 1.55
        }
        if (item == "Ejercicio intenso (+/- 6 días semanales)"){
            nivelActividad = 1.725
        }
        if (item == "Entrenamiento intenso diario"){
            nivelActividad = 1.9
        }
    }

    //Con esta función realizamos el cálculo de las kcal, partiendo de la opción elegida por el ususario en la variable "Sexo", ya que la fórmula no es igual para ambos sexos
    private fun calcularKcal(){
        if (sexo == "Masculino"){
            val resultadoKcal = ((10*(binding.etPeso.text.toString().toInt())) + (6.25*(binding.etAltura.text.toString().toInt())) - (5*(binding.etEdad.text.toString().toInt())) +5) * nivelActividad
            val aumentoKcal = resultadoKcal*1.2
            val reducirKcal = resultadoKcal/1.2
            binding.tvFormula.text = "TMB: " +Math.round(resultadoKcal) +" kcal"
            binding.tvCambios.text = "Si tu deseo es aumentar de peso, deberás consumir " + Math.round(aumentoKcal) +" kcal diarias, en cambio si quieres bajar de peso " +
                    "deberías consumir: "+ Math.round(reducirKcal) + " kcal diarias."
        }
        if (sexo == "Femenino"){
            val resultadoKcal = ((10*(binding.etPeso.text.toString().toInt())) + (6.25*(binding.etAltura.text.toString().toInt())) - (5*(binding.etEdad.text.toString().toInt())) -161) * nivelActividad
            val aumentoKcal = resultadoKcal*1.2
            val reducirKcal = resultadoKcal/1.2
            binding.tvFormula.text = "TMB: " +Math.round(resultadoKcal).toString() +" kcal"
            binding.tvCambios.text = "Si tu deseo es aumentar de peso, deberás consumir " + Math.round(aumentoKcal) +" kcal diarias, en cambio si quieres bajar de peso " +
                    "deberías consumir: " + Math.round(reducirKcal) + " kcal diarias."
        }
    }

}
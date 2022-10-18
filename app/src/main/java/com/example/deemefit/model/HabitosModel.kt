package com.example.deemefit.model

// Creamos un modelo para almacenar y mostrar los hábitos diarios del usuario, incluyendo el nombre del mismo hábito y un check para revisar si ha sido realizado

class HabitosModel (val itemHabito:String, val check:Boolean) {
    constructor():this("",false)
}

package com.example.deemefit.model

//Creamos un modelo de los ejercicios guardados, incluyendo el nombre, las repeticiones y series a realizar y el nombre de la rutina padre a la que pertenece

class EjercicioModel (val nombreEjercicio:String, val repeticiones:String, val series:String, val rutinaPadre:String) {
    constructor():this("","","","")
}
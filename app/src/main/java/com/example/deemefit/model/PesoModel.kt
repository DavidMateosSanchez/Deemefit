package com.example.deemefit.model

// Creamos un modelo del peso guardado por el usuario, utilizando la fecha como indicador y el peso almacenado

class PesoModel (val peso:String, val fecha:String) {
    constructor():this("","")
}
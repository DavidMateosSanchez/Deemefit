package com.example.deemefit.model

// Creamos un modelo para almacenar y mostrar las recetas, incluyendo el nombre de la misma, los ingredientes necesarios y la preparación

class RecetaModel (val nombreReceta:String, val ingredientes: String, val preparacion: String){
    constructor():this("","","")
}
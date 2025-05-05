package com.practicas;

import org.opencv.core.Core;

//ROLES
//user
//academico
//admin

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        Analizador analizador = new Analizador();
        Valoracion v = analizador.analizar("./src/main/files/Cachamas/pez/1744934698308.jpg");
        analizador.evaluarCalidad(v);
    }

}
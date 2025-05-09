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

        Imagen img = new Imagen("C:/Users/jesus/Pictures/cachamas/pez/1744934697771.jpg");
        img.segmentarImagen("C:/Users/jesus/Pictures/cachamas/outs/1744934697771.jpg");
        
    }

}
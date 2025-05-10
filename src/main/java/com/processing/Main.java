package com.processing;

import org.opencv.core.Core;

import java.awt.*;

//ROLES
//user
//academico
//admin

public class Main {
    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.opencv_java.class);
    }

    public static void main(String[] args) {

        Imagen img = new Imagen("C:/Users/jesus/Pictures/cachamas/pez/1744934697771.jpg");
        img.segmentarImagen("C:/Users/jesus/Pictures/cachamas/outs/1744934697771.jpg");
        System.out.println(img.toString());
    }

}
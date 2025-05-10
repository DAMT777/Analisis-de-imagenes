package com.processing;

import com.databaseInteractions.DBConnect;
import com.utils.HashUtil;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol;


    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public Usuario(int id, String nombre, String apellido, String email, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        //hashear la contraseña
        this.password = password;
        this.rol = rol;
    }

    public String cargarImagenes() {
        // Lógica para cargar imagenes
        return "Imágenes cargadas correctamente.";
    }

    public void cambiarDatosUsuario(int id) {
        // Lógica para actualizar datos del usuario
    }


    public boolean iniciarSesion() {
        return DBConnect.validarCredenciales(email, password);
    }
    private void getInfo() {
        if (iniciarSesion()) {
            Usuario usuario = DBConnect.getInfo(this.email); // Obtener el objeto Usuario
            if (usuario != null) {
                this.id = usuario.getId();
                this.nombre = usuario.getNombre();
                this.apellido = usuario.getApellido();
                this.email = usuario.getEmail();
                this.rol = usuario.getRol();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
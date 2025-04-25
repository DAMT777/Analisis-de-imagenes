package com.practicas;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    public Usuario(int id, String nombre, String apellido, String email, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    public String cargarImagenes() {
        // Lógica para cargar imagenes
        return "Imágenes cargadas correctamente.";
    }

    public void cambiarDatosUsuario(int id) {
        // Lógica para actualizar datos del usuario
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
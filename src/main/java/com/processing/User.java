package com.processing;

import com.databaseInteractions.DBConnect;
import com.utils.HashUtil;

/**
 * Representa un usuario del sistema, incluyendo sus datos personales y credenciales.
 *
 * Métodos principales:
 * <ul>
 *   <li><b>User(String email, String password)</b>: Constructor para crear un usuario con email y contraseña (hasheada).</li>
 *   <li><b>User(int id, String nombre, String apellido, String email, String password, String rol)</b>: Constructor completo.</li>
 *   <li><b>setPassword(String password)</b>: Establece la contraseña aplicando hash.</li>
 *   <li><b>registrar(User nuevoUsuario)</b>: Registra un nuevo usuario (solo admin).</li>
 *   <li><b>obtenerUsuarioPorCorreo(String correo)</b>: Obtiene un usuario por correo (solo admin).</li>
 *   <li><b>actualizarUsuario(...)</b>: Actualiza datos de un usuario (solo admin).</li>
 *   <li><b>eliminarUsuario(int id)</b>: Elimina un usuario por ID (solo admin).</li>
 *   <li><b>iniciarSesion()</b>: Verifica si las credenciales del usuario son válidas.</li>
 *   <li><b>getInfo()</b>: Actualiza los atributos del usuario con la información de la base de datos.</li>
 *   <li><b>Getters y Setters</b>: Métodos de acceso y modificación para los atributos.</li>
 * </ul>
 */
public class User {
    private int id;
    private String nombre;
    private String apellido;
    private String empresa;
    private String email;
    private String password;
    private String rol;
    private String imgProfilePath;


    /**
     * Crea un nuevo usuario con el email y la contraseña proporcionados.
     * La contraseña se almacena de forma segura usando hash.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña en texto plano (será hasheada).
     */
    public User(String email, String password) {
        this.email = email;
        this.password = HashUtil.hashPassword(password);
    }

    public User(int id, String nombre, String apellido, String email, String empresa, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.empresa = empresa;
        //hashear la contraseña
        this.password = HashUtil.hashPassword(password);
        this.rol = rol;
    }


    public boolean setUserName(int id, String nombre) {
        boolean updated = DBConnect.actualizarNombreUsuario(id, nombre);
        if (updated) {
            this.nombre = nombre;
        }
        return updated;
    }

    public boolean setUserLastName(int id, String apellido) {
        boolean updated = DBConnect.actualizarApellidoUsuario(id, apellido);
        if (updated) {
            this.apellido = apellido;
        }
        return updated;
    }

    public boolean setUserPassword(int id, String password) {
        password = HashUtil.hashPassword(password);
        boolean updated = DBConnect.actualizarPasswordUsuario(id, password);
        if (updated) {
            this.password = password;
        }
        return updated;
    }

    public boolean setUserEmail(int id, String email) {
        boolean updated = DBConnect.actualizarCorreoUsuario(id, email);
        if (updated) {
            this.email = email;
        }
        return updated;
    }


    public boolean setUserEmpresa(int id, String empresa) {
        boolean updated = DBConnect.actualizarEmpresaUsuario(id, empresa);
        if (updated) {
            this.empresa = empresa;
        }
        return updated;
    }

    public boolean setUserRol(int id, String rol) {
        boolean updated = DBConnect.actualizarRolUsuario(id, rol);
        if (updated) {
            this.rol = rol;
        }
        return updated;
    }


    public boolean setUserProfileImage(int id, String rutaImagenLocal) {
        String updated = DBConnect.actualizarImagenPerfil(id, rutaImagenLocal);
        // Si tienes un atributo local para la imagen, actualízalo aquí:
        if(updated != null) this.imgProfilePath = updated;
        // if (updated) this.imgProfilePath = DBConnect.getProfilePath(id);
        return updated != null;
    }

   /**
    * Registra un nuevo usuario en la base de datos si el usuario actual tiene rol "admin".
    * Valida que el objeto y sus campos requeridos no sean nulos.
    *
    * @param nuevoUsuario Objeto User con los datos del usuario a registrar.
    * @return true si el registro fue exitoso, false en caso contrario.
    */
   public boolean registrar(User nuevoUsuario) {
       if (nuevoUsuario == null) return false;
       if (!"admin".equalsIgnoreCase(this.rol)) return false;
       if (nuevoUsuario.getNombre() == null || nuevoUsuario.getApellido() == null ||
           nuevoUsuario.getEmpresa() == null || nuevoUsuario.getEmail() == null ||
           nuevoUsuario.password == null || nuevoUsuario.getRol() == null) {
           return false;
       }
       return DBConnect.registrarUsuario(
           nuevoUsuario.getNombre(),
           nuevoUsuario.getApellido(),
           nuevoUsuario.getEmpresa(),
           nuevoUsuario.getEmail(),
           nuevoUsuario.password,
           nuevoUsuario.getRol()
       );
   }




   /**
    * Obtiene un usuario por su correo solo si el usuario actual es admin.
    * @param correo Correo electrónico del usuario a buscar.
    * @return El objeto User encontrado o null si no es admin o no existe.
    */
   public User obtenerUsuarioPorCorreo(String correo) {
       if (!"admin".equalsIgnoreCase(this.rol)) return null;
       return DBConnect.obtenerUsuarioPorCorreo(correo);
   }






   /**
    * Actualiza los datos de un usuario solo si el usuario actual es admin.
    * @param id ID del usuario a actualizar.
    * @param nombre Nuevo nombre.
    * @param apellido Nuevo apellido.
    * @param empresa Nueva empresa.
    * @param correo Nuevo correo.
    * @param rol Nuevo rol.
    * @return true si la actualización fue exitosa, false en caso contrario o si no es admin.
    */
   public boolean actualizarUsuario(int id, String nombre, String apellido, String empresa, String correo, String rol) {
       if (!"admin".equalsIgnoreCase(this.rol)) return false;
       return DBConnect.actualizarUsuario(id, nombre, apellido, empresa, correo, rol);
   }

   /**
    * Elimina un usuario por su ID solo si el usuario actual es admin.
    * @param id ID del usuario a eliminar.
    * @return true si la eliminación fue exitosa, false en caso contrario o si no es admin.
    */
   public boolean eliminarUsuario(int id) {
       if (!"admin".equalsIgnoreCase(this.rol)) return false;
       return DBConnect.eliminarUsuario(id);
   }


    /**
     * Verifica si las credenciales del usuario son válidas.
     *
     * @return true si el email y la contraseña coinciden con un usuario registrado, false en caso contrario.
     */
    public boolean iniciarSesion() {
        return DBConnect.validarCredenciales(email, password);
    }



    /**
     * Actualiza los atributos del usuario actual con la información almacenada en la base de datos,
     * solo si las credenciales son válidas.
     * No realiza ninguna acción si la autenticación falla o el usuario no existe.
     */
    private void getInfo() {
        if (iniciarSesion()) {
            User usuario = DBConnect.getInfo(this.email); // Obtener el objeto Usuario
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
    public String getNombre() {
        return nombre;
    }
    public String getEmpresa() { return empresa; }
    public String getApellido() {
        return apellido;
    }
    public String getEmail() {
        return email;
    }
    public String getRol() {
        return rol;
    }

}
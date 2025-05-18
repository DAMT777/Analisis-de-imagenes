package com.mycompany.prototiposoftware;

/**
 * Clase utilitaria para almacenar y acceder de forma global a los datos
 * del usuario que ha iniciado sesión en el sistema.
 *
 * <p>Esta clase contiene atributos estáticos que representan los campos
 * personales del usuario autenticado, como su ID, nombres, correo y rol.
 * Al ser estáticos, los datos pueden ser accedidos desde cualquier parte
 * del proyecto sin necesidad de instanciar esta clase.</p>
 *
 * <p>Uso típico:
 * <pre>{@code
 * UserSesionData.setIdUser(123);
 * String nombre = UserSesionData.getFirtsNameUser();
 * }</pre>
 * </p>
 *
 * <p><b>Importante:</b> Esta clase solo debe usarse para almacenar los datos
 * temporales del usuario mientras está logueado en la sesión actual.</p>
 *
 */

import com.processing.User;

public class UserSesionData {

    /** Objeto usuario autenticado (composición). */
    private static User usuario;

    /** Cantidad histórica de lotes analizada por el usuario. */
    private static int lotesCountUser;

    /**
     * Obtiene el objeto Usuario autenticado.
     * @return Usuario autenticado o null si no hay sesión.
     */
    public static User getUsuario() {
        return usuario;
    }

    /**
     * Establece el objeto Usuario autenticado.
     * @param usuario Objeto Usuario.
     */
    public static void setUsuario(User usuario) {
        UserSesionData.usuario = usuario;
    }

    /**
     * Obtiene el ID del usuario autenticado.
     * @return ID del usuario o null si no hay sesión.
     */
    public static Integer getIdUser() {
        return usuario != null ? usuario.getId() : null;
    }

    /**
     * Establece el ID del usuario autenticado.
     * @param idUser ID del usuario.
     */
    public static void setIdUser(int idUser) {
        if (usuario != null) usuario.setId(idUser);
    }

    /**
     * Obtiene el nombre del usuario autenticado.
     * @return Nombre del usuario o null si no hay sesión.
     */
    public static String getFirtsNameUser() {
        return usuario != null ? usuario.getNombre() : null;
    }

    /**
     * Establece el nombre del usuario autenticado.
     * @param nombre Nombre del usuario.
     */
    public static void setFirtsNameUser(String nombre) {
        if (usuario != null) usuario.setNombre(nombre);
    }

    /**
     * Obtiene el apellido del usuario autenticado.
     * @return Apellido del usuario o null si no hay sesión.
     */
    public static String getLastNameUser() {
        return usuario != null ? usuario.getApellido() : null;
    }

    /**
     * Establece el apellido del usuario autenticado.
     * @param apellido Apellido del usuario.
     */
    public static void setLastNameUser(String apellido) {
        if (usuario != null) usuario.setApellido(apellido);
    }

    /**
     * Obtiene el correo electrónico del usuario autenticado.
     * @return Correo electrónico o null si no hay sesión.
     */
    public static String getEmailUser() {
        return usuario != null ? usuario.getEmail() : null;
    }

    /**
     * Establece el correo electrónico del usuario autenticado.
     * @param email Correo electrónico.
     */
    public static void setEmailUser(String email) {
        if (usuario != null) usuario.setEmail(email);
    }

    /**
     * Obtiene el rol del usuario autenticado.
     * @return Rol del usuario o null si no hay sesión.
     */
    public static String getRolUser() {
        return usuario != null ? usuario.getRol() : null;
    }

    /**
     * Establece el rol del usuario autenticado.
     * @param rol Rol del usuario.
     */
    public static void setRolUser(String rol) {
        if (usuario != null) usuario.setRol(rol);
    }

    /**
     * Obtiene la cantidad histórica de lotes analizada por el usuario.
     * @return Cantidad de lotes.
     */
    public static int getLotesCountUser() {
        return lotesCountUser;
    }

    /**
     * Establece la cantidad histórica de lotes analizada por el usuario.
     * @param lotesCountUser Cantidad de lotes.
     */
    public static void setLotesCountUser(int lotesCountUser) {
        UserSesionData.lotesCountUser = lotesCountUser;
    }

    /**
     * Obtiene la cantidad de lotes como String.
     * @return Cantidad de lotes en formato String.
     */
    public static String getLotesCountUserString() {
        return String.valueOf(lotesCountUser);
    }

    /**
     * Establece todos los datos del usuario y la cantidad de lotes.
     * @param usuario Objeto Usuario.
     * @param lotesCountUser Cantidad de lotes.
     */
    public static void setAllUserData(User usuario, int lotesCountUser) {
        setUsuario(usuario);
        setLotesCountUser(lotesCountUser);
    }

    /**
     * Limpia todos los datos de la sesión del usuario.
     *
     * <p>Este método debe llamarse al momento de cerrar sesión
     * o cuando se quiera invalidar la sesión actual del usuario.
     * Todos los campos se restablecen a {@code null}.</p>
     */
    public static void clearSession() {
        usuario = null;
        lotesCountUser = 0;
    }
}
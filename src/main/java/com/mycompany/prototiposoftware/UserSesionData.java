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
 * UserSesionData.setIdUser("123");
 * String id = UserSesionData.getIdUser();
 * }</pre>
 * </p>
 *
 * <p><b>Importante:</b> Esta clase solo debe usarse para almacenar los datos
 * temporales del usuario mientras está logueado en la sesión actual.</p>
 *
 */
public class UserSesionData {

    /** ID del usuario autenticado. */
    private static String idUser;

    /** Primer nombre del usuario. */
    private static String firtsNameUser;

    /** Apellido del usuario. */
    private static String lastNameUser;

    /** Correo electrónico del usuario. */
    private static String emailUser;

    /** Rol asignado al usuario dentro del sistema (ej: admin, operador, etc). */
    private static String rolUser;

    /** Rol asignado al usuario dentro del sistema (ej: admin, operador, etc). */
    private static int lotesCountUser;

    /**
     * Obtiene el ID del usuario autenticado.
     * @return ID del usuario.
     */
    public static String getIdUser() {
        return idUser;
    }

    /**
     * Establece el ID del usuario autenticado.
     * @param idUser ID del usuario.
     */
    public static void setIdUser(String idUser) {
        UserSesionData.idUser = idUser;
    }

    /**
     * Obtiene el primer nombre del usuario.
     * @return Primer nombre del usuario.
     */
    public static String getFirtsNameUser() {
        return firtsNameUser;
    }

    /**
     * Establece el primer nombre del usuario.
     * @param firtsNameUser Primer nombre del usuario.
     */
    public static void setFirtsNameUser(String firtsNameUser) {
        UserSesionData.firtsNameUser = firtsNameUser;
    }




    /**
     * Obtiene el apellido del usuario.
     * @return Apellido del usuario.
     */
    public static String getLastNameUser() {
        return lastNameUser;
    }

    /**
     * Establece el apellido del usuario.
     * @param lastNameUser Apellido del usuario.
     */
    public static void setLastNameUser(String lastNameUser) {
        UserSesionData.lastNameUser = lastNameUser;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return Correo del usuario.
     */
    public static String getEmailUser() {
        return emailUser;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param emailUser Correo del usuario.
     */
    public static void setEmailUser(String emailUser) {
        UserSesionData.emailUser = emailUser;
    }

    /**
     * Obtiene el rol asignado al usuario.
     * @return Rol del usuario.
     */
    public static String getRolUser() {
        return rolUser;
    }

    /**
     * Establece el rol del usuario dentro del sistema.
     * @param rolUser Rol del usuario.
     */
    public static void setRolUser(String rolUser) {
        UserSesionData.rolUser = rolUser;
    }

    public static int getLotesCountUser() {
        return lotesCountUser;
    }

    public static void setLotesCountUser(int lotesCountUser) {
        UserSesionData.lotesCountUser = lotesCountUser;
    }

    public static String getLotesCountUserString() {
        return String.valueOf(lotesCountUser);
    }
    /**
     * Establece todos los datos de la sesión del usuario en un solo método.
     *
     * @param idUser         ID del usuario.
     * @param firtsNameUser  Primer nombre del usuario.
     * @param lastNameUser   Apellido del usuario.
     * @param emailUser      Correo electrónico del usuario.
     * @param rolUser        Rol asignado al usuario.
     * @param lotesCountUser Cantidad historica de lotes analizada por el usuario
     */
    public static void setAllUserData(String idUser, String firtsNameUser, String lastNameUser, String emailUser, String rolUser, int lotesCountUser) {
        setIdUser(idUser);
        setFirtsNameUser(firtsNameUser);
        setLastNameUser(lastNameUser);
        setEmailUser(emailUser);
        setRolUser(rolUser);
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
        idUser = null;
        firtsNameUser = null;
        lastNameUser = null;
        emailUser = null;
        rolUser = null;
    }
}

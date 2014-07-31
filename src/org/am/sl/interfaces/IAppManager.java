package org.am.sl.interfaces;

import java.io.UnsupportedEncodingException;


/**
 * @author Cindy Espínola
 *
 */
public interface IAppManager {
	
	/**
	 * retorna el nombre del usuario que se ha autenticado
	 * @return
	 */
	public String getUsername();
	/**
	 * realiza el envío de los menjajes
	 * @param username usuario autenticado
	 * @param tousername usuario destinatario
	 * @param message mensaje a enviar
	 * @return mensaje a enviar
	 * @throws UnsupportedEncodingException atrapa la exception si el mensaje no ha sido enviado
	 */
	public String sendMessage(String username,String tousername, String message) throws UnsupportedEncodingException;
	/**
	 * @param usernameText recogemos el nombre del usuario que se quiere autenticar
	 * @param passwordText password del usuario que se quiere autenticar
	 * @return string de  amigos del usuario autenticado 
	 * @throws UnsupportedEncodingException 
	 */
	public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException; 
	/**
	 * @param username usuario autenticado
	 * @param message mensaje recibido
	 */
	public void messageReceived(String username, String message);
//	public void setUserKey(String value);
	/**
	 * @return true si el servicio se ha conectado y si no false
	 */
	public boolean isNetworkConnected();
	/**
	 * @return true si el usuario se ha autenticado con exito
	 */
	public boolean isUserAuthenticated();
	/**
	 * @return String con lista de amigos del usuario autenticado
	 */
	public String getLastRawFriendList();
	/**
	 * si se hace click en la opcion de menu salir, cierra la conexion y la aplicacion
	 */
	public void exit();

	/**
	 * @param usernameText EditText con usuario a registrar
	 * @param passwordText Password del usuario a registrar
	 * @param email email del usuario a registrar
	 * @param languageText lenguaje del usuario a registrar
	 * @return retorna un string con este aspecto 
	 * 					"username=" + usernameText +
						"&password=" + passwordText +
						"&action=" + "signUpUser"+
						"&email=" + emailText+
						"&language="+languageText+
						"&";
	 */
	public String signUpUser(String usernameText, String passwordText, String email, String languageText);
	/**
	 * @param friendUsername String con nombre del usuario a buscar y enviar solicitud de amistad
	 * @return string con url con datos de registro de usuario mas info del servidor
	 */
	public String addNewFriendRequest(String friendUsername);
	/**
	 * @param approvedFriendNames nombre de amigos aceptados
	 * @param discardedFriendNames nombre de amigos no aceptados
	 * @return  string con url con datos de amigos de usuario mas info del servidor
	 */
	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames);

	
}

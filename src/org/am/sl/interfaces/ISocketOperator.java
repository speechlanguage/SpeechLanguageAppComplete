package org.am.sl.interfaces;


/**
 * @author Cindy Espínola
 *
 */
public interface ISocketOperator {
	
	/**
	 * envia al servidor la url de la action que queremos realizar
	 * @param params string con url
	 * @return  string con la url
	 */
	public String sendHttpRequest(String params);
	/**
	 * @param port:  numero de puerto 
	 * @return 1 si la conexion se ha iniciado
	 */
	public int startListening(int port);
	/**
	 * ponemos a la variable listening de tipo boolean a false para que acabe con la conexion
	 */
	public void stopListening();
	/**
	 * cierra el Socket
	 */
	public void exit();
	/**
	 * @return retorna el puerto
	 */
	public int getListeningPort();

}

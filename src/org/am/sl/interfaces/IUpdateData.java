package org.am.sl.interfaces;
import org.am.sl.types.FriendInfo;
import org.am.sl.types.MessageInfo;


/**
 * @author Cindy Espínola
 *
 */
public interface IUpdateData {
	/**
	 * se encarga de actualizar los eventos, envio de mensaje, mensajes recibidos amigos aceptados, amigos no aceptados
	 * @param messages array de tipo MessageInfo que contiene datos del mensaje
	 * @param friends array de tipo FrienInfo que contiene datos del usuario amigo
	 * @param unApprovedFriends array de tipo FriendInfo no aceptado
	 * @param userKey numero identificativo del usuario
	 */
	public void updateData(MessageInfo[] messages, FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}

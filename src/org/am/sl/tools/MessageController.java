package org.am.sl.tools;

import org.am.sl.types.MessageInfo;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
/**
 * @author Cindy Espínola
 *
 */
public class MessageController 
{
	
	/**
	 * 
	 */
	private static MessageInfo[] messagesInfo = null;
	
	/**
	 * @param messageInfo
	 */
	public static void setMessagesInfo(MessageInfo[] messageInfo)
	{
		MessageController.messagesInfo = messageInfo;
	}
	
	
	
	/**
	 * @param username
	 * @return
	 */
	public static MessageInfo checkMessage(String username)
	{
		MessageInfo result = null;
		if (messagesInfo != null) 
		{
			for (int i = 0; i < messagesInfo.length;) 
			{
				
					result = messagesInfo[i];
					break;
								
			}			
		}		
		return result;
	}
	
	



	/**
	 * @param username
	 * @return
	 */
	public static MessageInfo getMessageInfo(String username) 
	{
		MessageInfo result = null;
		if (messagesInfo != null) 
		{
			for (int i = 0; i < messagesInfo.length;) 
			{
					result = messagesInfo[i];
					break;
							
			}			
		}		
		return result;
	}






	/**
	 * @return
	 */
	public static MessageInfo[] getMessagesInfo() {
		return messagesInfo;
	}



	
	
	

}

package org.am.sl.tools;

import org.am.sl.types.FriendInfo;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
/**
 * @author Cindy Espínola
 *
 */
public class FriendController 
{
	
	/**
	 * 
	 */
	private static FriendInfo[] friendsInfo = null;
	/**
	 * 
	 */
	private static FriendInfo[] unapprovedFriendsInfo = null;
	/**
	 * 
	 */
	private static String activeFriend;
	
	/**
	 * @param friendInfo
	 */
	public static void setFriendsInfo(FriendInfo[] friendInfo)
	{
		FriendController.friendsInfo = friendInfo;
	}
	
	
	
	/**
	 * @param username
	 * @param userKey
	 * @return
	 */
	public static FriendInfo checkFriend(String username, String userKey)
	{
		FriendInfo result = null;
		if (friendsInfo != null) 
		{
			for (int i = 0; i < friendsInfo.length; i++) 
			{
				if ( friendsInfo[i].userName.equals(username) && 
					 friendsInfo[i].userKey.equals(userKey)
					)
				{
					result = friendsInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}
	
	/**
	 * @param friendName
	 */
	public static void setActiveFriend(String friendName){
		activeFriend = friendName;
	}
	
	/**
	 * @return
	 */
	public static String getActiveFriend()
	{
		return activeFriend;
	}



	/**
	 * @param username
	 * @return
	 */
	public static FriendInfo getFriendInfo(String username) 
	{
		FriendInfo result = null;
		if (friendsInfo != null) 
		{
			for (int i = 0; i < friendsInfo.length; i++) 
			{
				if ( friendsInfo[i].userName.equals(username) )
				{
					result = friendsInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}



	/**
	 * @param unapprovedFriends
	 */
	public static void setUnapprovedFriendsInfo(FriendInfo[] unapprovedFriends) {
		unapprovedFriendsInfo = unapprovedFriends;		
	}



	/**
	 * @return
	 */
	public static FriendInfo[] getFriendsInfo() {
		return friendsInfo;
	}



	/**
	 * @return
	 */
	public static FriendInfo[] getUnapprovedFriendsInfo() {
		return unapprovedFriendsInfo;
	}
	
	
	

}

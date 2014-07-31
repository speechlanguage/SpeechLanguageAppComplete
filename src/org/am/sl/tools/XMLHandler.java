package org.am.sl.tools;

import java.util.Vector;

import org.am.sl.interfaces.IUpdateData;
import org.am.sl.types.FriendInfo;
import org.am.sl.types.MessageInfo;
import org.am.sl.types.STATUS;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/*
 * Parses the xml data to FriendInfo array
 * XML Structure 
 * <?xml version="1.0" encoding="UTF-8"?>
 * 
 * <friends>
 * 		<user key="..." />
 * 		<friend username="..." status="..." IP="..." port="..." key="..." expire="..." />
 * 		<friend username="..." status="..." IP="..." port="..." key="..." expire="..." />
 * </friends>
 *
 *
 *status == online || status == unApproved
 * */

/**
 * @author Cindy Espínola
 *
 */
public class XMLHandler extends DefaultHandler
{
		/**
		 * 
		 */
		private String userKey = new String();
		/**
		 * 
		 */
		private IUpdateData updater;
		
		/**
		 * @param updater
		 */
		public XMLHandler(IUpdateData updater) {
			super();
			this.updater = updater;
		}

		/**
		 * 
		 */
		private Vector<FriendInfo> fFriends = new Vector<FriendInfo>();
		/**
		 * 
		 */
		private Vector<FriendInfo> onlineFriends = new Vector<FriendInfo>();
		/**
		 * 
		 */
		private Vector<FriendInfo> unapprovedFriends = new Vector<FriendInfo>();
		
		/**
		 * 
		 */
		private Vector<MessageInfo> unreadMessages = new Vector<MessageInfo>();

		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
		 */
		public void endDocument() throws SAXException 
		{
			FriendInfo[] friends = new FriendInfo[fFriends.size() + onlineFriends.size()];
			MessageInfo[] messages = new MessageInfo[unreadMessages.size()];
			
			int onlineFriendCount = onlineFriends.size();			
			for (int i = 0; i < onlineFriendCount; i++) 
			{				
				friends[i] = onlineFriends.get(i);
			}
			
						
			int offlineFriendCount = fFriends.size();			
			for (int i = 0; i < offlineFriendCount; i++) 
			{
				friends[i + onlineFriendCount] = fFriends.get(i);
			}
			
			int unApprovedFriendCount = unapprovedFriends.size();
			FriendInfo[] unApprovedFriends = new FriendInfo[unApprovedFriendCount];
			
			for (int i = 0; i < unApprovedFriends.length; i++) {
				unApprovedFriends[i] = unapprovedFriends.get(i);
			}
			
			int unreadMessagecount = unreadMessages.size();
			//Log.i("MessageLOG", "mUnreadMessages="+unreadMessagecount );
			for (int i = 0; i < unreadMessagecount; i++) 
			{
				messages[i] = unreadMessages.get(i);
				Log.i("MessageLOG", "i="+i );
			}
			
			this.updater.updateData(messages, friends, unApprovedFriends, userKey);
			super.endDocument();
		}		
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException 
		{				
			if (localName == "friend")
			{
				FriendInfo friend = new FriendInfo();
				friend.userName = attributes.getValue(FriendInfo.USERNAME);
				String status = attributes.getValue(FriendInfo.STATUS);
				friend.ip = attributes.getValue(FriendInfo.IP);
				friend.port = attributes.getValue(FriendInfo.PORT);
				friend.userKey = attributes.getValue(FriendInfo.USER_KEY);
				//friend.expire = attributes.getValue("expire");
				
				if (status != null && status.equals("online"))
				{					
					friend.status = STATUS.ONLINE;
					onlineFriends.add(friend);
				}
				else if (status.equals("unApproved"))
				{
					friend.status = STATUS.UNAPPROVED;
					unapprovedFriends.add(friend);
				}	
				else
				{
					friend.status = STATUS.OFFLINE;
					fFriends.add(friend);	
				}											
			}
			else if (localName == "user") {
				this.userKey = attributes.getValue(FriendInfo.USER_KEY);
			}
			else if (localName == "message") {
				MessageInfo message = new MessageInfo();
				message.userid = attributes.getValue(MessageInfo.USERID);
				message.sendt = attributes.getValue(MessageInfo.SENDT);
				message.messagetext = attributes.getValue(MessageInfo.MESSAGETEXT);
				Log.i("MessageLOG", message.userid + message.sendt + message.messagetext);
				unreadMessages.add(message);
			}
			super.startElement(uri, localName, name, attributes);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
		 */
		@Override
		public void startDocument() throws SAXException {			
			this.fFriends.clear();
			this.onlineFriends.clear();
			this.unreadMessages.clear();
			super.startDocument();
		}
		
		
}


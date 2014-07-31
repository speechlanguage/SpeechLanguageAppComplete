package org.am.sl.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

import at.vcity.androidim.R;
import org.am.sl.Login;
import org.am.sl.Messaging;
import org.am.sl.communication.SocketOperator;
import org.am.sl.interfaces.IAppManager;
import org.am.sl.interfaces.ISocketOperator;
import org.am.sl.interfaces.IUpdateData;
import org.am.sl.tools.FriendController;
import org.am.sl.tools.LocalStorageHandler;
import org.am.sl.tools.MessageController;
import org.am.sl.tools.XMLHandler;
import org.am.sl.types.FriendInfo;
import org.am.sl.types.MessageInfo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
/**
 * @author Cindy Espínola
 *
 */
public class IService extends Service implements IAppManager, IUpdateData {
	
	/**
	 * nombre del usuario
	 */
	public static String USERNAME;
	/**
	 * string de notificacion
	 */
	public static final String TAKE_MESSAGE = "Take_Message";
	/**
	 * 
	 */
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	/**
	 * 
	 */
	public static final String MESSAGE_LIST_UPDATED = "Take Message List";
	/**
	 * 
	 */
	public ConnectivityManager conManager = null; 
	/**
	 * 
	 */
	private final int UPDATE_TIME_PERIOD = 15000;
//	private static final INT LISTENING_PORT_NO = ;
	/**
	 * 
	 */
	private String rawFriendList = new String();
	/**
	 * 
	 */
	private String rawMessageList = new String();

	/**
	 * 
	 */
	ISocketOperator socketOperator = new SocketOperator(this);

	/**
	 * 
	 */
	private final IBinder mBinder = new IMBinder();
	/**
	 * 
	 */
	private String username;
	/**
	 * 
	 */
	private String password;
	/**
	 * 
	 */
	private boolean authenticatedUser = false;
	 // timer to take the updated data from server
	/**
	 * 
	 */
	private Timer timer;
	

	/**
	 * 
	 */
	private LocalStorageHandler localstoragehandler; 
	
	/**
	 * 
	 */
	private NotificationManager nM;

	/**
	 * @author CinEsMe
	 *
	 */
	public class IMBinder extends Binder {
		public IAppManager getService() {
			return IService.this;
		}
		
	}
	   
    /* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {   	
         nM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

         localstoragehandler = new LocalStorageHandler(this);
        // Display a notification about us starting.  We put an icon in the status bar.
     //   showNotification();
    	conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	new LocalStorageHandler(this);
    	
    	// Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		timer = new Timer();   
		
		Thread thread = new Thread(){
			@Override
			public void run() {			
				
				//socketOperator.startListening(LISTENING_PORT_NO);
				Random random = new Random();
				int tryCount = 0;
				while (socketOperator.startListening(10000 + random.nextInt(20000))  == 0 )
				{		
					tryCount++; 
					if (tryCount > 10)
					{
						break;
					}
					
				}
			}
		};		
		thread.start();
    
    }

/*
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
*/	

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}

    /**
     * @param username
     * @param msg
     */
    private void showNotification(String username, String msg) {       
    	String title = "SpeechLanguage: You got a new Message! (" + username + ")";
 				
    	String text = username + ": " + 
     				((msg.length() < 5) ? msg : msg.substring(0, 5)+ "...");
    	
    	//NotificationCompat.Builder notification = new NotificationCompat.Builder(R.drawable.stat_sample, title,System.currentTimeMillis());
    	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
    	.setSmallIcon(R.drawable.stat_sample)
    	.setContentTitle(title)
    	.setContentText(text); 
    	
    	

        Intent i = new Intent(this, Messaging.class);
        i.putExtra(FriendInfo.USERNAME, username);
        i.putExtra(MessageInfo.MESSAGETEXT, msg);	
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                i, 0);

        mBuilder.setContentIntent(contentIntent); 
        
        mBuilder.setContentText("New message from " + username + ": " + msg);
        
        nM.notify((username+msg).hashCode(), mBuilder.build());
    }
	 

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#getUsername()
	 */
	public String getUsername() {
		return this.username;
	}

	
	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#sendMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String sendMessage(String  username, String  tousername, String message) throws UnsupportedEncodingException {			
		String params = "username="+ URLEncoder.encode(this.username,"UTF-8") +
						"&password="+ URLEncoder.encode(this.password,"UTF-8") +
						"&to=" + URLEncoder.encode(tousername,"UTF-8") +
						"&message="+ URLEncoder.encode(message,"UTF-8") +
						"&action="  + URLEncoder.encode("sendMessage","UTF-8")+
						"&";		
		//Log.d("PARAMS", params);
		return socketOperator.sendHttpRequest(params);		
	}

	
	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getFriendList() throws UnsupportedEncodingException 	{		
		
		 rawFriendList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		 if (rawFriendList != null) {
			 this.parseFriendInfo(rawFriendList);
		 }
		 return rawFriendList;
	}
	
	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getMessageList() throws UnsupportedEncodingException {		
		
		 rawMessageList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		 if (rawMessageList != null) {
			 this.parseMessageInfo(rawMessageList);
		 }
		 return rawMessageList;
	}
	

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#authenticateUser(java.lang.String, java.lang.String)
	 */
	public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{
		this.username = usernameText;
		this.password = passwordText;	
		this.authenticatedUser = false;
		
		String result = this.getFriendList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null && !result.equals(Login.AUTHENTICATION_FAILED)) 
		{			
			this.authenticatedUser = true;
			rawFriendList = result;
			USERNAME = this.username;
			Intent i = new Intent(FRIEND_LIST_UPDATED);					
			i.putExtra(FriendInfo.FRIEND_LIST, rawFriendList);
			sendBroadcast(i);
			
			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						
						Intent i = new Intent(FRIEND_LIST_UPDATED);
						Intent i2 = new Intent(MESSAGE_LIST_UPDATED);
						String tmp = IService.this.getFriendList();
						String tmp2 = IService.this.getMessageList();
						if (tmp != null) {
							i.putExtra(FriendInfo.FRIEND_LIST, tmp);
							sendBroadcast(i);	
							Log.i("friend list broadcast sent ", "");
						
						if (tmp2 != null) {
							i2.putExtra(MessageInfo.MESSAGE_LIST, tmp2);
							sendBroadcast(i2);	
							Log.i("friend list broadcast sent ", "");
						}
						}
						else {
							Log.i("friend list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}
		
		return result;		
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#messageReceived(java.lang.String, java.lang.String)
	 */
	public void messageReceived(String username, String message) 
	{				
		
		//FriendInfo friend = FriendController.getFriendInfo(username);
		MessageInfo msg = MessageController.checkMessage(username);
		if ( msg != null)
		{			
			Intent i = new Intent(TAKE_MESSAGE);
		
			i.putExtra(MessageInfo.USERID, msg.userid);			
			i.putExtra(MessageInfo.MESSAGETEXT, msg.messagetext);			
			sendBroadcast(i);
			String activeFriend = FriendController.getActiveFriend();
			if (activeFriend == null || activeFriend.equals(username) == false) 
			{
				localstoragehandler.insert(username,this.getUsername(), message.toString());
				showNotification(username, message);
			}
			
			Log.i("TAKE_MESSAGE broadcast sent by service", "");
		}	
		
	}  
	
	/**
	 * @param usernameText
	 * @param passwordText
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getAuthenticateUserParams(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{			
		String params = "username=" + URLEncoder.encode(usernameText,"UTF-8") +
						"&password="+ URLEncoder.encode(passwordText,"UTF-8") +
						"&action="  + URLEncoder.encode("authenticateUser","UTF-8")+
						"&port="    + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()),"UTF-8") +
						"&";		
		
		return params;		
	}

	/**
	 * @param value
	 */
	public void setUserKey(String value) 
	{		
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#isNetworkConnected()
	 */
	public boolean isNetworkConnected() {
		return conManager.getActiveNetworkInfo().isConnected();
	}
	
	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#isUserAuthenticated()
	 */
	public boolean isUserAuthenticated(){
		return authenticatedUser;
	}
	
	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#getLastRawFriendList()
	 */
	public String getLastRawFriendList() {		
		return this.rawFriendList;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.i("IMService is being destroyed", "...");
		super.onDestroy();
	}
	
	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#exit()
	 */
	public void exit() 
	{
		timer.cancel();
		socketOperator.exit(); 
		socketOperator = null;
		this.stopSelf();
	}
	
	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#signUpUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String signUpUser(String usernameText, String passwordText,
			String emailText,String languageText) 
	{
		String params = "username=" + usernameText +
						"&password=" + passwordText +
						"&action=" + "signUpUser"+
						"&email=" + emailText+
						"&language="+languageText+
						"&";
		//Log.d("PARAMS", params);
		String result = socketOperator.sendHttpRequest(params);		
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#addNewFriendRequest(java.lang.String)
	 */
	public String addNewFriendRequest(String friendUsername) 
	{
		String params = "username=" + this.username +
		"&password=" + this.password +
		"&action=" + "addNewFriend" +
		"&friendUserName=" + friendUsername +
		"&";

		String result = socketOperator.sendHttpRequest(params);		
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IAppManager#sendFriendsReqsResponse(java.lang.String, java.lang.String)
	 */
	public String sendFriendsReqsResponse(String approvedFriendNames,
			String discardedFriendNames) 
	{
		String params = "username=" + this.username +
		"&password=" + this.password +
		"&action=" + "responseOfFriendReqs"+
		"&approvedFriends=" + approvedFriendNames +
		"&discardedFriends=" +discardedFriendNames +
		"&";

		String result = socketOperator.sendHttpRequest(params);		
		
		return result;
		
	} 
	
	/**
	 * @param xml
	 */
	private void parseFriendInfo(String xml)
	{			
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}
	/**
	 * @param xml
	 */
	private void parseMessageInfo(String xml){			
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.IUpdateData#updateData(org.am.sl.types.MessageInfo[], org.am.sl.types.FriendInfo[], org.am.sl.types.FriendInfo[], java.lang.String)
	 */
	public void updateData(MessageInfo[] messages,FriendInfo[] friends,
			FriendInfo[] unApprovedFriends, String userKey) 
	{
		this.setUserKey(userKey);
		//FriendController.	
		MessageController.setMessagesInfo(messages);
		//Log.i("MESSAGEIMSERVICE","messages.length="+messages.length);
		
		int i = 0;
		while (i < messages.length){
			messageReceived(messages[i].userid,messages[i].messagetext);
			//appManager.messageReceived(messages[i].userid,messages[i].messagetext);
			i++;
		}
		
		
		FriendController.setFriendsInfo(friends);
		FriendController.setUnapprovedFriendsInfo(unApprovedFriends);
		
	}


	
	
	
	
}
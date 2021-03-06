package org.am.sl.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.am.sl.interfaces.IAppManager;
import org.am.sl.interfaces.ISocketOperator;

import android.util.Log;

/**
 * @author Cindy Esp�nola
 *
 */
public class SocketOperator implements ISocketOperator {
	
	private static final String AUTHENTICATION_SERVER_ADDRESS ="http://192.168.215.1/android_im/";
//	private static final String AUTHENTICATION_SERVER_ADDRESS = "http://192.168.219.146/android_im/";

	/**
	 * 
	 */
	private int listeningPort = 0;

	/**
	 * 
	 */
	private static final String HTTP_REQUEST_FAILED = null;

	/**
	 * 
	 */
	private HashMap<InetAddress, Socket> sockets = new HashMap<InetAddress, Socket>();

	/**
	 * 
	 */
	private ServerSocket serverSocket = null;

	/**
	 * 
	 */
	private boolean listening;

	/**
	 * @author CinEsMe
	 *
	 */
	private class ReceiveConnection extends Thread {
		Socket clientSocket = null;

		public ReceiveConnection(Socket socket) {
			this.clientSocket = socket;
			SocketOperator.this.sockets.put(socket.getInetAddress(), socket);
		}

		@Override
		public void run() {
			try {
				// PrintWriter out = new
				// PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					if (inputLine.equals("exit") == false) {
						// appManager.messageReceived(inputLine);
					} else {
						clientSocket.shutdownInput();
						clientSocket.shutdownOutput();
						clientSocket.close();
						SocketOperator.this.sockets.remove(clientSocket
								.getInetAddress());
					}
				}

			} catch (IOException e) {
				Log.e("ReceiveConnection.run: when receiving connection ", "");
			}
		}
	}

	/**
	 * @param appManager
	 */
	public SocketOperator(IAppManager appManager) {
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.ISocketOperator#sendHttpRequest(java.lang.String)
	 */
	public String sendHttpRequest(String params) {
		URL url;
		String result = new String();
		try {
			url = new URL(AUTHENTICATION_SERVER_ADDRESS);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);

			PrintWriter out = new PrintWriter(connection.getOutputStream());

			out.println(params);
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				result = result.concat(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (result.length() == 0) {
			result = HTTP_REQUEST_FAILED;
		}

		return result;

	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.ISocketOperator#startListening(int)
	 */
	public int startListening(int portNo) {
		listening = true;

		try {
			serverSocket = new ServerSocket(portNo);
			this.listeningPort = portNo;
		} catch (IOException e) {

			// e.printStackTrace();
			this.listeningPort = 0;
			return 0;
		}

		while (listening) {
			try {
				new ReceiveConnection(serverSocket.accept()).start();

			} catch (IOException e) {
				// e.printStackTrace();
				return 2;
			}
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.e("Exception server socket",
					"Exception when closing server socket");
			return 3;
		}

		return 1;
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.ISocketOperator#stopListening()
	 */
	public void stopListening() {
		this.listening = false;
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.ISocketOperator#exit()
	 */
	public void exit() {
		for (Iterator<Socket> iterator = sockets.values().iterator(); iterator
				.hasNext();) {
			Socket socket = (Socket) iterator.next();
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) {
			}
		}

		sockets.clear();
		this.stopListening();
	}

	/* (non-Javadoc)
	 * @see org.am.sl.interfaces.ISocketOperator#getListeningPort()
	 */
	public int getListeningPort() {

		return this.listeningPort;
	}

}

import java.io.*;
import java.net.*;


public class Server {
	public static void main(String args[]) {
		new Server().openServer();
	}
	
	ServerSocket providerSocket;
	Socket connection = null;	
	void openServer() {
		try {
				providerSocket = new ServerSocket(4321);
				
				while(true) {
					connection = providerSocket.accept();
					new ConnectionHandler(connection);
				}
				

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	} 
	
}

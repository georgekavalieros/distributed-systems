import java.io.*;
import java.net.*;


public class Reducer {
	public static void main(String args[]) {
		new Reducer().openServer();
	}
	
	ServerSocket providerSocket;
	Socket connection = null;	
	void openServer() {
		try {
				providerSocket = new ServerSocket(4322);
				
				while(true) {
					connection = providerSocket.accept();
					new ReducerHandler(connection);
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

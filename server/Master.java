import java.io.*;
import java.net.*;


public class Master {
	
	public static void main(String args[]) {
		new Master().openServer();
	}
	
	ServerSocket providerSocket;
	Socket connection = null;	
	void openServer() {
		try {
				providerSocket = new ServerSocket(4323);
				while(true) {
					connection = providerSocket.accept();
					new MasterHandler(connection);
					
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
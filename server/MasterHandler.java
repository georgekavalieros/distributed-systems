import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MasterHandler extends Thread implements Runnable{
	private List<POI> poiList = new ArrayList<>();
	private Socket socket;
	
	MasterHandler(Socket con) {
		this.socket = con;
		Thread t = new Thread(this);
		t.start();
	}
	
	
	public void run() {
		double step, latEnd;
		ObjectOutputStream out=null;
		ObjectInputStream in = null;
		Object message = null;
		String ips[] = {"172.16.1.73", "172.16.1.74"};
		Thread[] th;
		String[] searchArea = new String[6];
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
	        in = new ObjectInputStream(socket.getInputStream());
	        int j=0;
	            
	        while(true) {
	        	message = in.readObject();
				if(message.equals("OK")) {
					break;
				}else{
					searchArea[j] = (String)message;
					j++;
				}
			}
	            
	        th = new Thread[2];
	        step = (Double.parseDouble(searchArea[1]) - Double.parseDouble(searchArea[0]))/ips.length;
	        
	        for(int i=0; i<ips.length; i++) {
	           	latEnd = Double.parseDouble(searchArea[0]) + step;
	           	searchArea[1] = String.valueOf(latEnd);
	           	System.out.println("BHKE STO : " + ips[i]);
	           	th[i] = new thread(ips[i], searchArea);
				th[i].start();
				Thread.sleep(1000);
				searchArea[0] = searchArea[1];
	        }
	            
	        for(int i=0; i<th.length; i++){
				th[i].join();
	        }

	        startReducer();
			
	        out.writeObject(poiList);
	        out.close();
	        in.close();
	        socket.close();
	        
	        poiList.clear();
			
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private class thread extends Thread {
		private String[] searchArgs;
		private String thisip;
		
		thread(String ip, String[] params) {
			thisip = ip;
		    searchArgs = params;
		}
		
		@SuppressWarnings("unchecked")
		public void run() {
			Socket mapperSocket=null;
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			Object message = null;
			try {
				System.out.println(thisip);
				mapperSocket = new Socket(thisip, 4321);
				System.out.println("connected");
				out = new ObjectOutputStream(mapperSocket.getOutputStream());
		        in = new ObjectInputStream(mapperSocket.getInputStream());
		        
		        out.writeObject(searchArgs[0]);
	            out.flush();
	            out.writeObject(searchArgs[1]);
	            out.flush();
	            out.writeObject(searchArgs[2]);
	            out.flush();
	            out.writeObject(searchArgs[3]);
	            out.flush();
	            out.writeObject(searchArgs[4]);
	            out.flush();
	            out.writeObject(searchArgs[5]);
	            out.flush();
	            
		        out.writeObject("OK");
		        out.flush();
		        
		        message = in.readObject();
		        poiList.addAll((List<POI>) message);
		        
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}finally {
				try {
					out.close();
					in.close();
					mapperSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}   
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void startReducer() {
		Socket reducerSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		Object message = null;
		try {
			reducerSocket =  new Socket("172.16.1.74", 4322);
			out = new ObjectOutputStream(reducerSocket.getOutputStream());
			in = new ObjectInputStream(reducerSocket.getInputStream());
			
			out.writeObject(poiList);
			
			out.flush();

			message = in.readObject();
			
			poiList = (List<POI>) message;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
				in.close();
				reducerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReducerHandler implements Runnable{
    Socket socket = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    Object message = null;
    List<POI> poiList = new ArrayList<>();
    
    public ReducerHandler(Socket socket) {
		this.socket = socket;
		
		Thread t = new Thread(this);
		t.start();
	}

    @SuppressWarnings("unchecked")
	public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());


            message = in.readObject();
            poiList = (List<POI>) message;

            out.writeObject(poiList.parallelStream()
                    .sorted((p1, p2) -> Integer.compare(p2.getPhotosNumber(), p1.getPhotosNumber()))
                    .limit(5)
                    .collect(Collectors.toList()));
            out.flush();
        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
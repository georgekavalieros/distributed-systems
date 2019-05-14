import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

class ConnectionHandler implements Runnable {
	private Socket socket;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private Object o = null;
	private ArrayList<Object> params = new ArrayList<>();
	private List<POI> poiList = new ArrayList<>();
	
	
	private double searchLatStart, searchLatEnd, searchLongStart, searchLongEnd;
	private String searchTimeStart, searchTimeEnd;
	
	public ConnectionHandler(Socket socket) {
		this.socket = socket;
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			
			while(true) {
				o = in.readObject();
				System.out.println(o);
				if(o.equals("OK")) {
					break;
				}else{
					params.add(o);
				}
			}
			searchLatStart = Double.parseDouble((String) params.get(0));
			searchLatEnd = Double.parseDouble((String) params.get(1));
			searchLongStart = Double.parseDouble((String) params.get(2));
			searchLongEnd = Double.parseDouble((String) params.get(3));
			searchTimeStart = (String) params.get(4);
			searchTimeEnd =  (String) params.get(5);
			
			out.writeObject(select());
			out.flush();
			
			in.close();
			out.close();
			socket.close();
			
			 System.out.println("Finito");
			
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://83.212.117.76:3306/ds_systems_2016";
            String username = "omada4";
            String pass = "omada4db";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, pass);
            return conn;
        } catch(Exception e) {
           e.printStackTrace();
        }
        return null;
    }
	
	 public List<POI> select() {
	        try {
	            Connection con = getConnection();
	            PreparedStatement stmt = con.prepareStatement("SELECT POI,photos,POI_name,latitude,longitude FROM checkins WHERE (latitude BETWEEN " + searchLatStart + " AND " + searchLatEnd +") AND (longitude BETWEEN " + searchLongStart + " AND " + searchLongEnd + ") AND (time BETWEEN CAST('" + searchTimeStart + "' AS DATE) AND CAST('" + searchTimeEnd + "' AS DATE))");
	            
	            ResultSet result = stmt.executeQuery();
	            while(result.next()) {
					addPOI(new POI(result.getString("POI"),result.getString("photos"), result.getDouble("longitude"),result.getDouble("latitude"),result.getString("POI_name")));
	            }
	        }catch(Exception e) {
	            e.printStackTrace();
	        }
	        
	        return poiList.parallelStream()
	        		 .sorted((p1, p2) -> Integer.compare(p2.getPhotosNumber(), p1.getPhotosNumber()))
	        		 .collect(Collectors.toList());
	        		 
	    }

	public void addPOI(POI poi) {
		if(!contains(poi)) {
			poiList.add(poi);
			poi.setPhotosNumber();
		}
	}

	public boolean contains(POI poi) {
		for(POI p: poiList) {
			if (p.getPoiName().equals(poi.getPoiName())) {
				p.updatePhotosNumber(poi.getPhoto());
				return true;
			}
		}
		return false;
	}

}

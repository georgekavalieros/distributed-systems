package com.example.p3120065.findit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Setup extends Activity {
    EditText latS,latE,lngS,lngE,timeS,timeE;
    TextView progress;
    Button con;
    List<POI> poiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        latS = (EditText) findViewById(R.id.latS);
        latE = (EditText) findViewById(R.id.latE);
        lngS = (EditText) findViewById(R.id.lngS);
        lngE = (EditText) findViewById(R.id.lngE);
        timeS = (EditText) findViewById(R.id.timeS);
        timeE = (EditText) findViewById(R.id.timeE);

        progress = (TextView) findViewById(R.id.progress);

        con = (Button) findViewById(R.id.connect);
        con.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new connect().execute();
            }

        });
    }

    private class connect extends AsyncTask<List<POI>,String,List<POI>> {
        String[] searchArea = {latS.getText().toString(),
                latE.getText().toString(), lngS.getText().toString(), lngE.getText().toString() , timeS.getText().toString(), timeE.getText().toString()};

        @Override
        protected List<POI> doInBackground(List<POI>... objects) {
            publishProgress("Please wait...");
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            Object message;
            try {
                requestSocket =  new Socket("192.168.56.1", 4323);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                out.writeObject(searchArea[0]);
                out.flush();
                out.writeObject(searchArea[1]);
                out.flush();
                out.writeObject(searchArea[2]);
                out.flush();
                out.writeObject(searchArea[3]);
                out.flush();
                out.writeObject(searchArea[4]);
                out.flush();
                out.writeObject(searchArea[5]);
                out.flush();

                out.writeObject("OK");
                out.flush();

                message = in.readObject();
                poiList.addAll((List<POI>) message);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            return poiList;
        }

        protected void onPostExecute(List<POI> result) {
            if(poiList.isEmpty()){
                progress.setText("");
                Toast.makeText(Setup.this,"No match found",Toast.LENGTH_LONG).show();
            }else{
                Intent intent = new Intent(Setup.this, MainActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)result);
                intent.putExtra("BUNDLE",args);
                startActivity(intent);
                progress.setText("");
                poiList.clear();
            }
        }

        protected void onProgressUpdate(String... text){
            progress.setText(text[0]);
        }
    }
}

package com.example.mapsicesi2020_2.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mapsicesi2020_2.activity.MapsActivity;
import com.example.mapsicesi2020_2.model.Hole;
import com.example.mapsicesi2020_2.model.Position;
import com.example.mapsicesi2020_2.model.PositionContainer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class TrackUsersWorker extends Thread {

    private final MapsActivity ref;
    private boolean isAlive;

    public TrackUsersWorker(MapsActivity ref){
        this.ref = ref;
        isAlive = true;
    }

    @Override
    public void run() {
        HTTPSWebUtilDomi httpsWebUtilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
        while (true){
            delay(5000);
            String json = httpsWebUtilDomi.GETrequest("https://apps-moviles-d51ed.firebaseio.com/users.json");
            Log.e(">>>", json);
            Type type = new TypeToken< HashMap<String, PositionContainer>>(){}.getType();
            HashMap<String, PositionContainer> users = gson.fromJson(json, type);
            HashMap<String, PositionContainer> holes = gson.fromJson(json,type);
            ArrayList<Position> positions = new ArrayList<>();
            if(users.size() > 0 ) {
                users.forEach((key, value) -> {
                    //Log.e(">>>",key);
                    PositionContainer positionContainer = value;
                    double lat = positionContainer.getLocation().getLat();
                    double lng = positionContainer.getLocation().getLng();
                    positions.add(new Position(lat, lng));
                    ref.getUserNames().add(key);
                });
            }

            ref.updateMarkers(positions);
        }
    }


    public void delay(long time){
        try {
            Thread.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void finish(){
        this.isAlive = false;
    }

/*
    @Override
    protected String doInBackground(Void... voids) {
        HTTPSWebUtilDomi httpsWebUtilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
        while (true){
            delay(5000);
            String json = httpsWebUtilDomi.GETrequest("https://apps-moviles-d51ed.firebaseio.com/users.json");
            Log.e(">>>", json);
            Type type = new TypeToken< HashMap<String, PositionContainer>>(){}.getType();
            HashMap<String, PositionContainer> users = gson.fromJson(json, type);
            HashMap<String, PositionContainer> holes = gson.fromJson(json,type);
            ArrayList<Position> positions = new ArrayList<>();
            if(users.size() > 0 ) {
                users.forEach((key, value) -> {
                    //Log.e(">>>",key);
                    PositionContainer positionContainer = value;
                    double lat = positionContainer.getLocation().getLat();
                    double lng = positionContainer.getLocation().getLng();
                    positions.add(new Position(lat, lng));
                });
            }

            ref.updateMarkers(positions);
        }
    }

 */
}

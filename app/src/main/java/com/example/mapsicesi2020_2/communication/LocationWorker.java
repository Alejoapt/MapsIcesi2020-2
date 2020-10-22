package com.example.mapsicesi2020_2.communication;

import com.example.mapsicesi2020_2.activity.MapsActivity;
import com.example.mapsicesi2020_2.model.Position;
import com.google.gson.Gson;

public class LocationWorker extends Thread{

    private final MapsActivity ref;
    private Boolean isAlive;

    public LocationWorker(MapsActivity ref){
        this.ref = ref;
        isAlive = true;
    }

    @Override
    public void run() {
        HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
        while (isAlive) {
            delay(3000);
            //PositionPut
            if(ref.getMyMarker() != null) {
                ref.runOnUiThread(
                        ()->{

                        }
                );
               // Position pos = new Position(ref.getMyMarker().getPosition().latitude, ref.getMyMarker().getPosition().longitude);

                utilDomi.PUTrequest("https://apps-moviles-d51ed.firebaseio.com/users/" + ref.getUser() + "/location.json", gson.toJson(ref.getMyMarker()));
                //"https://apps-moviles-d51ed.firebaseio.com/users" + ref.getUser() + "/location/"
            }
        }
    }

    public void delay(long time){
            try {
                Thread.sleep(time);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

    }

    public void finish() {
        this.isAlive = false;
    }
}

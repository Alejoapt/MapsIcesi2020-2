package com.example.mapsicesi2020_2.communication;

import com.example.mapsicesi2020_2.activity.MapsActivity;
import com.example.mapsicesi2020_2.model.Hole;
import com.google.gson.Gson;

import java.util.UUID;

public class HolesWorker extends Thread{

    private MapsActivity ref;
    private Boolean isAlive;

    public HolesWorker(MapsActivity ref){
        this.ref = ref;
        isAlive = true;
    }

    @Override
    public void run() {
        Hole holeClass  = ref.getHoleClass();
        Gson gson = new Gson();
        String json = gson.toJson(holeClass);
        HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
        while (isAlive) {
            delay(3000);
            //PositionPut
            if(ref.getMyMarker() != null) {
                ref.runOnUiThread(
                        ()->{

                        }
                );
                // Position pos = new Position(ref.getMyMarker().getPosition().latitude, ref.getMyMarker().getPosition().longitude);

                utilDomi.PUTrequest("https://apps-moviles-d51ed.firebaseio.com/holes/" + holeClass.getId() + "/location.json", gson.toJson(ref.getMyMarker()));
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

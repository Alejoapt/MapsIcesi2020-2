package com.example.mapsicesi2020_2.communication;

import android.os.AsyncTask;

import com.example.mapsicesi2020_2.activity.MapsActivity;
import com.google.gson.Gson;

public class HoleWorker extends AsyncTask<Void, Void, String> {

    private final MapsActivity ref;
    private Boolean isAlive;

    public HoleWorker(MapsActivity ref) {
        this.ref = ref;
        isAlive = true;
    }

    public void delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void finish(){
        this.isAlive = false;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HTTPSWebUtilDomi utilDomi = new HTTPSWebUtilDomi();
        Gson gson = new Gson();
        String s = "";
        while (isAlive) {
            delay(5000);
            //PositionPut
            //if (ref.getCurrentPosition() != null) {
                ref.runOnUiThread(
                        () -> {

                        }
                );
                // Position pos = new Position(ref.getMyMarker().getPosition().latitude, ref.getMyMarker().getPosition().longitude);

                s = utilDomi.PUTrequest("https://apps-moviles-d51ed.firebaseio.com/users/" + ref.getUser() + "/location.json", gson.toJson(ref.getCurrentPosition()));
                //"https://apps-moviles-d51ed.firebaseio.com/users" + ref.getUser() + "/location/"
            //}
        }
        return s;
    }
}

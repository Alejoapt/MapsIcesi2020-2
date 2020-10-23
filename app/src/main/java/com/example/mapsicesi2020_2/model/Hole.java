package com.example.mapsicesi2020_2.model;

import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.maps.model.Marker;

public class Hole {

    private boolean isConfirmed;
    private String id;

    public Hole(String id) {
        isConfirmed = false;
        this.id = id;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

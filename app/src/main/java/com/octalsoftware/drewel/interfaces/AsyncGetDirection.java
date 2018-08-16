package com.octalsoftware.drewel.interfaces;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface AsyncGetDirection {

    void OnDirectionCompleted(ArrayList<LatLng> listLatLng, String state);

    void OnDirectionPathCompleted(ArrayList<LatLng> listLatLng, String state);
}

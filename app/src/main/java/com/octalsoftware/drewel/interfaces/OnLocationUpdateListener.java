package com.octalsoftware.drewel.interfaces;

import android.location.Location;

/**
 * Created by heena on 11/12/17.
 */

public interface OnLocationUpdateListener {
    void onLocationUpdate(Location location, Double latitude, Double Longitude);
}

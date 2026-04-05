package better.blackmove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GPSSpeedTracker {
    private LocationManager locationManager;
    private SpeedListener speedListener;

    public GPSSpeedTracker(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("GPS", "checkSelfPermission Error - Location permissions not granted.");
            // locationManager will remain null
            return;
        }        
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setSpeedListener(SpeedListener speedListener) {
        this.speedListener = speedListener;
    }

    @SuppressLint("MissingPermission")
    public void startTracking() {
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 3, locationListener);
        } else {
            Log.e("GPS", "LocationManager is null. Cannot start tracking without permissions.");
        }
    }

    public void stopTracking() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null && speedListener != null) {
                float speed = location.getSpeed(); // Speed in meters/second
                final int iSpeed = (int) (speed * 3.6f); // Convert to km/h
                speedListener.onSpeedUpdated(iSpeed);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    public interface SpeedListener {
        void onSpeedUpdated(int speed);
    }
}

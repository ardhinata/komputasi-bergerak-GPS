package info.mindbreak.ardhinata.gpstracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Ardhinata Juari (ardhinata.juari@gmail.com) for GPS Tracker on 08/03/16.
 */
public class LocationHandler implements LocationListener {
    private static LocationManager locationManager;
    private static boolean started = false;
    private static LocationHandler locationHandler = new LocationHandler();

    
    private LocationHandler(){
        
    }

    public static LocationHandler getHandler(){
        return locationHandler;
    }

    public void initTracking(Context context) {
        InfoContainer i = new InfoContainer();
        if(!started){
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                i.setStatus("GPS Disabled!");
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            started = true;
            i.setStatus("Enabling...");
        }
    }

    public void stopTracking(Context context) {
        if(started){
            InfoContainer i = new InfoContainer();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(this);
            started=false;
            i.setStatus("Disabled.");
        }
    }
    
    public boolean isStarted(){
        return started;
    }
    
    public void updateStats(Location location){
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        String alt = String.valueOf(location.getAltitude());
        String acc = String.valueOf(location.getAccuracy());

        InfoContainer i = new InfoContainer();
        i.setLat(lat);
        i.setLon(lon);
        i.setAlt(alt);
        i.setAcc(acc);
    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        this.getHandler().updateStats(location);
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String s = "noString";

        switch (status){
            case LocationProvider.OUT_OF_SERVICE:
                s = provider + " is not expected to start";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                s = provider + " lost it's fix";
                break;
            case LocationProvider.AVAILABLE:
                s = provider + " is tracking your movement";
                break;
            default:

                break;
        }
        InfoContainer i = new InfoContainer();
        i.setStatus(s);
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        InfoContainer i = new InfoContainer();
        i.setStatus(provider + " is enabled now");
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        InfoContainer i = new InfoContainer();
        i.setStatus(provider + " is disabled now");
    }
}

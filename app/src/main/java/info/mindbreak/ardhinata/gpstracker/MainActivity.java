package info.mindbreak.ardhinata.gpstracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static LocationManager locationManager;
    private static boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view){
        if(!started){
            this.initTracking(getApplicationContext());
        } else {
            this.stopTracking(getApplicationContext());
        }
    }

    public void changer(InfoContainer infoContainer){
        TextView status = (TextView) findViewById(R.id.textViewGpsStatus);
        TextView lat = (TextView) findViewById(R.id.textViewLatValue);
        TextView lon = (TextView) findViewById(R.id.textViewLonValue);
        TextView alt = (TextView) findViewById(R.id.textViewAltValue);
        TextView acc = (TextView) findViewById(R.id.textViewAccValue);
        Button button = (Button) findViewById(R.id.buttonTrack);
                
        if(infoContainer.isStatusSet()) status.setText(infoContainer.getStatus());
        if(infoContainer.isLatSet()) lat.setText(infoContainer.getLat());
        if(infoContainer.isLonSet()) lon.setText(infoContainer.getLon());
        if(infoContainer.isAltSet()) alt.setText(infoContainer.getAlt());
        if(infoContainer.isAccSet()) acc.setText(infoContainer.getAcc());
        if(infoContainer.isButtonSet()) button.setText(infoContainer.getButton());
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            started = true;
            i.setStatus("Enabling...");
            i.setButton("Disable Tracking");
            this.changer(i);
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
            i.setButton("Enable Tracking!");
            this.changer(i);
        }
    }


    public void updateStats(Location location){
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        String alt = String.valueOf(location.getAltitude());
        String acc = String.valueOf(location.getAccuracy());

        InfoContainer i = new InfoContainer();
        i.setStatus("Tracking your movement...");
        i.setLat(lat);
        i.setLon(lon);
        i.setAlt(alt);
        i.setAcc(acc);
        this.changer(i);
        Log.i("Update Stats", i.toString());
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
        this.updateStats(location);
        Log.i("Location Changed", "Location changed with " + location.getExtras().toString());
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
        this.changer(i);
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
        this.changer(i);
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
        this.changer(i);
    }
}

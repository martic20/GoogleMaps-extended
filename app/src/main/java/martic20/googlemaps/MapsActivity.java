package martic20.googlemaps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/*
A partir del ejercicio de Geolocalización que realizaste,
vamos a realizar un pequeño juego, sobre la ciudad de Barcelona
(en el ejercicio anterior, mostraba en el mapa los 4/5 lugares de interés de Barcelona).
Nuestro juego, tomará la ubicación actual, y cuando el usuario llegue a la
ubicación marcada en el mapa (a partir de las coordenadas),
nos mostrará una pregunta sobre algo que esté en esa ubicación.
En caso de acertarla, se marcará esa ubicación como resuelta.
Cuando el usuario consiga los 4/5 lugares marcados, saldrá un mensaje de juego completado.

 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;

    private static final int LOCATION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationandAddToMap();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void checkLocationandAddToMap() {
//Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

//Fetching the last known location using the Fus
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

//MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");

//Adding the created the marker on the map
        mMap.addMarker(markerOptions);

    }


/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(41.395253, 2.161658);
        mMap.addMarker(new MarkerOptions().position(sydney).title("La pedrera"));

        LatLng s2 = new LatLng(41.403859, 2.174367);
        mMap.addMarker(new MarkerOptions().position(s2).title("Sagrada Familia"));

        LatLng s3 = new LatLng(41.412005, 2.226315);
        mMap.addMarker(new MarkerOptions().position(s3).title("Parc del fórum"));

        LatLng s4 = new LatLng(41.391846, 2.164818);
        mMap.addMarker(new MarkerOptions().position(s4).title("Casa Batlló"));

    }
*/
}
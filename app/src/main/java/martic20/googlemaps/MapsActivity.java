package martic20.googlemaps;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderApi mFusedLocationClient;
    private boolean mRequestingLocationUpdates;
    private ArraySet<MarkerOptions> points;
    private int resueltos = 0;

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        points = new ArraySet<MarkerOptions>();
        points.add(new MarkerOptions().position(new LatLng(41.395253, 2.161658)).title("La pedrera").zIndex(1));
        points.add(new MarkerOptions().position(new LatLng(41.403859, 2.174367)).title("Sagrada Familia").zIndex(2));
        points.add(new MarkerOptions().position(new LatLng(41.412005, 2.226315)).title("Parc del fórum").zIndex(3));
        points.add(new MarkerOptions().position(new LatLng(41.391846, 2.164818)).title("Casa Batlló").zIndex(4));
        points.add(new MarkerOptions().position(new LatLng(41.416265, 2.199107)).title("Joan d'austria").zIndex(5));

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mFusedLocationClient = LocationServices.FusedLocationApi;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(), location.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        updateValuesFromBundle(savedInstanceState);

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
    }

    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            googleApiClient.connect();
        }
        //mFusedLocationClient.requestLocationUpdates(mGoogleApiClient ,        mLocationCallback,                null /* Looper */);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }


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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        Location location = mFusedLocationClient.getLastLocation(googleApiClient);
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 12.0f));
        for (MarkerOptions point : points) {
            mMap.addMarker(point);
        }
        mMap.addCircle(new CircleOptions().center(markerOptions.getPosition()).fillColor(Color.BLUE).radius(10).strokeColor(Color.BLUE));

        checkQuestion(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        checkQuestion(location);
    }

    public void checkQuestion(Location location) {
        for (MarkerOptions point : points) {
            if (isSamePlace(location, point)) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                resueltos++;
                                if (resueltos >= 4) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                    builder.setMessage("Felicitats!! ja has respost correctament 4 preguntes!!").show();
                                }
                                break;
                        }
                    }
                };
                /*
                title("La pedrera").zIndex(1));
                title("Sagrada Familia").zIndex(2));
                title("Parc del fórum").zIndex(3));
                title("Casa Batlló").zIndex(4));
                title("Joan d'austria").zIndex(5));*/
                String question = "Hi ha hagut un error. Depén de que responguis pots obtenir punts extra";
                switch ((int) point.getZIndex()) {
                    case 1:
                        question = "Hi ha una empresa d'aplicacions mòbils a la Pedrera?";
                        break;
                    case 2:
                        question = "Acabaran la Sagrada familia?";
                        break;
                    case 3:
                        question = "És el millor lloc on anar per les festes de la Mercé";
                        break;
                    case 4:
                        question = "És una obra d'Antoni Gaudí?";
                        break;
                    case 5:
                        question = "Que potser ets alumne del Joan d'Austria?";
                        break;
                    default:
                        break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(question).setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
    }

    public boolean isSamePlace(Location location, MarkerOptions marker) {
        float[] distance = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getPosition().latitude, marker.getPosition().longitude, distance);
        // distance[0] is now the distance between these lat/lons in meters
        if (distance[0] < 40.0) {
            return true;
        }
        return false;
    }
}
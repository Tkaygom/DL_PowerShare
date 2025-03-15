package com.firsthachathoners.powershare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.gms.location.LocationRequest;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
//import com.firsthachathoners.powershare.LocationRequest; // Custom class
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.core.view.GravityCompat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.firsthachathoners.powershare.HelpBottomSheetFragment;




public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Map components
    private GoogleMap mMap;
    private UiSettings uiSettings;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    // Location tracking
    private static final int REQUEST_FINE_LOCATION = 1;
    private long UPDATE_INTERVAL = 10000;
    private long FASTEST_INTERVAL = 4000;
    private LatLng myLoc, destPost;
    private Marker marker;
    private Marker destMarker;

    // UI components
    private DrawerLayout drawerLayout;
    private EditText rangeTxt;
    private Polyline paths;
    private Marker[] sMarker;
    private Button myAccountButton;

    // State flags
    private boolean isStationFound = false;
    private boolean isPathSet = false;
    private boolean isDestSet = false;

    // API clients
    private HTTPInterface httpInterface;
    private int zoomMX = 1;
    public List<Result> stations;



    @Override
    protected void onResume() {
        super.onResume();
        // Refresh login state (optional, as your onClick checks prefs every time)
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawer_layout);
        rangeTxt = findViewById(R.id.rng);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myAccountButton = findViewById(R.id.myAccountButton);

        // Setup map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Setup menu button
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Account button click listener
        myAccountButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            String storedUsername = prefs.getString("username", null); // Get saved username

            if (isLoggedIn && storedUsername != null) {
                // User is logged in, open AccountActivity
                Intent intent = new Intent(MapsActivity.this, AccountActivity.class);
                intent.putExtra("USERNAME", storedUsername);
                intent.putExtra("CREDIT_SESSION", prefs.getString("CREDIT_SESSION", "No Credit Info"));
                startActivity(intent);
            } else {
                // Show login reminder if user is not logged in
                showReminderDialog();
            }
        });





        // Check location permissions
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .apiKey(getString(R.string.directionsApiKey))
                .queryRateLimit(3)
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time: " + results.routes[0].legs[0].duration.humanReadable +
                " Distance: " + results.routes[0].legs[0].distance.humanReadable;
    }

    private DirectionsResult prepareRequest(LatLng dest) throws Exception {
        if (dest == null) return null;

        Instant now = Instant.now();
        return DirectionsApi.newRequest(getGeoContext())
                .mode(TravelMode.WALKING)
                .origin(myLoc.latitude + "," + myLoc.longitude)
                .destination(dest.latitude + "," + dest.longitude)
                .departureTime(now)
                .await();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.equals(this.marker)) {
            popAlertDialog(marker);
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    }
                },
                getMainLooper());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        uiSettings = mMap.getUiSettings();

        if (checkPermissions()) {
            enableLocationFeatures();
        }
    }

    @SuppressLint("MissingPermission")
    private void enableLocationFeatures() {
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        onLocationChanged(location);
                    }
                })
                .addOnFailureListener(e -> Log.e("MapsActivity", "Error getting location", e));
    }

    public void onLocationChanged(Location location) {
        myLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (marker != null) marker.remove();

        marker = mMap.addMarker(new MarkerOptions()
                .position(myLoc)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .snippet("Searching for power banks..."));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16f));
        updateStationMarkers();
    }

    private void updateStationMarkers() {
        httpInterface = APIClient.getClient().create(HTTPInterface.class);

        ApiLocationRequest request = new ApiLocationRequest();
        request.longitude = myLoc.longitude;
        request.latitude = myLoc.latitude;
        request.range = zoomMX * 1000;

        Call<JSONData> call = httpInterface.getAllRecords(request);

        call.enqueue(new Callback<JSONData>() {
            @Override
            public void onResponse(Call<JSONData> call, Response<JSONData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stations = response.body().getResult();
                    if (!isStationFound && !stations.isEmpty()) {
                        fillMapWithStations(stations);
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONData> call, Throwable t) {
                Log.e("MapsActivity", "API call failed", t);
            }
        });
    }

    private void fillMapWithStations(List<Result> stations) {
        sMarker = new Marker[stations.size()];
        for (int i = 0; i < stations.size(); i++) {
            Result station = stations.get(i);
            LatLng position = new LatLng(station.getLocation().get(1), station.getLocation().get(0));
            sMarker[i] = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(station.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_power_settings_new_black_24dp)));
        }
        isStationFound = true;
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }

    public void setR(View view) {
        try {
            String input = rangeTxt.getText().toString().trim();
            zoomMX = input.isEmpty() ? 1 : Integer.parseInt(input);
            rangeTxt.setText(String.valueOf(zoomMX));
        } catch (NumberFormatException e) {
            zoomMX = 1;
            rangeTxt.setText(String.valueOf(zoomMX));
            e.printStackTrace();
        }
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        if (results != null && results.routes.length > 0) {
            List<LatLng> decodedPath = PolyUtil.decode(
                    results.routes[0].overviewPolyline.getEncodedPath()
            );
            if (paths != null) paths.remove();
            paths = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        }
    }

    public boolean isReachedDest() {
        if (myLoc == null || destPost == null) return false;

        float[] results = new float[1];
        Location.distanceBetween(
                myLoc.latitude, myLoc.longitude,
                destPost.latitude, destPost.longitude,
                results
        );
        return results[0] < 150;
    }

    public void popAlertDialogIsReached() {
        new AlertDialog.Builder(this)
                .setTitle("Destination Reached")
                .setMessage("Have you returned the power bank?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (paths != null) paths.remove();
                    if (destMarker != null) destMarker.remove();
                    isDestSet = false;
                    isPathSet = false;
                    destPost = null;
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void popAlertDialog(final Marker tapMarker) {
        if (tapMarker.equals(marker) || (destMarker != null && tapMarker.equals(destMarker))) return;

        new AlertDialog.Builder(this)
                .setTitle("Navigate to station")
                .setMessage("Get directions to this station?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (paths != null) paths.remove();
                    if (destMarker != null) destMarker.remove();

                    destPost = tapMarker.getPosition();
                    isDestSet = true;
                    isPathSet = false;

                    new Thread(() -> {
                        try {
                            DirectionsResult results = prepareRequest(destPost);
                            runOnUiThread(() -> {
                                addPolyline(results, mMap);
                                destMarker = mMap.addMarker(new MarkerOptions()
                                        .position(destPost)
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_ORANGE))
                                        .title("Destination")
                                        .snippet(getEndLocationTitle(results)));
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Log.e("Directions", "Error getting directions", e));
                        }
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }


    public class MapActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps); // Verify this XML exists

            ImageButton btnHelp = findViewById(R.id.btnHelp);
            btnHelp.setOnClickListener(v -> {
                new HelpBottomSheetFragment().show(getSupportFragmentManager(), "HELP_SHEET_TAG");
            });
        }
    }


    private void showReminderDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Required")
                .setMessage("You need to login to access this feature")
                .setPositiveButton("Login", (dialog, which) -> {
                    Log.d("MapsActivity", "Login button clicked, launching LoginActivity...");
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}


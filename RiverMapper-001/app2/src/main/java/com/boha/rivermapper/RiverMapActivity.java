package com.boha.rivermapper;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.boha.rivermapper.dto.RequestDTO;
import com.boha.rivermapper.dto.ResponseDTO;
import com.boha.rivermapper.dto.RiverDTO;
import com.boha.rivermapper.dto.RiverPartDTO;
import com.boha.rivermapper.dto.RiverPointDTO;
import com.boha.rivermapper.toolbox.BaseVolley;
import com.boha.rivermapper.toolbox.CacheUtil;
import com.boha.rivermapper.toolbox.Statics;
import com.boha.rivermapper.toolbox.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class RiverMapActivity extends AppCompatActivity
        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    Location location;
    Context ctx;

    SeekBar seekBar;
    TextView txtRadius;
    ImageView imgSearch;
    Spinner spinner;

    List<Marker> markers = new ArrayList<Marker>();
    static final String LOG = RiverMapActivity.class.getSimpleName();
    boolean mResolvingError;
    static final long ONE_MINUTE = 1000 * 60;
    static final long FIVE_MINUTES = 1000 * 60 * 5;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    static final int RADIUS = 10;
    RiverDTO river;
    int index;
    TextView txtCount;
    View topLayout;
    Activity activity;
    ProgressBar progressBar;
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "############### onCreate ........................");
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        activity = this;
        try {
            setContentView(R.layout.activity_monitor_map);
        } catch (Exception e) {
            Log.e(LOG, "######## cannot setContentView", e);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        setFields();

        setTitle("River Finder");

        googleMap = mapFragment.getMap();
        if (googleMap == null) {
            finish();
            return;
        }
        setGoogleMap();
        getCachedData();
        Util.setCustomActionBar(ctx,getSupportActionBar(),
                "River Finder", ctx.getResources().getDrawable(R.mipmap.logo2));
    }


    private void setFields() {
        txtCount = (TextView) findViewById(R.id.MC_rivers);
        txtCount.setText("0");
        progressBar = (ProgressBar) findViewById(R.id.MC_progress);
        progressBar.setVisibility(View.GONE);
        spinner = (Spinner) findViewById(R.id.MC_spinner);
        txtRadius = (TextView) findViewById(R.id.MC_radius);
        imgSearch = (ImageView) findViewById(R.id.MC_search);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        topLayout = findViewById(R.id.top);
        seekBar = (SeekBar) findViewById(R.id.MC_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < RADIUS) {
                    seekBar.setProgress(RADIUS);
                }
                txtRadius.setText("" + seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRiversAroundMe();
            }
        });
    }

    private void setGoogleMap() {
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);

        //TODO - remove after test
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                Log.w(LOG, "$$$ onMapClick lat: " + latLng.latitude + " lng: " + latLng.longitude);
                getRiversAroundMe();

            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                final Location loc = new Location(location);
                loc.setLatitude(latLng.latitude);
                loc.setLongitude(latLng.longitude);

                float mf = location.distanceTo(loc);
                Log.w(LOG, "######### distance, again: " + mf);

                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("River Point - " + marker.getTitle())
                        .setMessage("Do you want directions to this point in the river (" + marker.getTitle() + ") ?\n\n" +
                                "You are about an estimated " + getDistance(mf) + "  from this river point. (Distance calculated as the crow flies!)")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startDirectionsMap(loc.getLatitude(), loc.getLongitude());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });

    }

    static final DecimalFormat df = new DecimalFormat("###,###,###,###,##0.0");

    private String getDistance(float mf) {
        if (mf < 1000) {
            return df.format(mf) + " metres";
        }
        Double m = Double.parseDouble("" + mf);
        Double n = m / 1000;

        return df.format(n) + " kilometres";

    }

    ResponseDTO response;
    boolean isBusy;

    private void getCachedData() {
        CacheUtil.getCachedRiverData(ctx, new CacheUtil.CacheUtilListener() {
            @Override
            public void onFileDataDeserialized(ResponseDTO r) {
                response = r;
                if (!r.getRiverList().isEmpty()) {
                    if (r.getRiverList().get(0).getRiverPartList() != null
                            && !r.getRiverList().get(0).getRiverPartList().isEmpty()) {
                        txtCount.setText("" + response.getRiverList().size());
                        setRiverSpinner();
                    }
                } else {
                    getRiversAroundMe();
                }
            }

            @Override
            public void onDataCached(ResponseDTO response) {
            }

            @Override
            public void onError() {
                getRiversAroundMe();
            }
        });
    }

    private void getRiversAroundMe() {
        if (location == null) {
            return;
        }
        if (isBusy) {
            Toast.makeText(ctx, "Busy...getting rivers ...", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_RIVERS_BY_RADIUS);
        w.setLatitude(location.getLatitude());
        w.setLongitude(location.getLongitude());
        w.setRadius(seekBar.getProgress());

        rotateSearch();
        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        BaseVolley.getRemoteData(Statics.RIVERS_SERVLET, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                isBusy = false;
//                progressBar.setVisibility(View.GONE);
                if (r.getStatusCode() > 0) {
                    Toast.makeText(ctx, r.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (r.getRiverList().isEmpty()) {
                    Toast.makeText(ctx, "No rivers found within 20 km", Toast.LENGTH_LONG).show();
                    return;
                }

                CacheUtil.cacheRiverData(ctx, r, new CacheUtil.CacheUtilListener() {
                    @Override
                    public void onFileDataDeserialized(ResponseDTO r) {

                    }

                    @Override
                    public void onDataCached(ResponseDTO r) {
                        response = r;
                        txtCount.setText("" + response.getRiverList().size());
                        setRiverSpinner();
                    }

                    @Override
                    public void onError() {
                        Log.e(LOG, "We have a fucking cache error!");
                    }
                });
            }

            @Override
            public void onVolleyError(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                isBusy = false;
                Toast.makeText(ctx, "Problem: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setRiverSpinner() {
        List<String> list = new ArrayList<>();
        calculateDistances();
        for (RiverDTO d : response.getRiverList()) {
            list.add(d.getRiverName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, R.layout.spinner_text, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                river = response.getRiverList().get(position);
                setRiverMarkers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void calculateDistances() {
        if (location != null) {
            List<RiverPointDTO> riverPoints = new ArrayList<>();
            Location spot = new Location(LocationManager.GPS_PROVIDER);
            for (RiverDTO w : response.getRiverList()) {
                for (RiverPartDTO x : w.getRiverPartList()) {
                    for (RiverPointDTO y : x.getRiverPointList()) {
                        spot.setLatitude(y.getLatitude());
                        spot.setLongitude(y.getLongitude());
                        y.setDistanceFromMe(location.distanceTo(spot));
                    }
                    Collections.sort(x.getRiverPointList());
                    x.setNearestLatitude(x.getRiverPointList().get(0).getLatitude());
                    x.setNearestLongitude(x.getRiverPointList().get(0).getLongitude());
                    x.setDistanceFromMe(x.getRiverPointList().get(0).getDistanceFromMe());
                }
                Collections.sort(w.getRiverPartList());
                w.setNearestLatitude(w.getRiverPartList().get(0).getNearestLatitude());
                w.setNearestLongitude(w.getRiverPartList().get(0).getNearestLongitude());
                w.setDistanceFromMe(w.getRiverPartList().get(0).getDistanceFromMe());


            }
            Collections.sort(response.getRiverList());
        }
    }

    ObjectAnimator an;
    public  void rotateSearch() {
        an =  ObjectAnimator.ofFloat(imgSearch, "rotation", 0.0f, 360f);
        an.setRepeatCount(ObjectAnimator.INFINITE);
        an.setInterpolator(new AccelerateDecelerateInterpolator());
        an.start();
    }
    private void setRiverMarkers() {
        googleMap.clear();
        int index = 0, count = 0;

        if (river == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        BitmapDescriptor desc = BitmapDescriptorFactory.fromResource(R.drawable.dot_red_tiny);
        for (RiverPartDTO riverPart : river.getRiverPartList()) {
            for (RiverPointDTO x : riverPart.getRiverPointList()) {
                LatLng pnt = new LatLng(x.getLatitude(), x.getLongitude());
                Marker m =
                        googleMap.addMarker(new MarkerOptions()
                                .title(riverPart.getRiverName())
                                .icon(desc)
                                .snippet("Points: " + riverPart.getRiverPointList().size())
                                .position(pnt));
                markers.add(m);
                index++;
                count++;
            }
        }
        double lat = river.getNearestLatitude();
        double lng = river.getNearestLongitude();
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 12f);
        googleMap.animateCamera(cu);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //ensure that all markers in bounds
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                progressBar.setVisibility(View.GONE);
                LatLngBounds bounds = builder.build();
                int padding = 60; // offset from edges of the map in pixels
                double lat = river.getNearestLatitude();
                double lng = river.getNearestLongitude();
                LatLng latLng = new LatLng(lat, lng);
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 12f);
                googleMap.animateCamera(cu);

            }
        });


    }



    private void startDirectionsMap(double lat, double lng) {
        Log.i(LOG, "startDirectionsMap ..........");
        String url = "http://maps.google.com/maps?saddr="
                + location.getLatitude() + "," + location.getLongitude()
                + "&daddr=" + lat + "," + lng + "&mode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.MAP_refresh) {
            getRiversAroundMe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static final int ACCURACY_LIMIT = 50;

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location.getAccuracy() <= ACCURACY_LIMIT) {
            stopLocationUpdates();
            getRiversAroundMe();
        }
        Log.e(LOG, "####### onLocationChanged");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG, "################ onStart .... connect API and location clients ");
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        Log.w(LOG, "############## onStop stopping google service clients");
        try {
            //mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(LOG, "Failed to Stop something", e);
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG, "+++  onConnected() -  requestLocationUpdates ...");
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.w(LOG, "## requesting location ....lastLocation: "
                    + location.getLatitude() + " "
                    + location.getLongitude() + " acc: "
                    + location.getAccuracy());
        }
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);

        if (location.getAccuracy() > ACCURACY_LIMIT) {
            startLocationUpdates();
        } else {
            getRiversAroundMe();
        }

    }

    boolean mRequestingLocationUpdates;

    protected void startLocationUpdates() {
        Log.w(LOG, "###### startLocationUpdates: " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        Log.e(LOG, "###### stopLocationUpdates - " + new Date().toString());
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    List<BitmapDescriptor> bmdList = new ArrayList<BitmapDescriptor>();


    boolean coordsConfirmed;

    @Override
    public void onBackPressed() {
        Log.e(LOG, "######## onBackPressed, coordsConfirmed: " + coordsConfirmed);
        if (coordsConfirmed) {
            Intent i = new Intent();
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

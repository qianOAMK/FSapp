package com.li.qian.app;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacePickerActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // client object for connecting to the Google API
    private GoogleApiClient mGoogleApiClient;

    // TextView for displaying the current location
    private TextView snapToPlace;

    //  RecyclerView and associated objects for displaying the nearby coffee spots
    private RecyclerView placePicker;
    private LinearLayoutManager placePickerManager;
    private RecyclerView.Adapter placePickerAdapter;

    // The base URL for the Foursquare API
    private String foursquareBaseURL = "https://api.foursquare.com/v2/";

    // client ID and client secret for authenticating with the Foursquare API
    private String foursquareClientID;
    private String foursquareClientSecret;
    private String FSquery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // visible TextView and RecyclerView objects
        snapToPlace = (TextView)findViewById(R.id.snapToPlace);
        placePicker = (RecyclerView)findViewById(R.id.queryList);

        // Sets the dimensions, LayoutManager, and dividers for the RecyclerView
        placePicker.setHasFixedSize(true);
        placePickerManager = new LinearLayoutManager(this);
        placePicker.setLayoutManager(placePickerManager);
        placePicker.addItemDecoration(new DividerItemDecoration(placePicker.getContext(), placePickerManager.getOrientation()));
        placePicker.setAdapter(placePickerAdapter);
        // Creates a connection to the Google API for location services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Gets the stored Foursquare API client ID and client secret from XML
        foursquareClientID = getResources().getString(R.string.foursquare_client_id);
        foursquareClientSecret = getResources().getString(R.string.foursquare_client_secret);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // Checks for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Makes a Google API request for the user's last known location
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                // The user's current latitude, longitude, and location accuracy
                String userLL = mLastLocation.getLatitude() + "," +  mLastLocation.getLongitude();
                double userLLAcc = mLastLocation.getAccuracy();

                // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(foursquareBaseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                FoursquareService foursquare = retrofit.create(FoursquareService.class);

                // Calls the Foursquare API to snap the user's location to a Foursquare venue
                Call<FoursquareJSON> stpCall = foursquare.snapToPlace(
                        foursquareClientID,
                        foursquareClientSecret,
                        userLL,
                        userLLAcc);



                stpCall.enqueue(new Callback<FoursquareJSON>() {
                    @Override
                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                        // Gets the venue object from the JSON response
                        FoursquareJSON fjson = response.body();
                        FoursquareResponse fr = fjson.response;
                        List<FoursquareVenue> frs = fr.venues;
                        FoursquareVenue fv = frs.get(0);

                        // Notifies the user of their current location
                        snapToPlace.setText("You are  now at " + fv.name + ". Here's some venues nearby.");
                    }

                    @Override
                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "App can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                // Calls the Foursquare API to explore nearby venues
                FSquery=MainActivity.query;
                Call<FoursquareJSON> venuesCall = foursquare.searchVenues(
                        foursquareClientID,
                        foursquareClientSecret,
                        userLL,
                        userLLAcc,
                        FSquery);



                venuesCall.enqueue(new Callback<FoursquareJSON>() {
                    @Override
                    public void onResponse(Call<FoursquareJSON> call, Response<FoursquareJSON> response) {

                        // Gets the venue object from the JSON response
                        FoursquareJSON fjson = response.body();
                        FoursquareResponse fr = fjson.response;
                        FoursquareGroup fg = fr.group;
                        List<FoursquareResults> frs = fg.results;

                        // Displays the results in the RecyclerView
                        placePickerAdapter = new PlacePickerAdapter(getApplicationContext(), frs);
                        placePicker.setAdapter(placePickerAdapter);
                    }

                    @Override
                    public void onFailure(Call<FoursquareJSON> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "App can't connect to Foursquare's servers!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "App can't determine your current location!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reconnects to the Google API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Disconnects from the Google API
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "App can't connect to Google's servers!", Toast.LENGTH_LONG).show();
        finish();
    }
}
package com.hue.helloworld;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener,
        GoogleApiClient.ConnectionCallbacks,OnConnectionFailedListener {
    public final static String EXTRA_MESSAGE = "com.hue.helloworld.MESSAGE";
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final int REQUEST_SELECT_PLACE = 1000;
    private EditText edtText;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Place mSelectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtText = (EditText) findViewById(R.id.edtText);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onSearchAddressClicked(View view) {
        LatLng mLatLng = new LatLng(45.41117, -75.69812);;

        // if we don't have a location then try to get an approximation from
        // the network provider
        if (mLastLocation == null) {
            LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (mLastLocation == null) {
                mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (mLastLocation == null) {
                Log.w(LOG_TAG, "onSearchAddressClicked: last location not known");
                Toast.makeText(this, "Last location not known," +
                                "cannot set place by default to Ottawa, Ontario.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (mLastLocation != null) {
            mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        LatLngBounds mLatLngBounds = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(mLatLng, 20, 0)).
                include(SphericalUtil.computeOffset(mLatLng, 20, 90)).
                include(SphericalUtil.computeOffset(mLatLng, 20, 180)).
                include(SphericalUtil.computeOffset(mLatLng, 20, 270)).build();

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(mLatLngBounds)
                    .build(MainActivity.this);
            startActivityForResult(intent, REQUEST_SELECT_PLACE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        mSelectedPlace = place;
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        edtText.setText(place.getAddress().toString());
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
    }

    /* called when the button 'I want something' is clicked */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText edtText = (EditText) findViewById(R.id.edtText);
        String message = edtText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    /* called when the button 'Show me map' is clicked */
    public void showMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        EditText edtText = (EditText) findViewById(R.id.edtText);
        String message = edtText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "onConnectionFailed: connectionResult = " + connectionResult.toString());
        Toast.makeText(this, "Connection to GoogleApiClient has failed: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) { }
}

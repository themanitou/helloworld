package com.hue.helloworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.IntegerRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private View mapView;
    private MapFragment mapFragment;
    private TextView tvAddress;
    private ImageView imageView;

    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            if (coder.isPresent()) {
                address = coder.getFromLocationName(strAddress, 1);
                if (address == null) {
                    return null;
                }
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
            else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private String getAddressFromLocation(Context context, LatLng latLng) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            if (coder.isPresent()) {
                address = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (address == null || address.size() == 0) {
                    return "Address not found!";
                }
                String result = "";
                for (int i = 0; i < address.get(0).getMaxAddressLineIndex(); i++) {
                    result += address.get(0).getAddressLine(i) + ", ";
                }
                return result;
            }
            else {
                return "Geocoder not present!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "Cannot retrieve address at this location!";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        imageView = (ImageView) findViewById(R.id.imgView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        imageView.setScaleX(0.2f);
                        imageView.setScaleY(0.2f);
                    }
                }
        );
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the MapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraIdleListener(this);
/*
        GroundOverlay groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
            .image(BitmapDescriptorFactory.defaultMarker())
            .transparency(0.5f));
*/

        Intent intent = getIntent();
        String strAddress = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_ADDRESS);
        double latlng[] = intent.getDoubleArrayExtra(MainActivity.EXTRA_MESSAGE_LATLNG);
        if (latlng.length != 2) {
            return;
        }
        LatLng ll = new LatLng(latlng[0],latlng[1]);
        // Add a marker and move the camera
//        mMap.addMarker(new MarkerOptions().position(ll).title(strAddress));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));

        // change zoom level
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onCameraIdle() {
        CameraPosition camPos = mMap.getCameraPosition();
//        double lat = camPos.target.latitude;
//        double lng = camPos.target.longitude;
//        tvAddress.setText("latitude:" + String.valueOf(lat) +
//                ", longitude:" + String.valueOf(lng));
        String addr = getAddressFromLocation(this, camPos.target);
        tvAddress.setText(addr);
    }
}

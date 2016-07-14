package com.example.vaishnavirachapudi.savefavplacesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener ,GoogleMap.OnInfoWindowLongClickListener {

    private GoogleMap mMap;
    private List<Marker> markerList = new ArrayList<>();
    Firebase myFirebaseRef;
    String address = null;
    String location=null;

    Location myLocation = null;
    Marker markers;
    String url = "https://androidhomework09.firebaseio.com/";
    Boolean Flag = false;
    Boolean ClearFavFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://androidhomework09.firebaseio.com/");
        MultiDex.install(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final EditText input = new EditText(MapsActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);
        builder.setMessage("Enter Address")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        address = input.getText().toString();
                        if (address == null || address.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please enter address", Toast.LENGTH_LONG).show();
                        } else {
                            if (Geocoder.isPresent()) {

                                new GeoTask(MapsActivity.this).execute(address);


                            }

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


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
        final ArrayList<Location> list = new ArrayList<Location>();

        if (myLocation == null) {
            LatLng location = new LatLng(40.73581, -73.99155);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5));
        } else {

            if(ClearFavFlag==false) {
                myFirebaseRef.child("myFavPlaces").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        HashMap<String, Object> myPlaces = (HashMap<String, Object>) snapshot.getValue();

                        int i = 1;
                        if (myPlaces != null) {
                            if (myPlaces.size() > 0) {
                                for (String key : myPlaces.keySet()) {
                                    Object value = myPlaces.get(key);

                                    if (value instanceof Object) {
                                        HashMap<String, String> map = (HashMap<String, String>) value;

                                        Location place = new Location(map.get("place"), map.get("latitude"), map.get("longitude")
                                        );
                                        System.out.println("user: " + place.toString());

                                        list.add(place);


                                    }
                                    i++;
                                }


                                if (list.size() > 0) {
                                    for (int j = 0; j < list.size(); j++) {
                                        String lat = (String) list.get(j).getLatitude();
                                        String lon = (String) list.get(j).getLongitude();
                                        String location = (String) list.get(j).getLocation();


                                        LatLng loc = new LatLng(Double.parseDouble((String) list.get(j).getLatitude()), Double.parseDouble((String) list.get(j).getLongitude()));
                                        markers = mMap.addMarker(new MarkerOptions().position(loc).title((String) list.get(j).getLocation()).snippet((String) list.get(j).getLocation()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                                        markerList.add(markers);
                                        mMap.setOnInfoWindowClickListener(MapsActivity.this);
                                        mMap.setOnInfoWindowLongClickListener(MapsActivity.this);

                                    }
                                }


                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


            }


            LatLng pointer = new LatLng(Double.parseDouble(myLocation.getLatitude()), Double.parseDouble(myLocation.getLongitude()));
            mMap.addMarker(new MarkerOptions().position(pointer).title(myLocation.getLocation()).snippet(myLocation.getLocation()));

            if (Flag == true && markers!=null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                markerList.add(markers);
                for (Marker marker : markerList) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            } else if(Flag==true && markers==null && ClearFavFlag==false)
            {
                Toast.makeText(getApplicationContext(),"No Saved Places to zoom",Toast.LENGTH_LONG).show();
            }
            else if(markers==null && ClearFavFlag==true)
            {
                Toast.makeText(getApplicationContext(),"No Saved Places to be Cleared",Toast.LENGTH_LONG).show();
            }

            else if (Flag == false) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pointer));
            }

            mMap.setOnInfoWindowClickListener(MapsActivity.this);
            mMap.setOnInfoWindowLongClickListener(MapsActivity.this);

        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {



        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        Firebase locationsRef = myFirebaseRef.child("myFavPlaces");
        HashMap locations = new HashMap();
        locations.put("latitude", myLocation.getLatitude());
        locations.put("longitude", myLocation.getLongitude());
        locations.put("place", location);

        locationsRef.push().setValue(locations);

    }


    class GeoTask extends AsyncTask<String, Void, java.util.List<Address>> {
        Context mContext;

        public GeoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected List<Address> doInBackground(String... params) {
            List<Address> addressList = null;

            Geocoder geocoder = new Geocoder(mContext);

            try {
                addressList = geocoder.getFromLocationName(params[0], 10);
                if (addressList == null) {
                    Toast.makeText(MapsActivity.this, "wrong address", Toast.LENGTH_LONG).show();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return addressList;
        }


        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses != null &&  !addresses.isEmpty()) {


                Address first = addresses.get(0);
                double lat = first.getLatitude();
                double lon = first.getLongitude();
                String lattitude = String.valueOf(lat);
                String longitude = String.valueOf(lon);

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(3);
                String zipcode = addresses.get(0).getAddressLine(2);
                if (country == null) {
                    country = "";

                }
                if (zipcode == null) {
                    zipcode = "";
                }

                location = address + "," + city + "," + country + "," + zipcode;
                myLocation = new Location(location, lattitude, longitude);


                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);


            } else {

                Toast.makeText(getApplicationContext(),"Invalid Address Entered",Toast.LENGTH_LONG).show();
            }

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:

                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();

                return true;
            case R.id.action_clear:
                url = url + "myFavPlaces";

                myFirebaseRef = new Firebase(url);
                myFirebaseRef.removeValue();
                ClearFavFlag=true;

                mMap.clear();

                SupportMapFragment mapFragment1 = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment1.getMapAsync(this);


                return true;
            case R.id.action_zoom:
                Flag = true;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}

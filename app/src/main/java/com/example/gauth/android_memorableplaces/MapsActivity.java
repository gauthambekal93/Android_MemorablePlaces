package com.example.gauth.android_memorableplaces;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

   public  void centreMapOnLocation(Location location,String title)
{
    LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
   // mMap.clear();
if(title!="Your location") {
    mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
}
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
}

 public  void goBack(View view)
 {
     Intent toMain =new Intent(getApplicationContext(),MainActivity.class);
  toMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
     startActivity(toMain);
 }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {//This code is basically taking input from user when he is prompted for permission
        //request code is the number 1
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {//for the code to work it needs to recheck the users location hence we have written the inner if statement
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                //the location Manager code below gets the location and 0,0 are just numbers on how often we want to update location
                //To save battery we have to update location less frequently
                //the below code is the location when he grants permission for 1st time.When later location changes the
                //the last part of this file is used.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
           try{
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centreMapOnLocation(lastKnownLocation,"Your location");}
           catch(Exception e)
           {e.printStackTrace();}

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setOnMapLongClickListener(this);  //this will listen for mouse pointer to be pressed on map
        //If the above on click listner runs it will call onMapLongClick() fuction return below
        Intent intent = getIntent();
      if(  intent.getIntExtra("placeNumber",0)==0)  //Since we are passing an integer i from Main Activity
      //the 0 in parenthesis is default value and we are comparing if value comming from main activity is zero
      {
          //zoom in
          locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
           locationListener =new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
        centreMapOnLocation(location,"Your location");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
};
          if (Build.VERSION.SDK_INT<23)//SDK<23 means device running on previous than Marshmallow
          //in that case we donot need to ask for permission from user and can straight away use gps
          {
              locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
          }
          else{
              if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
              {
                  //we have permission after being granted for 1st time then this code will update location everytime.
                  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                  //the above code actually calls the listner automatically The 1st 0 is interval time it calls the listner in millisecon
                  //The 2nd 0 is the distance in meter based on which listner is called

              Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                  try{
                      //This try and catch block is needed because lastKnownLocation can throw a exception
                      //possibly nullvalue when app is launched initially.
                      //In such a case the map gets location from onLocationChanged and not the lastKnownLocation
                      //The lastKnownLocation will rewrite the onLocationChanged value if it runs properly
                      //lastKnownLocation only needs to work when applaunched initially
                      centreMapOnLocation(lastKnownLocation,"Your location");
                  }
                  catch (Exception e)
                  {
                      e.printStackTrace();
                      Log.i("We have","ERROR!!!!!!");
                  }
              }
              else{
                  //ask for permission
                  //We need string for what we want permission for
                  //The integer can be any number you want and is esentially to check the other end that this particular request was made
                  ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},1);

              }
          }
      }
      else{
        //  centreMapOnLocation(MainActivity.locations.get(intent.getIntExtra("placeNumber",0)),MainActivity.places.get(intent.getIntExtra("placeNumber",0)));
          Log.i("Value is","Selected!!!");
        //  mMap.clear();
        //mMap.addMarker(new MarkerOptions().position(MainActivity.locations.get(intent.getIntExtra("placeNumber",0))).
          //       title(MainActivity.places.get(intent.getIntExtra("placeNumber",0))));

//the above commented cde will also work as the below code.
          //but for some reason its causing problems now
      Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
          placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeNumber",0)).latitude);
          placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeNumber",0)).longitude);
      centreMapOnLocation(placeLocation,MainActivity.places.get(intent.getIntExtra("placeNumber",0)));

      }

    }

    //For getting the long press on the map
    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
        String address="";

        try {

            List<Address>listAddress=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
 if(listAddress.get(0).getThoroughfare()!=null)
 {
     if(listAddress.get(0).getSubThoroughfare()!=null)
     {
         address+=listAddress.get(0).getSubThoroughfare()+" ";
     }
     address+=listAddress.get(0).getThoroughfare();
 }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address=="")
        {
            SimpleDateFormat sdf=new SimpleDateFormat("mm:HH yyyyMMdd");
            address=sdf.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        Log.i("Location",address);
        MainActivity.places.add(address);  //this will be the title of stored location
        MainActivity.locations.add(latLng);//this will be the position on map which is saved
        MainActivity.arrayAdapter.notifyDataSetChanged(); //to display on the list view the added location

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.gauth.android_memorableplaces;", Context.MODE_PRIVATE);

        try {
            ArrayList<String>latitudes=new ArrayList<>();
                    ArrayList<String>longitudes=new ArrayList<>();
            for(LatLng coordinates:MainActivity.locations)
            {
                latitudes.add(Double.toString(coordinates.latitude));
                longitudes.add(Double.toString(coordinates.longitude));
            }


            sharedPreferences.edit().putString("place",ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.x=false;
        Toast.makeText(this,"Location Saved",Toast.LENGTH_SHORT).show();

    }
}
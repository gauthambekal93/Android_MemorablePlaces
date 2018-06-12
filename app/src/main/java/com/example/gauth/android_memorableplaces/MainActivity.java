package com.example.gauth.android_memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

import static android.widget.Toast.*;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    //static variables are class variables and have to be declared at class level not within any method
    static ArrayList<String>places=new ArrayList<>();
    static ArrayList<LatLng> locations=new ArrayList<>();
    static  ArrayAdapter arrayAdapter;
    static boolean x=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Called from"," Maps Activity");

        ListView listView=(ListView)findViewById(R.id.listView);
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.gauth.android_memorableplaces;", Context.MODE_PRIVATE);


        ArrayList<String>latitudes=new ArrayList<>();
        ArrayList<String>longitudes=new ArrayList<>();
        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();
        try{
          places=  (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("place",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes=  (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes=  (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));

      }
      catch (Exception e)
      {
          e.printStackTrace();
      }

        if(places.size()>0 && latitudes.size()>0&&longitudes.size()>0)
        {
            if(places.size()==latitudes.size()&&latitudes.size()==longitudes.size())
            {
                for(int i=0;i<latitudes.size();i++)
                {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        }


      else {

            places.add("Add a new place..");//this is the default location
            locations.add(new LatLng(0, 0));  //this is 0th position corresponding to default place Add a new place...
        }

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
              intent.putExtra("placeNumber",i);
                startActivity(intent);
            }
        });

    }
}

/**
 * home view
 * the initial user interaction
 */
package com.li.qian.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    // The app-specific constant when requesting the location permission
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    //Define and Initialise query
    public static String query="";
    //Define button
    Button buttonOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate the button in activity_main.xml
        // Attaches a listener for animation/transition to the button

        buttonOne = (Button) findViewById(R.id.buttonOne);
        buttonOne.setOnClickListener(this);
        // Requests for location permissions at runtime (required for API >= 23)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonOne:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getApplicationContext(), PlacePickerActivity.class);
                    TextView addressET = (TextView) findViewById(R.id.addressET);
                    query=addressET.getText().toString();
                    startActivity(i);
                } else {
                    // Notifies the user if there are insufficient location permissions
                    Toast.makeText(getApplicationContext(), "App is missing permissions to access your location!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
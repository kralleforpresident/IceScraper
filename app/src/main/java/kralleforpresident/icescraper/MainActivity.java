package kralleforpresident.icescraper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.widget.CompoundButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    //Variaben definieren
    private TextView hum_value;
    private TextView temp_value;
    private EditText city_input;
    private String city_string;
    private String city_result;
    private TextView main_text;
    private EditText city_box;
    private ImageView main_icon;

    int meinepermission;





    //GPS zeugs


    public class MyCurrentLoctionListener implements LocationListener {


        @Override

        public void onLocationChanged(final Location location) {





            location.getLatitude();
            location.getLongitude();



            ImageButton gps_button = (ImageButton) findViewById(R.id.gps_button);
            TextView gps_text = (TextView) findViewById(R.id.GPS_text);

            String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

            //I make a log to see the results
            Log.e("MY CURRENT LOCATION", myLocation);
            gps_text.setBackgroundColor(Color.GREEN);



            //GSP Button druecken

            gps_button.setOnClickListener(
                    new ImageButton.OnClickListener() {

                        public void onClick(View v) {

                            new JSONTask().execute("http://api.openweathermap.org/data/2.5/weather?units=metric&appid=cad29819e983f9368360f738c9721dc8&lat=" + location.getLatitude() + "&lon=" + location.getLongitude());

                        }
                    }

            );


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {


        }
    }




    //Variablen definieren

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton gps_button = (ImageButton) findViewById(R.id.gps_button);
        ImageButton search_button = (ImageButton) findViewById(R.id.search_button);
        hum_value = (TextView) findViewById(R.id.hum_value);
        temp_value = (TextView) findViewById(R.id.temp_value);
        final EditText citybox = (EditText) findViewById(R.id.citybox);
        main_text = (TextView) findViewById(R.id.main_text);
        city_box = (EditText) findViewById(R.id.citybox);





        //Android Marshmallow Berechtigung
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, meinepermission);

            //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();



        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            //noch mehr GPS zeugs
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);

        }





        //Was passiert, wenn man den Suchen Button drückt
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                city_input = (EditText) findViewById(R.id.citybox);
                city_string = city_input.getText().toString();
                city_result = city_string.replaceAll("\\s", "_");
                citybox.setHint(city_result);
                ImageView main_icon = (ImageView) findViewById(R.id.main_icon);

                if(city_input.getText().toString().trim().length() == 0) {
                    main_text.setText(R.string.Enter_City);
                    main_icon.setImageResource(R.drawable.city);
                }

                else



                //City Eingabe API Aufruf
                new JSONTask().execute("http://api.openweathermap.org/data/2.5/weather?units=metric&appid=cad29819e983f9368360f738c9721dc8&q=" + city_result);

        }
       });


    //Irgendwas, keine Ahnung
    }
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }


                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        //JSON result bearbeiten
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ImageView main_icon = (ImageView) findViewById(R.id.main_icon);

            if (result.equals("{\"cod\":\"404\",\"message\":\"Error: Not found city\"}")) {
                main_text.setText(R.string.City_not_found);
                main_icon.setImageResource(R.drawable.city);
            } else {


                String temp_cut = result.replaceAll(".*temp\":", "");
                String temp_result = temp_cut.replaceAll(",.*", "");
                Float temp_result_float = Float.parseFloat(temp_result);

                temp_value.setText(temp_result);

                String humidity_cut = result.replaceAll(".*humidity\":", "");
                String humidity_value = humidity_cut.replaceAll(",.*", "");
                Float humidity_value_float = Float.parseFloat(humidity_value);

                hum_value.setText(humidity_value);


                String city_cut = result.replaceAll(".*name\":\"", "");
                String city_result = city_cut.replaceAll("\".*", "");

                city_box.setHint(city_result);
                //ImageView main_icon = (ImageView) findViewById(R.id.main_icon);

                //IceScraper Text bearbeiten
                if (humidity_value_float > 75 && temp_result_float <= 4) {
                    main_text.setText(R.string.IceScraper_true);
                    main_icon.setImageResource(R.drawable.snowman);

                } else {
                    main_text.setText(R.string.IceScraper_false);
                    main_icon.setImageResource(R.drawable.sun);
                }
            }

        }


    }



    //Noch mehr Berechtigungen

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == meinepermission) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PLAYGROUND", "Permission has been granted");
               // textView.setText("You can send SMS!");
               // button.setEnabled(true);


                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);


            } else {
                Log.d("PLAYGROUND", "Permission has been denied or request cancelled");
              //  textView.setText("You can not send SMS!");
              //  button.setEnabled(false);
            }
        }
    }









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        startActivity(new Intent(getApplicationContext(), AboutMe.class));
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

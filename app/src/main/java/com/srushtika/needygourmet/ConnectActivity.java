package com.srushtika.needygourmet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConnectActivity extends Activity {
    Button btndir;
    ImageView imageView;
    private double latitude=0;
    private double longitude=0;

    String imgname;
    String destlat;
    String destlong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);
        int loader = R.drawable.loader;
        ImageView image = (ImageView) findViewById(R.id.imageView);
        // String image_url = "http://192.168.0.103/imgupload/1421251841465.jpg";

        StrictMode.enableDefaults();
        GPSTracker gps;
        this.latitude = 0;
        this.longitude = 0;

        imageView = (ImageView) findViewById(R.id.imageView);
        btndir = (Button) findViewById(R.id.button);

        gps = new GPSTracker(ConnectActivity.this);

        if (gps.canGetLocation()) {
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        getData();
        final String image_url =
                // "http://192.168.0.104/imgupload/1425911385262.jpg";
                "http://127.0.0.1/imgupload/"+imgname;

        Log.i("Image here :",""+imgname);
        Log.i("URL :",""+image_url);

        ImageLoader imgLoader = new ImageLoader(getApplicationContext());

        imgLoader.DisplayImage(image_url, loader, image);
        //getData();

        //final String URL =
        //      "http://192.168.0.103/imgupload/"+imgname+".jpg";

        // GetXMLTask task = new GetXMLTask();
        // Execute the task
        //task.execute(URL);

        btndir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String so=destlat;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?,34.34&daddr=13.19820595, 77.60485840"));
                        Uri.parse("http://maps.google.com/maps?,34.34&daddr=" + destlat + ", " + destlong));
                startActivity(intent);
            }
        });

    }

    private void getData() {

        String result = "";
        InputStream isr = null;

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        String someString = Double.toString(this.latitude);
        Log.i("Somestring:", "" + someString);
        nameValuePairs.add(new BasicNameValuePair("Slat", someString));
        String someString1 = Double.toString(this.longitude);
        nameValuePairs.add(new BasicNameValuePair("Slat1", someString1));
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://127.0.0.1/imgupload/getAllCustomers.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());

        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();

            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error  converting result "+e.toString());
        }

        //parse json data
        try {
            String s = "";
            JSONArray jArray = new JSONArray(result);


            for(int i=0; i<1;i++){
                JSONObject json = jArray.getJSONObject(i);
                s = s +
                        "PicName : "+json.getString("picname")+"\n"+"\n\n";

                imgname=json.getString("picname");

                Log.i("Img Name:", "" + imgname);
                destlat=json.getString("latfloat");
                destlong=json.getString("longifloat");


            }

            Log.i("name:",""+imgname);
            Log.i("destlat:",""+destlat);
            Log.i("destlong:",""+destlong);

            // resultView.setText(s);



        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }




    }
}
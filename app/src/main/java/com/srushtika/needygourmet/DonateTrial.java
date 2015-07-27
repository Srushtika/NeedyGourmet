package com.srushtika.needygourmet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class DonateTrial extends Activity {

    ImageView imageView;
    Button btndir;
    private double latitude=0;
    private double longitude=0;

    String imgname;
    String destlat;
    String destlong;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);
        StrictMode.enableDefaults();
        GPSTracker gps;
        this.latitude = 0;
        this.longitude = 0;

        imageView = (ImageView) findViewById(R.id.imageView);
        btndir = (Button) findViewById(R.id.button);

        gps = new GPSTracker(DonateTrial.this);

        if (gps.canGetLocation()) {
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        getData();

        final String URL =
                "http://127.0.0.1/imgupload/"+imgname+".jpg";

        GetXMLTask task = new GetXMLTask();
        // Execute the task
        task.execute(URL);

        btndir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String so=destlat;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?,34.34&daddr=13.19820595, 77.60485840"));
                        Uri.parse("http://maps.google.com/maps?,34.34&daddr="+destlat+", "+destlong));
                startActivity(intent);
            }
        });

    }

    private void getData() {

        String result = "";
        InputStream isr = null;

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        String someString = Double.toString(this.latitude);
        Log.i("Somestring:",""+someString);
        nameValuePairs.add(new BasicNameValuePair("Slat", someString));
        String someString1 = Double.toString(this.longitude);
        nameValuePairs.add(new BasicNameValuePair("Slat1", someString1));
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.0.103/imgupload/getAllCustomers.php");
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
                destlat=json.getString("latfloat");
                destlong=json.getString("longifloat");


            }

            Log.i("name:",""+imgname);

            // resultView.setText(s);



        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data "+e.toString());
        }




    }



    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {

            imageView.setImageBitmap(result);
        }




        // Creates Bitmap from InputStream and returns it
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;

            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();


            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                //bitmap.compress(Bitmap.CompressFormat.JPEG,4096,4096);
                Log.i("width:", String.valueOf(bitmap.getWidth()));
                Log.i("height:",String.valueOf(bitmap.getHeight()));
                //Log.i("name:",""+URL);


                Log.i("mutable:","" +bitmap.isMutable());
                //Log.i("mutable:","" +bmOptionsq);


                Log.i("width:", String.valueOf(bitmap.getWidth()));
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


}
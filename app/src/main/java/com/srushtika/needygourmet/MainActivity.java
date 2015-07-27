package com.srushtika.needygourmet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class MainActivity extends Activity {

    Button btpic, btnup;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1;
    GPSTracker gps;
    double latitude;
    double longitude;
    public static String URL = "http://127.0.0.1/imgupload/up.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btpic = (Button) findViewById(R.id.cpic);

//after git

        gps = new GPSTracker(MainActivity.this);

        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

     /*       Log.i("Latitude value is :", ""+latitude);
            Toast.makeText(
                    getApplicationContext(),
                    "Your Location is -\nLat: " + latitude + "\nLong: "
                            + longitude, Toast.LENGTH_LONG).show();
*/        } else {
            gps.showSettingsAlert();
        }

        btpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickpic();
            }
        });

        btnup = (Button) findViewById(R.id.up);
        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }

    private void upload() {
        // Image location URL
        Log.e("path", "uploadedimages" + picturePath);

        // Image
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeBytes(ba);

        Log.e("base64", "-----" + ba1);

        // Upload image to server


        new uploadToServer(latitude,longitude).execute();

    }

    private void clickpic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {

            selectedImage = data.getData();
            photo = (Bitmap) data.getExtras().get("data");

            // Cursor to get image uri to display

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.Imageprev);
            imageView.setImageBitmap(photo);
        }
    }

    public class uploadToServer extends AsyncTask<Void, Void, String> {
        private double latitude;
        private double longitude;


        private ProgressDialog pd = new ProgressDialog(MainActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Wait image uploading!");
            pd.show();


        }

        public uploadToServer(double lat, double lon){
            this.latitude=lat;
            this.longitude=lon;
        }



        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", ba1));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));

            String someString = Double.toString(latitude);
            nameValuePairs.add(new BasicNameValuePair("Slat", someString));

            String someString1 = Double.toString(longitude);
            nameValuePairs.add(new BasicNameValuePair("Slat1", someString1));


//            Log.i("show :",""+latitude);




            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //System.out.print(httppost);
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";



        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /*gps = new GPSTracker(MainActivity.this);

            if(gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Toast.makeText(
                        getApplicationContext(),
                        "Your Location is -\nLat: " + latitude + "\nLong: "
                                + longitude, Toast.LENGTH_LONG).show();
            } else {
                gps.showSettingsAlert();
            }*/
            Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
            pd.hide();
            pd.dismiss();
        }
    }
}

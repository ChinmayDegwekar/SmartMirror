//INITIATLIZATION://IP ADDRESS OF SERVER
//FUNCTIONALITIES:
/*
1)DOWNLOAD MULTIPLE IMAGES FROM SERVER
2)RUN AUTO DELETE IMAGES FROM SERVER TO AVOID DUPLICATES
3)SHOW THUBNAILS OF MOST IMAGES

 */
package com.example.chinmay.smartmirror;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private EditText editTextId;
    private Button buttonGetImage;
    private Button btnaddphp;

    private ImageView imageView;
    ProgressDialog loading;

    static final String appDirectoryName = "Smart Selfies";
    static final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), appDirectoryName);
    private boolean internet_connected=true;
    //private RequestHandler requestHandler;


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        loading.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //editTextId = (EditText) findViewById(R.id.editTextId);
        buttonGetImage = (Button) findViewById(R.id.btnGetImage);
       // btnaddphp = (Button) findViewById(R.id.btnaddphp);

        imageView = (ImageView) findViewById(R.id.ivShow);

       // requestHandler = new RequestHandler();

        buttonGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internet_connected=true;

                String counter = "http://192.168.1.103/demo/count.php";

                //getImage(1);

               VOlleyCounter();

            }
        });
//
//        btnaddphp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callAddPHP();
//            }
//        });
    }

    private void getImage(final int name, final int count) {
       // String id = editTextId.getText().toString().trim();
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            //ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
              //  loading = ProgressDialog.show(MainActivity.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                Log.e("sdss","post execute called");
                super.onPostExecute(b);
                //loading.dismiss();
                if(internet_connected) {
                    imageView.setImageBitmap(b);

                    saveImage(b,name,count);

                }
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                //String add = "http://www.uk-lanparty.co.uk/wp-content/uploads/2013/05/lan_server_01.jpg";
                //String add = "http://192.168.1.103/demo/uploads/1.png";
                String add = "http://192.168.1.103/demo/uploads/"+name+".png";//IP ADDRESS OF SERVER

                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    internet_connected=false;
                } catch (IOException e) {
                    e.printStackTrace();
                    internet_connected=false;
                }

//                imageRoot.mkdirs();
//                final File store_image = new File(imageRoot, "image1.jpg");

                return image;
            }


        }

        GetImage gi = new GetImage();
        gi.execute("0");
    }

    void saveImage(Bitmap bitmap, int current, int total)
    {
        imageRoot.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String name = "Selfie_"+timeStamp;
        Log.e("name",name);
        final File store_image = new File(imageRoot, name+".jpg");
        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(store_image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DESCRIPTION,"selfies");
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
        //values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
        //values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
        values.put("_data", store_image.getAbsolutePath());

        ContentResolver cr = getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (success) {
            Toast.makeText(getApplicationContext(), "Image saved with success",
                    Toast.LENGTH_LONG).show();
            if(current==total)
                deletePHP();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }

    }


    void countFiles(final String counter)
    {


        class RetrieveCount extends AsyncTask<String, Void, Integer> {

            private Exception exception;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(MainActivity.this, "Uploading...", null,true,true);
            }
            protected void onPostExecute(Integer count) {
                // TODO: check this.exception
                // TODO: do something with the feed
                super.onPostExecute(count);

                Log.e("total_files: ",count+"");
                if(count!=-1)
                {
                    for(int i=0;i<count;i++)
                    {
                        getImage(i+1,count);
                    }
                }

            }
            protected Integer doInBackground(String... urls) {
                try {
                    String url = counter;
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response;
                    try {
                        response = client.execute(new HttpGet(url));
                        HttpParams params = response.getParams();
                        return params.getIntParameter("count",-1);
                    } catch(IOException e) {
                        //do something here
                        e.printStackTrace();
                    }



                    return -1;

                } catch (Exception e) {
                    this.exception = e;


                }
                return -1;
            }

        }

        RetrieveCount rc = new RetrieveCount();
        rc.execute(counter);

    }




    private void VOlleyCounter() {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Downloading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.103/demo/count.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        String st[] = s.split("\n");
                        int count = Integer.parseInt(st[1]);
                       loading.dismiss();
                        //Showing toast message of the response
                        if(count==0)
                            Toast.makeText(MainActivity.this, "No New Selfie", Toast.LENGTH_LONG).show();
                        else
                        Toast.makeText(MainActivity.this, count+" new selfie(s)", Toast.LENGTH_LONG).show();


                        Log.e("total_files","--->"+count);
                        if(count!=-1)
                        {
                            for(int i=0;i<count;i++)
                            {
                                getImage(i+1,count);
                            }
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                  //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

//        StringRequest deleteRequest = new StringRequest(Request.Method.POST, "http://192.168.1.103/demo/deleteAll.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                       // loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new Hashtable<String, String>();
//                return params;
//            }
//        };

       // requestQueue.add(deleteRequest);




    }



    void deletePHP()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        //requestQueue.add(stringRequest);

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, "http://192.168.1.103/demo/deleteAll.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        // loading.dismiss();

                        //Showing toast
                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                return params;
            }
        };

        requestQueue.add(deleteRequest);

    }


    private void callAddPHP()
    {
        Intent intent = new Intent(this,AddPHPActivity.class);
    }
}
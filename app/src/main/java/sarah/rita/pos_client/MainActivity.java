package sarah.rita.pos_client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MainActivity extends ActionBarActivity {
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void okClicked(View v){
        id = 0;
        EditText idET = (EditText) findViewById(R.id.idtv);

        try {
            if(String.valueOf(idET.getText())!=null){
                id = Integer.parseInt(URLEncoder.encode(String.valueOf(idET.getText()), "utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("id", String.valueOf(id));
        if (id>0) {
            Viewer viewer = new Viewer();
            viewer.execute();
        }else {
            Toast.makeText(this, "Input ID " , Toast.LENGTH_LONG).show();
        }

    }
    public boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    class Viewer extends AsyncTask<String, String, String> {
        String nik_ktp;
        long saldo;
        String nama;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Harap tunggu sebentar..");
        }

        @Override
        protected String doInBackground(String... params) {
//            if(isNetworkAvailable()) {
                String result = "";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet("http://pos-fingerprint.herokuapp.com/api/getcredentials?id="+id);
                HttpResponse response;

                try {
                    response = client.execute(request);

                    // Get the response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result += line;
                    }

                    try {
                        // Data
                        JSONArray arrRes = new JSONArray(result);
                        JSONObject res = (JSONObject) arrRes.get(0);
                        Log.d("Jsonresult",res.toString());
                        nama = res.getString("nama");
                        nik_ktp = res.getString("nik_ktp");
                        saldo = res.getLong("saldo");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Anda tidak terhubung ke internet", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, MenuUtama.class);
            Bundle b = new Bundle();
            b.putInt("id", id); //Your id
            b.putLong("saldo",saldo);
            b.putString("nik_ktp",nik_ktp);
            b.putString("nama",nama);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }
    }


}

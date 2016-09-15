package sarah.rita.pos_client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;


public class Belanja extends ActionBarActivity {

    private LinearLayout myLinearLayout;
    private TextView TitleEventTV;
    private TextView JudulEventTV;
    private TextView TitleTanggalTV;
    private TextView JudulTanggalTV;
    private TextView TitleWaktuTV;
    private TextView JudulWaktuTV;
    private TextView TitleKeteranganTV;
    private TextView IsiKeteranganTV;
    private Button SelengkapnyaBtn;
    private LinearLayout.LayoutParams paramsJarakAntarEvent;
    private LinearLayout.LayoutParams paramsJarakAntarIsi;
    private LinearLayout.LayoutParams paramsJarakIsiDenganButton;
    private LinearLayout rowLayout;
    private LinearLayout colLayout;
    private LinearLayout subRowLayout;

    private ArrayList<String> judulSaved;
    private ArrayList<String> tanggalSaved;
    private ArrayList<String> keteranganSaved;
    private ArrayList<String> linkSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belanja);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Viewer v = new Viewer();
        v.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_belanja, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_belanja, container, false);
            return rootView;
        }
    }

    // Fungsi untuk menyiapkan layout tampilan
    public void setUpLayout(){
        myLinearLayout = (LinearLayout) findViewById(R.id.container_listbelanja);
        myLinearLayout.setOrientation(LinearLayout.VERTICAL);
        myLinearLayout.removeAllViews();

        // Add LayoutParams
        paramsJarakAntarEvent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsJarakAntarEvent.setMargins(0, 15, 20, 0);

        paramsJarakAntarIsi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsJarakAntarIsi.setMargins(5, 0, 0, 0);

        paramsJarakIsiDenganButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsJarakIsiDenganButton.setMargins(5, 5, 0, 15);

        rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Buat linear layout vertical untuk menampung kata-kata
        colLayout = new LinearLayout(this);
        colLayout.setOrientation(LinearLayout.VERTICAL);
        colLayout.setPadding(0, 10, 10, 0);

        subRowLayout = new LinearLayout(this);
        subRowLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void generateKontenEvent() {
        setUpLayout();

        int dataLength = judulSaved.size();
        for (int i = 0; i < dataLength; i++) {
            generateUI(judulSaved.get(i), tanggalSaved.get(i), keteranganSaved.get(i), linkSaved.get(i));
            if (i != dataLength) {
                rowLayout.addView(colLayout);
                myLinearLayout.addView(rowLayout);
                rowLayout = new LinearLayout(this);
                colLayout = new LinearLayout(this);
                colLayout.setOrientation(LinearLayout.VERTICAL);
                subRowLayout = new LinearLayout(this);
            }
        }
    }

    // Fungsi untuk generate komponen-komponen tampilan
    public void generateUI (String judul, String tanggal, String keterangan, String linkGambar) {
        Display display = getWindowManager().getDefaultDisplay();
        int image_width = display.getWidth()/3;
        int image_height = (int) (display.getHeight()/4.3);

//        int defaultColor = getResources().getColor(R.color.defaultFontColor);

        // Add image View
        ImageView GambarIV = new ImageView(this);

        // Loading image from below url into imageView
//        Picasso.with(getActivity())
//                .load(linkGambar)
//                .resize(image_height, image_width)
//                .into(GambarIV);
//        GambarIV.setLayoutParams(paramsJarakAntarEvent);
//        rowLayout.addView(GambarIV);

        // Add text View TitleEventTV
        TitleEventTV = new TextView(this);
        TitleEventTV.setText("Event: ");
//        TitleEventTV.setTextColor(defaultColor);
        TitleEventTV.setLayoutParams(paramsJarakAntarIsi);
//        TitleEventTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
        subRowLayout.addView(TitleEventTV);

        // Add text View JudulEventTV
        JudulEventTV = new TextView(this);
        JudulEventTV.setText(judul);
//        JudulEventTV.setTextColor(defaultColor);
        JudulEventTV.setLayoutParams(paramsJarakAntarIsi);

        if (subRowLayout.getParent() != null) {
            ((ViewGroup) subRowLayout.getParent()).removeView(subRowLayout);
        }

        subRowLayout.addView(JudulEventTV);
        colLayout.addView(subRowLayout);
        subRowLayout = new LinearLayout(this);

        // Add text View TitleTanggalTV
        TitleTanggalTV = new TextView(this);
        TitleTanggalTV.setText("Tanggal: ");
//        TitleTanggalTV.setTextColor(defaultColor);
//        TitleTanggalTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
        TitleTanggalTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(TitleTanggalTV);

        // Add text View JudulTanggalTV
        JudulTanggalTV= new TextView(this);
        JudulTanggalTV.setText(tanggal);
//        JudulTanggalTV.setTextColor(defaultColor);
        JudulTanggalTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(JudulTanggalTV);
        colLayout.addView(subRowLayout);
        subRowLayout = new LinearLayout(this);

        // Add text View TitleWaktuTV
        TitleWaktuTV = new TextView(this);
        TitleWaktuTV.setText("Waktu: ");
//        TitleWaktuTV.setTextColor(defaultColor);
//        TitleWaktuTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
        TitleWaktuTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(TitleWaktuTV);

        // Add text View JudulWaktuTV
        JudulWaktuTV = new TextView(this);
        JudulWaktuTV.setText(tanggal);
//        JudulWaktuTV.setTextColor(defaultColor);
        JudulWaktuTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(JudulWaktuTV);
        colLayout.addView(subRowLayout);
        subRowLayout = new LinearLayout(this);

        // Add text View TitleKeteranganTV
        TitleKeteranganTV = new TextView(this);
        TitleKeteranganTV.setText("Keterangan: ");
//        TitleKeteranganTV.setTextColor(defaultColor);
        TitleKeteranganTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(TitleKeteranganTV);

        // Add text View IsiKeteranganTV
        IsiKeteranganTV = new TextView(this);
        if (keterangan.length() > 80) {
            keterangan = keterangan.substring(0, 80);
            keterangan = keterangan + "...";
        }
        IsiKeteranganTV.setText(keterangan);
//        IsiKeteranganTV.setTextColor(defaultColor);
        IsiKeteranganTV.setLayoutParams(paramsJarakAntarIsi);
        subRowLayout.addView(IsiKeteranganTV);
        colLayout.addView(subRowLayout);
        subRowLayout = new LinearLayout(this);

        // Add selengkapnya button
        SelengkapnyaBtn = new Button(this);
        SelengkapnyaBtn.setText("Selengkapnya");
//        SelengkapnyaBtn.setTextColor(getResources().getColor(R.color.white));
        SelengkapnyaBtn.setLayoutParams(paramsJarakIsiDenganButton);
//        SelengkapnyaBtn.setBackgroundColor(getResources().getColor(R.color.header));
        subRowLayout.addView(SelengkapnyaBtn);
        colLayout.addView(subRowLayout);

        final String finalJudul = judul;
        final String finalTanggal = tanggal;
        final String finalKeterangan = keterangan;
        final String finalLinkGambar = linkGambar;
        SelengkapnyaBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Masuk ke konstruktor parameter EventLengkapFragment dengan parameter judul, tanggal, keterangan, dan gambar
//                        frag = new EventLengkapFragment(finalJudul, finalTanggal, finalKeterangan, finalLinkGambar);
//                        fragManager = getActivity().getSupportFragmentManager();
//                        fragTransaction = fragManager.beginTransaction();
//                        fragTransaction.replace(R.id.container, frag);
//                        fragTransaction.addToBackStack(null);
//                        fragTransaction.commi
//                        t();
                    }
                }
        );
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

        JSONArray arrRes;

        @Override
        protected void onPreExecute() {
            setUpLayout();

        }

        @Override
        protected String doInBackground(String... params) {
//            if(isNetworkAvailable()) {
                String result = "";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet("http://pos-fingerprint.herokuapp.com/api/getbarang");
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
                        arrRes = new JSONArray(result);

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

            for (int i=0;i<arrRes.length();i++){
                JSONObject res = (JSONObject) arrRes.get(i);
                Log.d("json oj",res.toString());

                String nama = res.getString("nama");
                int stok = res.getInt("stok");
                int harga = res.getInt("harga");
                generateUI(nama,String.valueOf(harga),String.valueOf(stok),"");

                if (i != arrRes.length()) {
                    rowLayout.addView(colLayout);
                    myLinearLayout.addView(rowLayout);
                    rowLayout = new LinearLayout(Belanja.this);
                    colLayout = new LinearLayout(Belanja.this);
                    colLayout.setOrientation(LinearLayout.VERTICAL);
                    subRowLayout = new LinearLayout(Belanja.this);
                }

            }


        }
    }

}

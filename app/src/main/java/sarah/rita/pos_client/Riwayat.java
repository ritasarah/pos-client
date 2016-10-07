package sarah.rita.pos_client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Riwayat extends ActionBarActivity {
//    private ArrayList<String> judulSaved;
//    private ArrayList<String> tanggalSaved;
//    private ArrayList<String> keteranganSaved;
//    private ArrayList<String> linkSaved;

    int reqtype = 0;
    long saldo = 0;
    int id = 0;
    String nama = null;
    String namabarang = null;
    private LinearLayout scrollViewLayout;
    private ScrollView daftarHistoriSV;
    private Calendar myCalendar;
    String dateawal;
    String dateakhir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            nama = b.getString("nama");
            saldo = b.getLong("saldo");
            id= b.getInt("id");

            Log.d("id di riwayat ", String.valueOf(id));
        }
        scrollViewLayout = new LinearLayout(Riwayat.this);
        scrollViewLayout.setOrientation(LinearLayout.VERTICAL);
        daftarHistoriSV = (ScrollView) findViewById(R.id.container_svriwayat);
        myCalendar = Calendar.getInstance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_riwayat, menu);
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
    public void barangClicked(View v){
        clearSVLayout();
        clearJangkaLayout();
        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Riwayat.this);

        // Setting Dialog Title
        alertDialog2.setTitle("Masukkan Nama Barang");

        // Setting Dialog Message
        alertDialog2.setMessage("Masukkan nama barang yang untuk mengetahui riwayat pembelian");
        final EditText input = new EditText(Riwayat.this);
        alertDialog2.setView(input);

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        reqtype=5;
                        namabarang= String.valueOf(input.getText());

                        Viewer v = new Viewer();
                        v.execute();

                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Toast.makeText(getApplicationContext(),
                                "Pencarian Dibatalkan", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
    }

//    public void waktuClicked(View v){
//        LinearLayout aLinearLayout = (LinearLayout) findViewById(R.id.container_tigabutton);
//
//        Button dailyBtn = new Button(Riwayat.this);
//        dailyBtn.setText("Daily");
////        weeklyBtn.setLayoutParams(marginHorizontal);
//        aLinearLayout.addView(dailyBtn);
//
//        Button weeklyBtn = new Button(Riwayat.this);
//        weeklyBtn.setText("Weekly");
////        weeklyBtn.setLayoutParams(marginHorizontal);
//        aLinearLayout.addView(weeklyBtn);
//
//        Button monthlyBtn = new Button(Riwayat.this);
//        monthlyBtn.setText("Monthly");
////        weeklyBtn.setLayoutParams(marginHorizontal);
//        aLinearLayout.addView(monthlyBtn);
//    }

    public void harianClicked(View v){
        clearSVLayout();
        clearJangkaLayout();
        reqtype = 1;
        Viewer vi = new Viewer();
        vi.execute();
    }

    public void mingguanClicked(View v){
        clearSVLayout();
        clearJangkaLayout();
        reqtype = 2;
        Viewer vi = new Viewer();
        vi.execute();
    }

    public void bulananClicked(View v){
        clearSVLayout();
        clearJangkaLayout();
        reqtype = 3;
        Viewer vi = new Viewer();
        vi.execute();
    }

    private void setUpDatePicker() {
        final EditText dateFrom = (EditText) findViewById(R.id.starttv);
        final EditText dateTo = (EditText) findViewById(R.id.endtv);

        final DatePickerDialog.OnDateSetListener dateDialogFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

//                String myFormat = "YYYY/MM/DD"; //In which you need put here
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                edittext.setText(sdf.format(myCalendar.getTime()));

                String monthStr = "";
                if (month+1 < 10)
                    monthStr = "0" + Integer.toString(month+1);
                else
                    monthStr = Integer.toString(month+1);

                String dayStr = "";
                if (day < 10)
                    dayStr = "0" + Integer.toString(day);
                else
                    dayStr = Integer.toString(day);

                String selected = Integer.toString(year) + "-" + monthStr + "-" + dayStr;
                dateFrom.setText(selected);
            }
        };

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Riwayat.this,
                                     dateDialogFrom,
                                     myCalendar.get(Calendar.YEAR),
                                     myCalendar.get(Calendar.MONTH),
                                     myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateDialogTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);

                String monthStr = "";
                if (month+1 < 10)
                    monthStr = "0" + Integer.toString(month+1);
                else
                    monthStr = Integer.toString(month+1);

                String dayStr = "";
                if (day < 10)
                    dayStr = "0" + Integer.toString(day);
                else
                    dayStr = Integer.toString(day);

                String selected = Integer.toString(year) + "-" + monthStr + "-" + dayStr;
                dateTo.setText(selected);
            }
        };

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Riwayat.this,
                        dateDialogTo,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void jangkaClicked(View v){
        clearSVLayout();
        LinearLayout jangkaBtnLayout = (LinearLayout) findViewById(R.id.container_jangkaet);
        jangkaBtnLayout.setVisibility(View.VISIBLE);
        setUpDatePicker();

        reqtype = 4;

//        final int from_year = 0, from_month = 0, from_day = 0,to_year = 0, to_month = 0, to_day = 0; //initialize them to current date in onStart()/onCreate()
//        DatePickerDialog.OnDateSetListener from_dateListener = null;
//        DatePickerDialog.OnDateSetListener to_dateListener = null;
//        Calendar c = Calendar.getInstance();
//
//        to_dateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//            }
//        };
//
//        new DatePickerDialog(this, from_dateListener, from_year, from_month, from_day).show();
//        final DatePickerDialog.OnDateSetListener finalTo_dateListener = to_dateListener;
//        from_dateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                new DatePickerDialog(Riwayat.this, finalTo_dateListener, to_year, to_month, to_day).show();
//            }
//        };



//        Calendar c = Calendar.getInstance();
//
//        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // Do something here
//
//
//            }
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void cariHistoriDariJarak(View v) {
        clearSVLayout();
        EditText dateFrom = (EditText) findViewById(R.id.starttv);
        EditText dateTo = (EditText) findViewById(R.id.endtv);

        dateawal = dateFrom.getText().toString();
        dateakhir = dateTo.getText().toString();

        if(dateawal.length()>2 && dateakhir.length()>2){
            Viewer vi = new Viewer();
            vi.execute();
        }else {
            Toast.makeText(this, "Isi Jangka Waktu " , Toast.LENGTH_LONG).show();
        }
    }

    private void clearSVLayout(){
        scrollViewLayout.removeAllViews();
        daftarHistoriSV.removeAllViews();
    }

    private void clearJangkaLayout() {
        LinearLayout jangkaLayout = (LinearLayout) findViewById(R.id.container_jangkaet);
        jangkaLayout.setVisibility(View.GONE);
    }

    public void generateUI (String namaBarang, String tanggal, int qty, String linkGambar) {
        Display display = getWindowManager().getDefaultDisplay();
        int image_width = display.getWidth()/5;
        int image_height = (int) ((image_width*3)/4);

        // Define margins
        LinearLayout.LayoutParams marginVertical = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        marginVertical.setMargins(5, 0, 0, 0);
        LinearLayout.LayoutParams marginHorizontal = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        marginHorizontal.setMargins(0, 5, 0, 0);

        // Define layout
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);

        // Add image View
        ImageView GambarIV = new ImageView(this);
        // Loading image from below url into imageView
        Picasso.with(this)
                .load(linkGambar)
                .resize(image_height, image_width)
                .centerInside()
                .into(GambarIV);
        GambarIV.setLayoutParams(marginHorizontal);
        rowLayout.addView(GambarIV);

        // Make info component
        TextView infoTV = new TextView(this);
        String infoStr = "Tanggal: " + tanggal;
        infoTV.setText(infoStr);
//        infoTV.setTextColor(defaultColor);
        infoTV.setLayoutParams(marginVertical);
//        infoTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
        infoLayout.addView(infoTV);

        infoTV = new TextView(this);
        infoStr = "Beli: " + namaBarang;
        infoTV.setText(infoStr);
        infoTV.setLayoutParams(marginVertical);
        infoLayout.addView(infoTV);

        infoTV = new TextView(this);
        infoStr = "Jumlah dibeli: " + Integer.toString(qty);
        infoTV.setText(infoStr);
        infoTV.setLayoutParams(marginVertical);
        infoLayout.addView(infoTV);

        rowLayout.addView(infoLayout);
        scrollViewLayout.addView(rowLayout);
    }

    public void backRiwayatClicked (View v) {
        Intent intent = new Intent(this, MenuUtama.class);
        Bundle b = new Bundle();
        b.putInt("id", id); //Your id
        b.putLong("saldo",saldo);
        b.putString("nama",nama);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    class Viewer extends AsyncTask<String, String, String> {
        JSONArray arrRes;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
//            reqtype = 1;
            progressDialog = ProgressDialog.show(Riwayat.this, "Loading", "Harap tunggu sebentar..");
        }

        @Override
        protected String doInBackground(String... params) {
//            if(isNetworkAvailable()) {
            String result = "";
            HttpClient client = new DefaultHttpClient();
            HttpGet request;
            Log.d("reqtype", String.valueOf(reqtype));
            if (reqtype==5){
                namabarang = namabarang.replace(" ","%20");
                request = new HttpGet("http://pos-fingerprint.herokuapp.com/api/gethistorybarang?id="+id+"&nama="+namabarang);
                Log.d("urlget","http://pos-fingerprint.herokuapp.com/api/gethistorybarang?id="+id+"&nama="+namabarang);
            }else if (reqtype==4){
                request = new HttpGet("http://pos-fingerprint.herokuapp.com/api/gethistory?id="+id+"&reqtype="+reqtype+"&dateawal='"+dateawal+"'&dateakhir='"+dateakhir+"'");
            }
            else {
                request = new HttpGet("http://pos-fingerprint.herokuapp.com/api/gethistory?id="+id+"&reqtype="+reqtype);
                Log.d("urget","http://pos-fingerprint.herokuapp.com/api/gethistory?id="+id+"&reqtype="+reqtype);
            }
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
                    Log.d("arres",result);
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
//            setUpLayout();
            progressDialog.dismiss();
            if (arrRes.length() > 0) {
                for (int i = 0; i < arrRes.length(); i++) {
                    JSONObject res = null;
                    try {
                        res = (JSONObject) arrRes.get(i);
                        Log.d("json oj", res.toString());

                        String tgl = res.getString("tanggal");
                        String nama = res.getString("nama");
                        int kuantitas = res.getInt("kuantitas");
                        String link = "http://pos-fingerprint.herokuapp.com/asset/img/" + res.getString("icon");
                        generateUI(nama, tgl, kuantitas, link);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                daftarHistoriSV.addView(scrollViewLayout);
            }
            else {
                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Riwayat.this);
                // Setting Dialog Title
                alertDialog2.setTitle("Informasi");
                // Setting Dialog Message
                alertDialog2.setMessage("Tidak ada histori untuk pencarian tersebut");

                // Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                alertDialog2.show();
            }
        }
    }
}

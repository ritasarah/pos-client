package sarah.rita.pos_client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Belanja extends ActionBarActivity {

//    private ArrayList<String> judulSaved;
//    private ArrayList<Integer> hargaSaved;
//    private ArrayList<Integer> stokSaved;
//    private ArrayList<String> linkSaved;

    private ArrayList<String> namaBarang;
    private ArrayList<Integer> qtyBarang;

    private class productObj {
        public String namaBarang;
        public String urlImage;
        public int stok;
        public long harga;
        public int id;

        public productObj (String _nama, String _url, int _qty, long _harga, int _id) {
            namaBarang = _nama;
            stok = _qty;
            urlImage = _url;
            harga = _harga;
            id = _id;
        }
    }

    private class boughtObj {
        public String namaBarang;
        public int qtyDibeli;
        public long totalBeli;
        public String urlImage;
        public int id;

        public boughtObj(String _nama, int _qty, long _sum, String _url, int _id) {
            namaBarang = _nama;
            qtyDibeli = _qty;
            totalBeli = _sum;
            urlImage = _url;
            id = _id;
        }
    }

    private ArrayList<boughtObj> boughObjList;
    private ArrayList<productObj> productObjList;
    private LinearLayout scrollViewLayout;
    private LinearLayout scrollViewBoughtLayout;

    int id_user = 0 ;
    long saldo = 0;
    String nama = null;
    long curSaldo = 0;
    String token = "";
//    String base_url = "http://pos-fingerprint.herokuapp.com/";
    String base_url = "http://pos-server-fp.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        namaBarang = new ArrayList<>();
        qtyBarang = new ArrayList<>();
        boughObjList = new ArrayList<>();
        productObjList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belanja);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Bundle b = getIntent().getExtras();
            if(b != null) {
            id_user= b.getInt("id");
            nama = b.getString("nama");
            saldo = b.getLong("saldo");
            token = b.getString("token");
            Log.d("saldo", String.valueOf(saldo));
        }
        curSaldo=saldo;

        if (isConnectNetwork() && isNetworkAvailable()) {
            Viewer v = new Viewer();
            v.execute();
        }
        else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
//            builder.setMessage("Koneksi internet Anda bermasalah")
//                    .setCancelable(false)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                        //do things
//                        Intent intent = new Intent(Belanja.this, MenuUtama.class);
//                        Bundle b = new Bundle();
//                        b.putInt("id", id); //Your id
//                        b.putLong("saldo",curSaldo);
//                        b.putString("nama",nama);
//                        intent.putExtras(b); //Put your id to your next Intent
//                        startActivity(intent);
//                        finish();
//                        }
//                    });
//            AlertDialog alert = builder.create();
//            alert.show();
            dialogNoInet();
        }
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

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_belanja, container, false);
            return rootView;
        }
    }

    public void dialogNoInet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
        builder.setMessage("Internet Anda bermasalah")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        Intent intent = new Intent(Belanja.this, MenuUtama.class);
                        Bundle b = new Bundle();
                        b.putInt("id", id_user); //Your id
                        b.putLong("saldo", curSaldo);
                        b.putString("nama", nama);
                        b.putString("token", token);
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private int searchInBoughtList (String _nama) {
        int i = 0;
        boolean found = false;
        while (i<boughObjList.size() && !found) {
            if (boughObjList.get(i).namaBarang.equals(_nama))
                found = true;
            else
                i++;
        }
        if (found) return i;
        else return -1;
    }

    private int searchInProductList (String _nama) {
        int i = 0;
        boolean found = false;
        while (i<boughObjList.size() && !found) {
            if (boughObjList.get(i).namaBarang.equals(_nama))
                found = true;
            else
                i++;
        }
        if (found) return i;
        else return -1;
    }

    private void setInfoBeliSaldo() {
        long _saldoAwal = 0, _saldoAkhir = 0, _totalBeli = 0;

        _saldoAwal = saldo;
        if (boughObjList.size() > 0) {
            for (int i=0; i<boughObjList.size(); i++)
                _totalBeli += boughObjList.get(i).totalBeli;
        }
        _saldoAkhir = _saldoAwal - _totalBeli;

        TextView saldoAwal = (TextView) findViewById(R.id.saldo_awal);
        TextView saldoAkhir = (TextView) findViewById(R.id.saldo_akhir);
        TextView totalBeli = (TextView) findViewById(R.id.total);

        saldoAwal.setText(Long.toString(_saldoAwal));
        saldoAkhir.setText(Long.toString(_saldoAkhir));
        totalBeli.setText(Long.toString(_totalBeli));
    }

    public void generateUI (final String judul, final int harga, final int stok, final String linkGambar, final int id_produk) {
        Display display = getWindowManager().getDefaultDisplay();
        int image_width = display.getWidth()/5;
        int image_height = (int) ((image_width*3)/4);

        // Add image View
        ImageView GambarIV = new ImageView(this);

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

        // Make component and attach to layout

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
        String infoStr = "Nama: " + judul;
        infoTV.setText(infoStr);
//        infoTV.setTextColor(defaultColor);
        infoTV.setLayoutParams(marginVertical);
//        infoTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
        infoLayout.addView(infoTV);

        infoTV = new TextView(this);
        infoStr = "Harga: " + harga;
        infoTV.setText(infoStr);
        infoTV.setLayoutParams(marginVertical);
        infoLayout.addView(infoTV);

        infoTV = new TextView(this);
        infoStr = "Stok: " + stok;
        infoTV.setText(infoStr);
        infoTV.setLayoutParams(marginVertical);
        infoLayout.addView(infoTV);

        rowLayout.addView(infoLayout);


        // Make buy button
        Button buyBtn = new Button(Belanja.this);
        buyBtn.setText("Beli");
        buyBtn.setLayoutParams(marginHorizontal);
        rowLayout.addView(buyBtn);

        final String finalJudul = judul;
        final String finalLinkGambar = linkGambar;
        buyBtn.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Belanja.this);

                    // Setting Dialog Title
                    alertDialog2.setTitle("Masukkan Jumlah");

                    // Setting Dialog Message
                    alertDialog2.setMessage("Masukkan jumlah benda yang hendak dibeli");
                    final EditText input = new EditText(Belanja.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alertDialog2.setView(input);

                    // Setting Positive "Yes" Btn
                    alertDialog2.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int qty = 0;
                                try{
                                    qty = Integer.parseInt(String.valueOf(input.getText()));} // quantity barang yang hendak dibeli
                                catch (Exception e){

                                }
                                int idx_produkDibeli = searchInBoughtList(judul);
                                int stok_tersisa = 0;
                                if (idx_produkDibeli > -1) //pernah beli barang itu sebelumnya
                                    stok_tersisa = stok - boughObjList.get(idx_produkDibeli).qtyDibeli;
                                else
                                    stok_tersisa = stok;

                                // cek cursaldo - (qty x harga) > 0
                                if ((curSaldo - (qty* harga) >= 0) && (stok_tersisa >= qty) &&(qty>0)) {
                                    Log.d("saldo",String.valueOf(saldo));
                                    Log.d("cursaldo",String.valueOf(curSaldo));

                                    curSaldo  = curSaldo - (Integer.parseInt(String.valueOf(input.getText())) * harga);
//                                    if(namaBarang.contains(judul)){

                                    if (idx_produkDibeli > -1) {
//                                        qtyBarang.set(namaBarang.indexOf(judul),(Integer.parseInt(String.valueOf(input.getText()))+qtyBarang.get(namaBarang.indexOf(judul))));
//                                        qtyBarang.set(namaBarang.indexOf(judul),(qty));
                                        if (idx_produkDibeli > -1) {
                                            boughObjList.get(idx_produkDibeli).qtyDibeli += qty;
                                            boughObjList.get(idx_produkDibeli).totalBeli += Long.valueOf(qty * harga);
                                        }
                                    }
                                    else{
                                        Integer sumInt = qty * harga;
                                        long sum = sumInt.longValue();

                                        productObj pO = new productObj(judul, linkGambar, stok, harga, id_produk);
                                        productObjList.add(pO);

                                        boughtObj bO = new boughtObj(judul, qty, sum, linkGambar, id_produk);
                                        boughObjList.add(bO);

//                                        qtyBarang.add(qty);
//                                        namaBarang.add(judul);
                                    }
                                    Log.d("nama barangs",namaBarang.toString());
                                    Log.d("qty barangs",qtyBarang.toString());

//                                    addBoughtList(judul, Integer.parseInt(String.valueOf(input.getText())), linkGambar, qty * harga);
//                                    addBoughtList(judul, qty, linkGambar, qty * harga);
                                    clearSVBoughtLayout();
                                    addBoughtList();
                                    setInfoBeliSaldo();
                                }
                                else if (curSaldo - (qty * harga) < 0) {
                                    // Write your code here to execute after dialog
                                    Toast.makeText(getApplicationContext(),
                                            "Saldo Anda tidak cukup", Toast.LENGTH_SHORT)
                                            .show();
                                    alertDialog2.create().dismiss();
                                }
                                else if (stok_tersisa < qty) {
                                    // Write your code here to execute after dialog
                                    Toast.makeText(getApplicationContext(),
                                            "Stok barang tidak mencukupi", Toast.LENGTH_SHORT)
                                            .show();
                                    alertDialog2.create().dismiss();
                                }
                            }
                        });

                    // Setting Negative "NO" Btn
                    alertDialog2.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(),
                                        "Pembelian Dibatalkan", Toast.LENGTH_SHORT)
                                        .show();
                                dialog.cancel();
                            }
                        });

                    // Showing Alert Dialog
                    alertDialog2.show();
                }
            }
        );

        // Attack rowLayout to mainLayout
        scrollViewLayout.addView(rowLayout);
    }

//    public void addBoughtList(final String namaBarang, final int qty, String imageURL, final long sum) {
    public void addBoughtList() {

        Display display = getWindowManager().getDefaultDisplay();
        int image_width = display.getWidth()/6;
        int image_height = (int) ((image_width*3)/4);

        for (int i=0; i<boughObjList.size(); i++) {

            final String namaBarang = boughObjList.get(i).namaBarang;
            final int qty = boughObjList.get(i).qtyDibeli;
            String imageURL = boughObjList.get(i).urlImage;
            final long sum = boughObjList.get(i).totalBeli;

            // Add image View
            ImageView GambarIV = new ImageView(this);

            // Define margins
            LinearLayout.LayoutParams marginVertical = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            marginVertical.setMargins(5, 0, 0, 0);

            LinearLayout.LayoutParams marginHorizontal = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            marginHorizontal.setMargins(0, 5, 0, 0);

            // Define layout
            final LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout infoLayout = new LinearLayout(this);
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            // Make component and attach to layout

//            Loading image from below url into imageView
            Picasso.with(this)
                    .load(imageURL)
                    .resize(image_height, image_width)
                    .centerInside()
                    .into(GambarIV);
            GambarIV.setLayoutParams(marginHorizontal);
            rowLayout.addView(GambarIV);


            // Make info component
            TextView infoTV = new TextView(this);
            String infoStr = "Nama: " + namaBarang;
            infoTV.setText(infoStr);
            //        infoTV.setTextColor(defaultColor);
            infoTV.setLayoutParams(marginVertical);
            //        infoTV.setTextColor(getResources().getColor(R.color.defaultFontColor));
            infoLayout.addView(infoTV);

            infoTV = new TextView(this);
            infoStr = "Jumlah beli: " + qty;
            infoTV.setText(infoStr);
            infoTV.setLayoutParams(marginVertical);
            infoLayout.addView(infoTV);

            infoTV = new TextView(this);
            infoStr = "Total beli: " + sum;
            infoTV.setText(infoStr);
            infoTV.setLayoutParams(marginVertical);
            infoLayout.addView(infoTV);

            rowLayout.addView(infoLayout);


            // Make delete button
            Button delBtn = new Button(Belanja.this);
            delBtn.setText("Hapus");
            delBtn.setLayoutParams(marginHorizontal);
            rowLayout.addView(delBtn);

            delBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            scrollViewBoughtLayout.removeView(rowLayout);
                            int idxDibeli = searchInBoughtList(namaBarang);
                            int idxProduk = searchInBoughtList(namaBarang);

                            if ((idxDibeli > -1) && idxProduk > -1) {
                                long total_dibeli = Long.valueOf(boughObjList.get(idxDibeli).qtyDibeli) * productObjList.get(idxProduk).harga;
                                curSaldo += total_dibeli;
                                boughObjList.remove(idxDibeli);
                                clearSVBoughtLayout();
                                addBoughtList();
                                setInfoBeliSaldo();
                            }
                            else {
                                // ada error, handle! exception kah?
                            }
                        }
                    }
            );

            // Make change button
            Button chgBtn = new Button(Belanja.this);
            chgBtn.setText("Ubah");
            chgBtn.setLayoutParams(marginHorizontal);
            rowLayout.addView(chgBtn);

            chgBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Belanja.this);

                            // Setting Dialog Title
                            alertDialog2.setTitle("Masukkan Jumlah");

                            // Setting Dialog Message
                            alertDialog2.setMessage("Masukkan jumlah benda yang hendak dibeli");
                            final EditText input = new EditText(Belanja.this);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            alertDialog2.setView(input);
                            input.setText(Integer.toString(qty));

                            // Setting Positive "Yes" Btn
                            alertDialog2.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            int newQty = Integer.parseInt(String.valueOf(input.getText())); // quantity baru yang mau dibeli user
                                            int idxDibeli = searchInBoughtList(namaBarang);
                                            int idxProduk = searchInProductList(namaBarang);

                                            int stokAwal = productObjList.get(idxProduk).stok;
                                            int stokDibeli = boughObjList.get(idxDibeli).qtyDibeli;
                                            int stok_tersisa = stokAwal - stokDibeli;

//                                            int stok_tersisa = productObjList.get(idxProduk).stok - boughObjList.get(idxDibeli).qtyDibeli;
                                            if (stok_tersisa >= 0) { // masih bisa beli
                                                boughObjList.get(idxDibeli).qtyDibeli = newQty;
                                                boughObjList.get(idxDibeli).totalBeli = Long.valueOf(newQty * productObjList.get(idxProduk).harga);
                                                clearSVBoughtLayout();
                                                addBoughtList();
                                                setInfoBeliSaldo();
                                            }
                                            else {
                                                // gabisa beli lagi karena stok produk kurang
                                                Toast.makeText(getApplicationContext(),
                                                    "Stok produk tidak mencukupi", Toast.LENGTH_SHORT)
                                                    .show();
                                            }

                                            //                                        // cek cursaldo - (qty x harga) > 0
                                            //                                        if ((curSaldo - sum > 0) && (stok > Integer.parseInt(String.valueOf(input.getText())))) {
                                            //                                            Log.d("saldo",String.valueOf(saldo));
                                            //                                            curSaldo  = curSaldo - (Integer.parseInt(String.valueOf(input.getText())) * harga);
                                            //                                            Log.d("cursaldo",String.valueOf(curSaldo));
                                            //                                            if(namaBarang.contains(judul)){
                                            //                                                qtyBarang.set(namaBarang.indexOf(judul),(Integer.parseInt(String.valueOf(input.getText()))+qtyBarang.get(namaBarang.indexOf(judul))));
                                            //                                            }
                                            //                                            else{
                                            //                                                qtyBarang.add(Integer.parseInt(String.valueOf(input.getText())) );
                                            //                                                namaBarang.add(judul);
                                            //                                            }
                                            //                                            Log.d("nama barangs",namaBarang.toString());
                                            //                                            Log.d("qty barangs",qtyBarang.toString());
                                            //                                        }else {
                                            //                                            // Write your code here to execute after dialog
                                            //                                            Toast.makeText(getApplicationContext(),
                                            //                                                    "Gagal Memroses", Toast.LENGTH_SHORT)
                                            //                                                    .show();
                                            //                                            alertDialog2.create().dismiss();
                                            //                                        }
                                        }
                                    });

                            // Setting Negative "NO" Btn
                            alertDialog2.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog
                                            Toast.makeText(getApplicationContext(),
                                                    "Pembelian Dibatalkan", Toast.LENGTH_SHORT)
                                                    .show();
                                            dialog.cancel();
                                        }
                                    });

                            // Showing Alert Dialog
                            alertDialog2.show();
                        }
                    }
            );


            // Attack rowLayout to mainLayout
            scrollViewBoughtLayout.addView(rowLayout);
        }
    }

    public void clearSVBoughtLayout () {
        scrollViewBoughtLayout.removeAllViews();
    }

    public boolean isConnectNetwork() {
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void lanjutBelanja(View v) {
        if(boughObjList.size() > 0 ){
            if (isConnectNetwork() && isNetworkAvailable()) {
                for (int i = 0; i < boughObjList.size(); i++) {
                    Poster p = new Poster(boughObjList.get(i).id, boughObjList.get(i).qtyDibeli);
                    p.execute();
                }
                PostSaldo po = new PostSaldo();
                po.execute();
            }
            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
//                builder.setMessage("Koneksi internet Anda bermasalah")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //do things
//                                Intent intent = new Intent(Belanja.this, MenuUtama.class);
//                                Bundle b = new Bundle();
//                                b.putInt("id", id); //Your id
//                                b.putLong("saldo",curSaldo);
//                                b.putString("nama",nama);
//                                intent.putExtras(b); //Put your id to your next Intent
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
                dialogNoInet();
            }
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Silahkan masukkan pembelian terlebih dahulu", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void backBelanjaClicked(View v) {
        Intent intent = new Intent(this, MenuUtama.class);
        Bundle b = new Bundle();
        b.putInt("id", id_user); //Your id
        b.putLong("saldo",curSaldo);
        b.putString("nama",nama);
        b.putString("token",token);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    class Viewer extends AsyncTask<String, String, String> {
//        ProgressDialog progressDialog;
        JSONArray arrRes;

        @Override
        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(Belanja.this, "Loading", "Harap tunggu sebentar..");
            scrollViewLayout = new LinearLayout(Belanja.this);
            scrollViewLayout.setOrientation(LinearLayout.VERTICAL);

            scrollViewBoughtLayout = new LinearLayout(Belanja.this);
            scrollViewBoughtLayout.setOrientation(LinearLayout.VERTICAL);
        }

        @Override
        protected String doInBackground(String... params) {
//            if(isNetworkAvailable()) {
                String result = "";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(base_url+"api/getbarang");
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
                        arrRes = new JSONArray();
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    arrRes = new JSONArray();
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

//            if ((!Belanja.this.isFinishing()) && progressDialog != null)
//                progressDialog.dismiss();
            if (arrRes.length() > 0) {
                for (int i=0;i<arrRes.length();i++) {
                    JSONObject res = null;
                    try {
                        res = (JSONObject) arrRes.get(i);
                        Log.d("json oj", res.toString());

                        String nama = res.getString("nama");
                        int stok = res.getInt("stok");
                        int harga = res.getInt("harga");
                        int id_produk = res.getInt("id");
                        String url = base_url + "asset/img/" + res.getString("icon");
                        generateUI(nama, harga, stok, url, id_produk);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
//                final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(Belanja.this);
//                // Setting Dialog Title
//                alertDialog2.setTitle("Informasi");
//                // Setting Dialog Message
//                alertDialog2.setMessage("Terdapat kesalahan, silakan ulangi kembali");
//
//                // Setting Positive "Yes" Btn
//                alertDialog2.setPositiveButton("OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//
//                alertDialog2.show();
                dialogNoInet();
            }

            // Search scroll view 'daftar belanja'
            ScrollView daftarBelanjaSV = (ScrollView) findViewById(R.id.scrollview_listbelanja);
            daftarBelanjaSV.addView(scrollViewLayout);

            ScrollView daftarBelanjaDibeliSV = (ScrollView) findViewById(R.id.scrollview_belanja);
            daftarBelanjaDibeliSV.addView(scrollViewBoughtLayout);
        }
    }

    public class Poster extends AsyncTask<String, String, String> {
        int id_barang;
        int kuantitas;
        ProgressDialog progressDialog;
        boolean sent = false;

        Poster(int idbarang,int kuantita){
            id_barang = idbarang;
            kuantitas = kuantita;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Belanja.this, "Loading", "Harap tunggu sebentar..");
        }

        @Override
        protected String doInBackground(String... params) {
            //            if(isNetworkAvailable()) {
            String result = "";

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(base_url + "api/posthistori?id_user="+id_user+"&id_barang="+id_barang+"&kuantitas="+kuantitas+"&token="+token);
            HttpResponse response;

            try {
                response = client.execute(request);
                sent = true;

//                // Get the response
//                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//                String line = "";
//                while ((line = rd.readLine()) != null) {
//                    result += line;
//                }
//                try {
//                    // Data
//                    JSONObject arrRes = new JSONObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
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
            if (!sent)
                Toast.makeText(getApplicationContext(), "Anda tidak terhubung ke internet", Toast.LENGTH_SHORT).show();
            Log.d("kuantias", String.valueOf(kuantitas));
        }
    }

    public class PostSaldo extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Belanja.this, "Loading", "Harap tunggu sebentar..");
        }

        @Override
        protected String doInBackground(String... params) {
            String message = "";
            String result = "";
            HttpClient client = new DefaultHttpClient();
            String url = base_url + "api/postsaldo?id="+id_user+"&saldo="+curSaldo+"&token="+token;
            Log.d("url belanja saldo",url);
            HttpGet request = new HttpGet(url);
            HttpResponse response;

            try {
                try {
                    response = client.execute(request);

                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result += line;
                    }
//                    JSONArray arrRes = new JSONArray(result);
                    JSONObject res = new JSONObject(result);
                    message = res.getString("token");
                    Log.d("message",message);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (JSONException e) {
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

            return message;
        }

        @Override
        protected void onPostExecute(final String _token) {
            progressDialog.dismiss();
            if (_token != null) {
                if (_token.length()>0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
                    builder.setMessage("Pembelian berhasil dilakukan")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    Intent intent = new Intent(Belanja.this, MenuUtama.class);
                                    Bundle b = new Bundle();
                                    b.putInt("id", id_user); //Your id
                                    b.putLong("saldo", curSaldo);
                                    b.putString("nama", nama);
                                    b.putString("token", _token);
                                    intent.putExtras(b); //Put your id to your next Intent
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
                    builder.setMessage("Pembelian gagal")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Belanja.this);
//                builder.setMessage("Internet Anda bermasalah")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //do things
//                                Intent intent = new Intent(Belanja.this, MenuUtama.class);
//                                Bundle b = new Bundle();
//                                b.putInt("id", id_user); //Your id
//                                b.putLong("saldo", curSaldo);
//                                b.putString("nama", nama);
//                                intent.putExtras(b); //Put your id to your next Intent
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
                dialogNoInet();
            }
        }
    }

    public class PostTask extends AsyncTask<Void, Void, JSONObject> {
        private final String TAG = "HttpClient";
        private JSONObject result = null;

        @Override
        protected JSONObject doInBackground(Void... params) {
            String sendMessage;

            try {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPostRequest = new HttpPost(base_url + "api/posthistory");

                JSONObject sendObject = new JSONObject();
                sendObject.put("id_user",1);
                sendObject.put("id_barang",2);
                sendObject.put("kuantitas",3);
                sendMessage = sendObject.toString();

                StringEntity se;
                se = new StringEntity(sendMessage);

                // Set HTTP parameters
                httpPostRequest.setEntity(se);
                httpPostRequest.setHeader("Accept", "application/json");
                httpPostRequest.setHeader("Content-type", "application/json");

                long t = System.currentTimeMillis();
                HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
                Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // Read the content stream
                    InputStream instream = entity.getContent();

                    // convert content stream to a String
                    String resultString = convertStreamToString(instream);
                    instream.close();
                    resultString = resultString.substring(1, resultString.length() - 1); // remove wrapping "[" and "]"

                    JSONObject jsonObjRecv = new JSONObject(resultString);

                    // Raw DEBUG output of our received JSON object:
                    Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                    return jsonObjRecv;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

}

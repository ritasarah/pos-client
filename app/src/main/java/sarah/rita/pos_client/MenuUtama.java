package sarah.rita.pos_client;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MenuUtama extends ActionBarActivity {

    int id = 0; // or other values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle b = getIntent().getExtras();
        String nama=null;

//        if(b != null)
//            nama = b.getString("nama");

        TextView hai = (TextView) findViewById(R.id.hai);
//        hai.append(nama);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_utama, menu);
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

    public void belanjaClicked(View v){
        Toast.makeText(this, "Belanja" , Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Belanja.class);
        Bundle b = new Bundle();
        b.putInt("id", id); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    public void riwayatClicked(View v){
        Toast.makeText(this, "Riwayat" , Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, Riwayat.class);
        Bundle b = new Bundle();
        b.putInt("id", id); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }



}

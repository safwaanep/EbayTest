package com.akiya.myfirstapp;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String POKEMON_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    private DatePickerDialog dpd = null;
    private PokemonAdapter pokeAdapt;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toast_me:
                Toast.makeText(this, "toast", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.button1);
        TextView tv_textView = (TextView) findViewById(R.id.textView);
        String now = DateUtils.formatDateTime(getApplicationContext(), (new Date()).getTime(), DateFormat.FULL);
        tv_textView.setText(now);

        // DatePickerDialog
        DatePickerDialog.OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                TextView tv_textView = (TextView) findViewById(R.id.textView);
                tv_textView.setText(i2 + "/" + i1 +"/"+ i);
            }
        };
        dpd= new DatePickerDialog(this,odsl,2018,10,5);

        //Services & Threading
        IntentFilter intentFilter = new IntentFilter(POKEMON_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new PokemonUpdate(),intentFilter);

        GetBiersServices.startActionBiers(this);

        //Recycler View
        RecyclerView rv = findViewById(R.id.rv_pokemon);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        pokeAdapt= new PokemonAdapter(getPokemonsFromFile());
        rv.setAdapter(pokeAdapt);



    }

    public void clickButton1(View view) {

        //Toast
        Toast.makeText(getApplicationContext(),getString(R.string.msg),Toast.LENGTH_LONG).show();

        // DatePickerDialog
        dpd.show();
    }

    public void clickButton2(View view){
        // Notification
        notification_test();
    }

    public void clickButton3(View view){
        // Intent
        Intent i = new Intent(this,SecondActivity.class);
        startActivity(i);
    }

    public void notification_test(){
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("The title")
                .setContentText("The content")
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notifBuilder.build());
    }

    public JSONArray getPokemonsFromFile(){
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "pokemon.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONArray array = new JSONObject(new String(buffer, "UTF-8")).getJSONArray("objects");
            return array;
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public class PokemonUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("tag",intent.getAction() + "  recu");
            pokeAdapt.setNewPokemon(getPokemonsFromFile());
        }
    }
}
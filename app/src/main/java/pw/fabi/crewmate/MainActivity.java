package pw.fabi.crewmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    boolean hasPermission = false;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.Swbutton);
        hasPermission = requestFilePermission();

        btn.setOnClickListener(v -> {
            String addr = ((EditText) findViewById(R.id.editTextTextAddr)).getText().toString();
            if(addr.length() < 4){
                // weird bypass
                showToast("Please type an IP Address or Domain");
                btn.setBackgroundColor(getResources().getColor(R.color.red));
                return;
            }

            if(!addr.startsWith("http://") & !addr.startsWith("https://")){ // make sure address starts with http://
                addr = "http://" + addr;
            }

            String ipAddr = new IPHandler().getIp(addr,MainActivity.this);
            final String port = ((EditText) findViewById(R.id.editTextTextPort)).getText().toString();

            if(port.length() < 2) {
                showToast("Please type a Port");
                btn.setBackgroundColor(getResources().getColor(R.color.red));
                return;
            }
            if(ipAddr != null){
                switchIpAddr(ipAddr.split("/")[1], port);
            }
        });
    }

    public void switchIpAddr(String addr, String port){

        // Google Play report this (people typing 2202322023?)
       short shortPort = 0;
        try{
            shortPort = Short.parseShort(port);
        }
        catch (NumberFormatException ex){
            showToast("Max port number: 65534. Make sure it is correct (Default Port: 22023)");
            btn.setBackgroundColor(getResources().getColor(R.color.red));
            ((EditText) findViewById(R.id.editTextTextPort)).setText("22023");
            ex.printStackTrace();
            return;
        }

        boolean result = new FileHandler().openFile(Environment.getExternalStorageDirectory().toString() + "/Android/data/xyz.crowdedmods.crowdedmod/files/regionInfo.dat")
                .replaceFile("Impostor", addr, shortPort);

        if(result){
            btn.setBackgroundColor(getResources().getColor(R.color.green));
            showToast("Server changed successfully, (re)start the game");
        }
        else{
            btn.setBackgroundColor(getResources().getColor(R.color.red));
            showToast("Error, try to grant permissions (this app doesn't work in android 11)");
        }
    }

    public void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }

    public boolean requestFilePermission(){

        boolean r = false;
        boolean c = false;
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            r = false;
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            c = false;
        }

        String[] permissionArray = new String[2];
        if(!r || !c){
            permissionArray[0] =  Manifest.permission.WRITE_EXTERNAL_STORAGE;
            permissionArray[1] =  Manifest.permission.READ_EXTERNAL_STORAGE;
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionArray,
                    0);
            return false;
        }
        else{
            return true;
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            }
            else {
                showToast("App requires file permission to access Among Us files");
                hasPermission = false;
            }
    }
}

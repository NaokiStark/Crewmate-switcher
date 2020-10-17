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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //((TextView) findViewById(R.id.textView)).setText();

        Button btn = (Button)findViewById(R.id.Swbutton);
        hasPermission = requestFilePermission();

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        ((EditText) findViewById(R.id.editTextTextAddr)).setFilters(filters);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String addr = ((EditText) findViewById(R.id.editTextTextAddr)).getText().toString();
                if(addr.length() < 7){
                    Toast.makeText(MainActivity.this,
                            "Please type an IP address",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                final String port = ((EditText) findViewById(R.id.editTextTextPort)).getText().toString();
                if(addr.length() < 2) {
                    Toast.makeText(MainActivity.this,
                            "Please type a Port",
                            Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                switchIpAddr(addr, port);
            }
        });


    }


    public void switchIpAddr(String addr, String port){

        short shortPort = Short.parseShort(port);

        boolean result = new FileHandler().openFile(Environment.getExternalStorageDirectory().toString()+"/Android/data/com.innersloth.spacemafia/files/regionInfo.dat").replaceFile("Impostor", addr, shortPort);

        if(result){
            Toast.makeText(MainActivity.this,
                    "Server changed successfully, (re)start the game",
                    Toast.LENGTH_LONG)
                    .show();
        }
        else{
            Toast.makeText(MainActivity.this,
                    "Error, try to grant permissions (this app doesn't work in android 11)",
                    Toast.LENGTH_LONG)
                    .show();
        }
        //Log.d("CREWMATE" ,"Result file: " + Boolean.toString(result));
    }

    public void listFilesForFolder(final File folder) {
        File[] filesList = folder.listFiles();
        for (final File fileEntry : filesList) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
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
                /*Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();*/
                hasPermission = true;
            }
            else {
                Toast.makeText(MainActivity.this,
                        "App requires file permission to access Among Us files",
                        Toast.LENGTH_SHORT)
                        .show();
                hasPermission = false;
            }

    }

}
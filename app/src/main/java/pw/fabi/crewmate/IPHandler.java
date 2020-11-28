package pw.fabi.crewmate;

import android.os.StrictMode;
import android.widget.Toast;

import java.net.*;
import java.*;

public class IPHandler {
    public String getIp(String url, MainActivity context){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }  // Policy stuff for android 8+
        try {
            // Among Us only allows IPv4
            Inet4Address ip = (Inet4Address) Inet4Address.getByName(new URL(url).getHost());

            return ip.toString();
        } catch (MalformedURLException e) {
            Toast.makeText(context,"Invalid Address",Toast.LENGTH_LONG).show();
        } catch (UnknownHostException e) {
            Toast.makeText(context,"Unknown error occurred",Toast.LENGTH_LONG).show();
        }
        return null;
    }
}

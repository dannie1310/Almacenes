package mx.grupohi.almacenes.almacensao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Creado por JFEsquivel on 07/10/2016.
 */

class Util {

    static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public static boolean isGpsEnabled(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            String providers = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(providers)) {
                return false;
            }
            return providers.contains(LocationManager.GPS_PROVIDER);
        } else {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
    }

    static String getQuery(ContentValues values) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : values.valueSet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }
        return result.toString();
    }

    static String timeStamp() {
        return (String) android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
    }

    static Integer getIdCamion(String string) {

        try {
            Integer result = Integer.valueOf(string.substring(0,4));
            if (result != null) {
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static Integer getIdProyecto(String string) {
        try {
            Integer result = Integer.valueOf(string.substring(4,8));
            if(result != null) {
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    static Integer getIdMaterial(String string) {
        try {
            Integer result = Integer.valueOf(string.substring(0,4));
            if(result != null) {
                return result;
            } else {
                return null;
            }
        } catch (Exception e){
            return null;
        }
    }

   static Integer getIdOrigen(String string) {
       try {
           Integer result = Integer.valueOf(string.substring(4,8));
           if(result != null) {
               return result;
           } else {
               return null;
           }
       } catch (Exception e){
           return null;
       }
   }

    static String concatenar(String id, String id1){
        String aux = id;
        String aux1 = id1;
        for(int i = id.length(); i < 4; i++){
            aux = 0 + aux;
        }
        for(int i = id1.length(); i < 4; i++){
            aux1 = 0 + aux1;
        }
        return aux + aux1;
    }

    static String getFechaHora() {
        return (String) android.text.format.DateFormat.format("HHmmssyyyyMMdd", new Date());
    }

    static String getFechaSegundos() {
        return (String) android.text.format.DateFormat.format("yyMMddHHmmss", new Date());
    }

    static String getFecha() {
        return (String) android.text.format.DateFormat.format("yyyy/MM/dd", new Date());
    }

    static String getFecha(String string) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmssyyyyMMdd");
        try {
            Date date = format.parse(string);
            SimpleDateFormat fechaFormat = new SimpleDateFormat("yyyy/MM/dd");

            return fechaFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getTime() {
        return (String) android.text.format.DateFormat.format("HH:mm:ss", new Date());
    }

    static String getTime(String string) {
        SimpleDateFormat format = new SimpleDateFormat("HHmmssyyyyMMdd");
        try {
            Date date = format.parse(string);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            return timeFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void copyDataBase(Context mActivity) throws IOException {
        InputStream myInput = new FileInputStream(new File("/data/user/0/" + mActivity.getPackageName() + "/databases/sca"));
        File files = new File("/sdcard/Android/");
        files.mkdirs();
        String outFileName = "/sdcard/Android/data/by.androld.app.dbreader/files/almacenes.sqlite";
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int bufferLength;
        while ((bufferLength = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, bufferLength);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}

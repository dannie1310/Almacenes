package mx.grupohi.almacenes.almacensao;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Usuario on 06/03/2017.
 */

public class Sincronizar  extends AsyncTask<Void, Void, Boolean> {

        private Context context;
        private ProgressDialog progressDialog;
        private Usuario usuario;


        private JSONObject JSONCAMIONES;
        private JSONObject JSON;
        Integer imagenesRegistradas = 0;
        Integer imagenesTotales = 0;
        String IMEI;

        Sincronizar(Context context, ProgressDialog progressDialog) {

            this.context = context;
            this.progressDialog = progressDialog;
            usuario = new Usuario(context);
            usuario = usuario.getUsuario();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /*TelephonyManager phneMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = phneMgr.getDeviceId();*/
            ContentValues values = new ContentValues();

            values.clear();
            values.put("metodo", "capturaActualizacionCamiones");
            values.put("usr", usuario.usr);
            values.put("pass", usuario.pass);
            values.put("usuarioCADECO", usuario.usuarioCADECO);
            values.put("idusuario", usuario.idUsuario);
            values.put("Version", String.valueOf(BuildConfig.VERSION_NAME));
           // values.put("id_proyecto", usuario.idProyecto);
           // values.put("IMEI",IMEI);


            if (Entrada.getCount(context) != 0) {
                JSONObject entradas = EntradaDetalle.getJSON(context);
                values.put("Entradas", String.valueOf(entradas));
            }
            if (Salida.getCount(context) != 0){
                JSONObject salidas = SalidaDetalle.getJSON(context);
                values.put("Salidas", String.valueOf(salidas));
            }
            if (Transferencia.getCount(context) != 0){
                JSONObject transferencias = TransferenciaDetalle.getJSON(context);
                values.put("Transferencias", String.valueOf(transferencias));
            }
            //System.out.println("JSON: "+String.valueOf(values));
            /*try {

                URL url = new URL("http://sca.grupohi.mx/android20160923.php");
                JSONCAMIONES = HttpConnection.POST(url, values);*/
                System.out.println("JSON: "+String.valueOf(values));
                /*ContentValues aux = new ContentValues();
                int i = 0;
                imagenesTotales= ImagenesCamion.getCount(context);
                System.out.println("JSON2: "+JSONCAMIONES);
                while (ImagenesCamion.getCount(context) != 0) {
                    i++;
                    JSON = null;
                    //System.out.println("Existen imagenes para sincronizar: " + ImagenesViaje.getCount(context));
                    aux.put("metodo", "cargaImagenesCamiones");
                    aux.put("usr", usuario.usr);
                    aux.put("pass", usuario.pass);
                    aux.put("bd", usuario.baseDatos);
                    aux.put("Imagenes", String.valueOf(ImagenesCamion.getJSONImagenes(context)));

                    try {
                        JSON = HttpConnection.POST(url, aux);
                        Log.i("json-Imagenes", String.valueOf(aux));
                        try {
                            if (JSON.has("imagenes_registradas")) {
                                final JSONArray imagenes = new JSONArray(JSON.getString("imagenes_registradas"));
                                for (int r = 0; r < imagenes.length(); r++) {
                                    ImagenesCamion.syncLimit(context, imagenes.getInt(r));
                                    imagenesRegistradas++;
                                }
                            }
                            System.out.println("JSON3: "+JSON);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }catch (Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }*/
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if(aBoolean) {
                try {
                   /* if (JSONCAMIONES.has("error_ambos")) {
                        Toast.makeText(context, (String) JSONCAMIONES.get("error_ambos"), Toast.LENGTH_SHORT).show();
                    }*/

                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }


    }

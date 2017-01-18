package mx.grupohi.almacenes.almacensao;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SeleccionaObraActivity extends AppCompatActivity {

    private CatalogosTask mAuthTask = null;
    private Spinner spinner;
    private HashMap<String, String> spinnerMap;
    private String idObra;
    private Button seleccionar;
    private ProgressDialog mProgressDialog;
    private DBScaSqlite db_sca;
    Intent main;
    Usuario user;
    Obras obras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_obra);

        db_sca = new DBScaSqlite(getApplicationContext(), "sca", null, 1);
        spinner = (Spinner) findViewById(R.id.spinner);

        user = new Usuario(getApplicationContext());
        obras = new Obras(getApplicationContext());
        final ArrayList<String> nombres = obras.getArrayListNombres();
        final ArrayList<String> ids = obras.getArrayListId();

        String[] spinnerArray = new String[ids.size()];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            spinnerMap.put(nombres.get(i), ids.get(i));
            spinnerArray[i] = nombres.get(i);
        }

        final ArrayAdapter<String> a = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner.setAdapter(a);

        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String nombre = spinner.getSelectedItem().toString();
                    idObra = spinnerMap.get(nombre);
                    System.out.println("add: " + idObra + nombre);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        seleccionar = (Button) findViewById(R.id.button_write);

        seleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Selecciona y sincorniza");
                System.out.println("addboton: " + idObra);

                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                    attemptLogin(Integer.valueOf(idObra));
                } else {
                    Toast.makeText(SeleccionaObraActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void attemptLogin(Integer id) {

        obras = obras.find(id);
        System.out.println("Find: " + obras.id_base + obras.id_obra);
        mProgressDialog = ProgressDialog.show(SeleccionaObraActivity.this, "Autenticando", "Por favor espere...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAuthTask = new CatalogosTask(obras.id_base, obras.id_obra);
                mAuthTask.execute((Void) null);
            }
        }).run();
    }

    public class CatalogosTask extends AsyncTask<Void, Void, Boolean> {

        private final Integer id_base;
        private final Integer id_obras;

        CatalogosTask(Integer idbase, Integer idobras) {
            id_base = idbase;
            id_obras = idobras;
            System.out.println("Proyecto: " + idbase + idobras);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            ContentValues data = new ContentValues();
            data.put("controlador", "SAO");
            data.put("accion", "getCatalogosMovil");
            data.put("idbase", id_base);
            data.put("idobra", id_obras);

            try {
                URL url = new URL(getApplicationContext().getString(R.string.url));

                final JSONObject JSON = HttpConnection.POST(url, data);
                System.out.println("url: " + url + " " + JSON);

                db_sca.deleteCatalogos();
                if (JSON.has("error")) {
                    System.out.println("ERROR");
                    errorMessage((String) JSON.get("error"));
                    return false;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setTitle("Actualizando");
                            mProgressDialog.setMessage("Actualizando datos de usuario...");
                        }
                    });


                    //agregar id obra activa al usuario.

                    /*obras = new Obras(getApplicationContext());
                    try {
                        final JSONArray obra = new JSONArray(JSON.getString("BasesObras"));
                        for (int i = 0; i < obra.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Obras... \n Obra " + finalI + " de " + obra.length());
                                }
                            });
                            System.out.println("obras: " + obra.getJSONObject(i));
                            JSONObject info = obra.getJSONObject(i);
                            data.clear();
                            data.put("nombre", info.getString("nombre"));
                            data.put("idbase", info.getInt("id_base"));
                            data.put("base", info.getString("base"));
                            data.put("idobra", info.getInt("id"));

                            if (!obras.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage(getResources().getString(R.string.general_exception));
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAuthTask = null;
            mProgressDialog.dismiss();
            if (aBoolean) {
                nextActivity();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void errorMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void errorLayout(final TextInputLayout layout, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setErrorEnabled(true);
                layout.setError(message);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity() {
        main = new Intent(this, MainActivity.class);
        startActivity(main);
    }

    @Override
    protected void onStart() {

        super.onStart();
        /*if(user.get()) {
            nextActivity();
        }*/
    }

}

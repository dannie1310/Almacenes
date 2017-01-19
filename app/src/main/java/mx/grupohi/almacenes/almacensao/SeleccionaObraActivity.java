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
    Obra obras;
    OrdenCompra ordenCompra;
    Almacen almacen;
    Material material;
    Contratista contratista;
    MaterialesAlmacen materialesAlmacen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_obra);

        db_sca = new DBScaSqlite(getApplicationContext(), "sca", null, 1);
        spinner = (Spinner) findViewById(R.id.spinner);

        user = new Usuario(getApplicationContext());
        user = user.getUsuario();
        obras = new Obra(getApplicationContext());
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

    private void attemptLogin(final Integer id) {

        obras = obras.find(id);
        System.out.println("Find: " + obras.id_base + obras.id_obra);
        mProgressDialog = ProgressDialog.show(SeleccionaObraActivity.this, "Autenticando", "Por favor espere...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAuthTask = new CatalogosTask(id,obras.id_base, obras.id_obra);
                mAuthTask.execute((Void) null);
            }
        }).run();
    }

    public class CatalogosTask extends AsyncTask<Void, Void, Boolean> {

        private final Integer id_base;
        private final Integer id_obras;
        private final Integer ID;

        CatalogosTask(Integer id,Integer idbase, Integer idobras) {
            ID = id;
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

                db_sca.deleteCatalogosObras();
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
                    boolean resp = user.update(ID);
                    System.out.println("update: "+resp+ID);
                    if(resp){
                    ordenCompra = new OrdenCompra(getApplicationContext());
                    try {
                        final JSONArray ordenes = new JSONArray(JSON.getString("ordenescompra"));
                        for (int i = 0; i < ordenes.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Ordenes de Compra... \n Orden " + finalI + " de " + ordenes.length());
                                }
                            });
                            System.out.println("ordenes: " + ordenes.getJSONObject(i));
                            JSONObject info = ordenes.getJSONObject(i);

                            data.clear();
                            data.put("descripcion", info.getString("descripcion"));
                            data.put("idmaterial", info.getInt("id_material"));
                            data.put("unidad", info.getString("unidad"));
                            data.put("iditem", info.getInt("id_item"));
                            data.put("existencia", info.getString("existencia"));
                            data.put("razonsocial", info.getString("razon_social"));
                            data.put("numerofolio", info.getString("numero_folio"));
                            data.put("preciounitario", info.getInt("precio_unitario"));
                            data.put("idorden", info.getInt("id_transaccion"));

                            if (!ordenCompra.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    almacen = new Almacen(getApplicationContext());
                    try {
                        final JSONArray almacenes = new JSONArray(JSON.getString("almacenes"));
                        for (int i = 0; i < almacenes.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Almacenes... \n Almacen " + finalI + " de " + almacenes.length());
                                }
                            });
                            System.out.println("almacenes: " + almacenes.getJSONObject(i));
                            JSONObject info = almacenes.getJSONObject(i);

                            data.clear();
                            data.put("descripcion", info.getString("descripcion"));
                            data.put("id_almacen", info.getInt("id"));

                            if (!almacen.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    material = new Material(getApplicationContext());
                    try {
                        final JSONArray materials = new JSONArray(JSON.getString("materiales"));
                        for (int i = 0; i < materials.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Materiales... \n Material " + finalI + " de " + materials.length());
                                }
                            });
                            System.out.println("materiales: " + materials.getJSONObject(i));
                            JSONObject info = materials.getJSONObject(i);

                            data.clear();
                            data.put("descripcion", info.getString("descripcion"));
                            data.put("id_material", info.getInt("id_material"));
                            data.put("tipomaterial", info.getInt("tipo_material"));
                            data.put("marca", info.getInt("marca"));

                            if (!material.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    materialesAlmacen = new MaterialesAlmacen(getApplicationContext());
                    try {
                        final JSONArray ma_al = new JSONArray(JSON.getString("materiales_x_almacen"));
                        for (int i = 0; i < ma_al.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Material por Almacén... \n Material " + finalI + " de " + ma_al.length());
                                }
                            });
                            System.out.println("ma_al: " + ma_al.getJSONObject(i));
                            JSONObject info = ma_al.getJSONObject(i);

                            data.clear();
                            data.put("unidad", info.getString("unidad"));
                            data.put("id_material", info.getInt("id_material"));
                            data.put("id_almacen", info.getInt("id_almacen"));
                            data.put("id_obra", info.getInt("id_obra"));
                            data.put("cantidad", info.getInt("cantidad"));

                            if (!materialesAlmacen.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    contratista = new Contratista(getApplicationContext());
                    try {
                        final JSONArray contratistas = new JSONArray(JSON.getString("contratistas"));
                        for (int i = 0; i < contratistas.length(); i++) {
                            final int finalI = i + 1;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Actualizando catálogo de Contratistas... \n Contratista " + finalI + " de " + contratistas.length());
                                }
                            });
                            System.out.println("contratistas: " + contratistas.getJSONObject(i));
                            JSONObject info = contratistas.getJSONObject(i);

                            data.clear();
                            data.put("razonsocial", info.getString("razon_social"));
                            data.put("idempresa", info.getInt("id_empresa"));

                            if (!contratista.create(data)) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }}
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
     /*   if(user.get()) {
            nextActivity();
        }*/
    }

}

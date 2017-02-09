package mx.grupohi.almacenes.almacensao;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SalidaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Usuario usuario;
    Almacen almacen;
    Salida salida;
    SalidaDetalle salidaDetalle;
    ListaMaterialAdaptador lista;
    DialogoRecepcion dialogoRecepcion;

    private HashMap<String, String> spinnerMap;
    EditText concepto;
    EditText referenica_salida;
    EditText observacion_salida;
    ListView materiales;
    Spinner almacen_spinner;
    Button botonSalida;

    String nombrealmacen;
    String idAlmacen;
    Integer idMaterial;
    Integer posicion_almacen;
    Double cantidad;
    ListView mListRecibido;
    ListaDialog listaRecibido;

    SalidaFragment salidaFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salida);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usuario = new Usuario(this);
        usuario = usuario.getUsuario();
        almacen = new Almacen(getApplicationContext());
        dialogoRecepcion = new DialogoRecepcion(getApplicationContext());
        String x = getIntent().getStringExtra("observacion");
        String y = getIntent().getStringExtra("referencia");
        String z = getIntent().getStringExtra("concepto");
        String w =getIntent().getStringExtra("posicion");
        System.out.println("extra: "+ x + y + z + w);

        concepto = (EditText) findViewById(R.id.textConcepto);
        referenica_salida = (EditText) findViewById(R.id.textReferenciaSalida);
        observacion_salida = (EditText) findViewById(R.id.textObservacionesSalida);
        botonSalida = (Button) findViewById(R.id.buttonSalida);

        almacen_spinner = (Spinner) findViewById(R.id.spinner_almacen);


        final ArrayList<String> almacenes = almacen.getArrayListAlmacenes();
        final ArrayList<String> ids = almacen.getArrayListId();

        String[] spinnerArray = new String[ids.size()];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            spinnerMap.put(almacenes.get(i), ids.get(i));
            spinnerArray[i] = almacenes.get(i);
        }

        final ArrayAdapter<String> a = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        almacen_spinner.setAdapter(a);

        if (x != null){
            observacion_salida.setText(x);
        }
        if (y != null){
            referenica_salida.setText(y);
        }
        if(z != "" || z != null){
            concepto.setText(z);
        }
        if(w != null){
            almacen_spinner.setSelection(Integer.valueOf(w));
            System.out.println("valor:. "+w);
        }

        if (almacen_spinner != null) {
            almacen_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    nombrealmacen = almacen_spinner.getSelectedItem().toString();
                    idAlmacen = spinnerMap.get(nombrealmacen);
                    posicion_almacen = position;
                    System.out.println("almacenes: " + idAlmacen + nombrealmacen);


                    materiales = (ListView) findViewById(R.id.listView_lista_materiales);
                    lista = new ListaMaterialAdaptador(getApplicationContext(), MaterialesAlmacen.getMateriales(getApplicationContext(), idAlmacen));
                    materiales.setAdapter(lista);




                    materiales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MaterialesAlmacen m = lista.getItem(position);

                            idMaterial = m.id_material;
                            cantidad = Double.valueOf(m.cantidad);
                            if(cantidad != 0) {
                                System.out.println(idMaterial + "Click orden: " + m.id_material + "posicion" + position);
                               // idMaterial = String.valueOf(orden.idmaterial);
                              //  showEditDialog(idMaterial);
                                FragmentManager fm = getSupportFragmentManager();

                                salidaFragment = new SalidaFragment();
                                salidaFragment = salidaFragment.newInstance(String.valueOf(cantidad),m.unidad , idMaterial, m.descripcion, m.id_almacen,concepto.getText().toString(), referenica_salida.getText().toString(),observacion_salida.getText().toString(), posicion_almacen);

                                salidaFragment.show(fm, "Material Recibido");


                            }else{
                                Toast.makeText(getApplicationContext(), "NO HAY MÁS MATERIAL PARA RECIBIR.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(dialogoRecepcion.getCount() != 0) {
                mListRecibido = (ListView) findViewById(R.id.listView_salida_materiales);
                listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), "null", String.valueOf(idMaterial)));
                mListRecibido.setAdapter(listaRecibido);

                mListRecibido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        DialogoRecepcion dialogR = listaRecibido.getItem(position);

                        final Integer idDialogo = dialogR.id;
                        AlertDialog.Builder alert = new AlertDialog.Builder(SalidaActivity.this);
                        alert.setTitle("Salida de Almacén");
                        alert.setMessage("¿Estas seguro de eliminar esta salida del material?");
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Double cantx = Double.parseDouble(DialogoRecepcion.getCantidadRS(getApplicationContext(), idDialogo) + "");
                                listaRecibido.remove((DialogoRecepcion) listaRecibido.getItem(position));

                                mListRecibido.setAdapter(listaRecibido);
                                System.out.println("aq: " + cantx);
                                DialogoRecepcion.remove(getApplicationContext(), idDialogo);

                                lista = new ListaMaterialAdaptador(getApplicationContext(), MaterialesAlmacen.getMateriales(getApplicationContext(), idAlmacen));
                                materiales.setAdapter(lista);
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                mListRecibido = (ListView) findViewById(R.id.listView_salida_materiales);
                                listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), "null", String.valueOf(idMaterial)));
                                mListRecibido.setAdapter(listaRecibido);
                            }
                        });
                        alert.show();
                        System.out.println(dialogR.contratista + "Adaptador Dialog: posicion" + position);
                    }
                });
            }
        }


        botonSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salida = new Salida(getApplicationContext());
                salidaDetalle = new SalidaDetalle(getApplicationContext());

                System.out.println("add5: " + idMaterial +cantidad);

                ContentValues salidas= new ContentValues();
                Integer a = 0;
                String aux_material;
                String aux_almacen;
                for (int z =0; z < lista.getCount(); z++){
                    a=z;
                    MaterialesAlmacen ord= lista.getItem(z);
                    aux_almacen = String.valueOf(ord.id_almacen);
                    aux_material = String.valueOf(ord.id_material);
                    //System.out.println("EXPYTY: "+listaRecibido.getCount());
                    if(almacen_spinner == null){
                        Toast.makeText(getApplicationContext(), R.string.error_orden, Toast.LENGTH_SHORT).show();
                    }
                    else if(referenica_salida.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.error_referencia, Toast.LENGTH_SHORT).show();
                    }
                    else if(concepto.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.error_concepto, Toast.LENGTH_SHORT).show();
                    }
                    else if(observacion_salida.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.error_OB, Toast.LENGTH_SHORT).show();
                    }
                    else if(listaRecibido == null){
                        Toast.makeText(getApplicationContext(), R.string.error_recibir, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        salidas.clear();
                        salidas.put("referencia", referenica_salida.getText().toString());
                        salidas.put("observacion", observacion_salida.getText().toString());
                        salidas.put("idalmacen", ord.id_almacen);
                        salidas.put("fecha", Util.timeStamp());
                        salidas.put("concepto",concepto.getText().toString());


                        Integer e = salida.create(salidas);
                        System.out.println("salidas: "+e+" "+salidas);

                        for(int l = 0; l<listaRecibido.getCount(); l++){
                            DialogoRecepcion dr = listaRecibido.getItem(l);
                            System.out.println("ffff: "+aux_almacen+" "+aux_material+" "+dr.id_almacen+" "+ord.id_material);
                            if(aux_almacen.equals(dr.id_almacen) && aux_material.equals(dr.idmaterial) ){

                                salidas.clear();
                                salidas.put("cantidad",dr.cantidadRS);
                                salidas.put("idsalida",e);
                                salidas.put("claveConcepto",dr.claveConcepto);
                                salidas.put("idcontratista",dr.idcontratista);
                                salidas.put("cargo",dr.cargo);
                                salidas.put("unidad",dr.unidad);
                                salidas.put("idmaterial",dr.idmaterial);

                                System.out.println("entradaDetalle: "+salidas);

                                if(!salidaDetalle.create(salidas)){
                                    Toast.makeText(getApplicationContext(), R.string.error_entradadetalle, Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                        System.out.println("FUNCION: "+e);
                        if(!salidaDetalle.find(e)){
                            Salida.remove(getApplicationContext(), e);
                            System.out.println("remove: "+e);
                        }

                    }

                }
                a++;
                if(a==lista.getCount()) {
                    Toast.makeText(getApplicationContext(), R.string.guardado, Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    dialogoRecepcion.destroy();
                    startActivity(main);
                }



            }
        });


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if(drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(drawer != null)
            drawer.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < drawer.getChildCount(); i++) {
                        View child = drawer.getChildAt(i);
                        TextView tvp = (TextView) child.findViewById(R.id.textViewProyecto);
                        TextView tvu = (TextView) child.findViewById(R.id.textViewUser);
                        TextView tvv = (TextView) child.findViewById(R.id.textViewVersion);

                        if (tvp != null) {
                            tvp.setText(usuario.getObraActiva());
                        }
                        if (tvu != null) {
                            tvu.setText(usuario.getNombre());
                        }
                        if (tvv != null) {
                            tvv.setText("Versión " + String.valueOf(BuildConfig.VERSION_NAME));
                        }
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {
            Intent inicio = new Intent(this, MainActivity.class);
            startActivity(inicio);
        } else if (id == R.id.nav_imprimir) {

        } else if (id == R.id.nav_entrada) {

            Intent entrada = new Intent(getApplicationContext(), EntradaActivity.class);
            startActivity(entrada);

        } else if (id == R.id.nav_salida) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_trans) {

        } else if (id == R.id.nav_syn) {

        } else if (id == R.id.nav_cambio) {
            Intent seleccionar = new Intent(this, SeleccionaObraActivity.class);
            startActivity(seleccionar);

        } else if (id == R.id.nav_cerrar) {
            usuario.destroy();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

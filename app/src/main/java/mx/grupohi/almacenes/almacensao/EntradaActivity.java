package mx.grupohi.almacenes.almacensao;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
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

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.data;

public class EntradaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Usuario usuario;
    OrdenCompra ordenCompra;
    Entrada entrada;
    EntradaDetalle entradaDetalle;
    DialogoRecepcion dialogoRecepcion;


    Spinner spinner;
    private HashMap<String, String> spinnerMap;
    String idOrden;
    String nombre;
    String idMaterial;
    Integer idfolio;
    Button guardar;
    ListView mList;
    lista_adaptador lista;
    ListView mListRecibido;
    ListaDialog listaRecibido;
    EditText referencia;
    EditText observaciones;
    Double existencia;
    Integer idOrdenCompra;

    CantidadEntradaFragment editNameDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String x = getIntent().getStringExtra("observacion");
        String y = getIntent().getStringExtra("referencia");
        System.out.println("extra: "+ x + y);
        usuario = new Usuario(getApplicationContext());
        usuario = usuario.getUsuario();
        dialogoRecepcion = new DialogoRecepcion(getApplicationContext());
        ordenCompra = new OrdenCompra(getApplicationContext());
        referencia = (EditText) findViewById(R.id.textReferencia);
        observaciones = (EditText) findViewById(R.id.textObservaciones);
        spinner = (Spinner) findViewById(R.id.spinner_ordencompra);
        guardar = (Button) findViewById(R.id.buttonGuardar);

        if (x != null){
            observaciones.setText(x);
        }
        if (y != null){
            referencia.setText(y);
        }

        final ArrayList<String> nombres = ordenCompra.getArrayListOrdenes();
        final ArrayList<String> ids = ordenCompra.getArrayListId();

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

                    nombre = spinner.getSelectedItem().toString();
                    idOrden = spinnerMap.get(nombre);

                    System.out.println("add: " + idOrden + nombre);
                    mList = (ListView) findViewById(R.id.listView_materiales_ordencompra);
                    System.out.println("valores: " + idOrden);

                    lista = new lista_adaptador(getApplicationContext(), OrdenCompra.getOrden(getApplicationContext(), idOrden));
                    mList.setAdapter(lista);




                    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            OrdenCompra orden = lista.getItem(position);

                            idOrdenCompra = orden.id;
                            existencia = Double.valueOf(orden.existencia);
                            if(existencia != 0) {
                                System.out.println(idOrdenCompra + "Click orden: " + orden.idorden + "posicion" + position);
                                idMaterial = String.valueOf(orden.idmaterial);
                                showEditDialog(idOrdenCompra);



                                System.out.println("idcompra: " + idOrden);
                            }else{
                                Toast.makeText(getApplicationContext(), "NO HAY MÁS MATERIAL PARA RECIBIR.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    if(dialogoRecepcion.getCount() != 0) {
                        mListRecibido = (ListView) findViewById(R.id.listView_materiales_ordencompra_temp);
                        listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), idOrden));
                        mListRecibido.setAdapter(listaRecibido);

                        mListRecibido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                DialogoRecepcion dialogR = listaRecibido.getItem(position);

                                final Integer idDialogo = dialogR.id;
                                AlertDialog.Builder alert = new AlertDialog.Builder(EntradaActivity.this);
                                alert.setTitle("Entrada a Almacén");
                                alert.setMessage("¿Estas seguro de eliminar esta entrada del material?");
                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Double cantx = Double.parseDouble(DialogoRecepcion.getCantidadRS(getApplicationContext(), idDialogo) + "");
                                        listaRecibido.remove((DialogoRecepcion) listaRecibido.getItem(position));

                                        mListRecibido.setAdapter(listaRecibido);
                                        System.out.println("aq: " + cantx);
                                        DialogoRecepcion.remove(getApplicationContext(), idDialogo);

                                        lista = new lista_adaptador(getApplicationContext(), OrdenCompra.getOrden(getApplicationContext(), idOrden));
                                        mList.setAdapter(lista);


                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                        mListRecibido = (ListView) findViewById(R.id.listView_materiales_ordencompra_temp);
                                        listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), idOrden));
                                        mListRecibido.setAdapter(listaRecibido);
                                    }
                                });
                                alert.show();
                                System.out.println(dialogR.contratista + "Adaptador Dialog: posicion" + position);
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });

        }


        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrada = new Entrada(getApplicationContext());
                entradaDetalle = new EntradaDetalle(getApplicationContext());

                System.out.println("add5: " + idOrden +nombre);

                ContentValues entradas= new ContentValues();
                Integer a = 0;
                String aux_orden;
                String aux_material;
                for (int z =0; z < lista.getCount(); z++){
                    a=z;
                    OrdenCompra ord= lista.getItem(z);
                    aux_orden = String.valueOf(ord.numerofolio);
                    aux_material = String.valueOf(ord.idmaterial);
                    //System.out.println("EXPYTY: "+listaRecibido.getCount());
                    if(spinner == null){
                        Toast.makeText(getApplicationContext(), R.string.error_orden, Toast.LENGTH_SHORT).show();
                    }
                    else if(referencia.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.error_referencia, Toast.LENGTH_SHORT).show();
                    }
                    else if(observaciones.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.error_OB, Toast.LENGTH_SHORT).show();
                    }
                    else if(listaRecibido == null){
                        Toast.makeText(getApplicationContext(), R.string.error_recibir, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        entradas.clear();
                        entradas.put("referencia", referencia.getText().toString());
                        entradas.put("observacion", observaciones.getText().toString());
                        entradas.put("idorden", ord.idorden);
                        entradas.put("fecha", Util.timeStamp());
                        entradas.put("idmaterial",ord.idmaterial);

                        Integer e = entrada.create(entradas);
                        System.out.println("entrada: "+e+" "+entradas);

                        for(int l = 0; l<listaRecibido.getCount(); l++){
                            DialogoRecepcion dr = listaRecibido.getItem(l);
                            System.out.println("ffff: "+aux_orden+" "+aux_material+" "+dr.idorden+" "+ord.idmaterial);
                            if( aux_material.equals(dr.idmaterial) && aux_orden.equals(dr.idorden) ){

                                entradas.clear();
                                entradas.put("idalmacen",dr.id_almacen);
                                entradas.put("cantidad",dr.cantidadRS);
                                entradas.put("identrada",e);
                                entradas.put("claveConcepto",dr.claveConcepto);
                                entradas.put("idcontratista",dr.idcontratista);
                                entradas.put("cargo",dr.cargo);
                                entradas.put("unidad",dr.unidad);
                                entradas.put("idmaterial",dr.idmaterial);
                                System.out.println("entradaDetalle: "+entradas);

                                if(!entradaDetalle.create(entradas)){
                                    Toast.makeText(getApplicationContext(), R.string.error_entradadetalle, Toast.LENGTH_SHORT).show();
                                }

                            }

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

    private boolean showEditDialog(Integer idOrdenCompra) {


        FragmentManager fm = getSupportFragmentManager();

        editNameDialogFragment = new CantidadEntradaFragment();
        editNameDialogFragment = editNameDialogFragment.newInstance(ordenCompra.getDescripcion(idOrdenCompra), String.valueOf(existencia), ordenCompra.getUnidad(idOrdenCompra), idOrden, idMaterial, referencia.getText().toString(),observaciones.getText().toString());

        editNameDialogFragment.show(fm, "Material Recibido");


        return true;


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        DialogoRecepcion d = new DialogoRecepcion(getApplicationContext());
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        d.destroy();
        startActivity(main);
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

            Intent intent = getIntent();
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_salida) {
            Intent salida = new Intent(getApplicationContext(), SalidaActivity.class);
            startActivity(salida);

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

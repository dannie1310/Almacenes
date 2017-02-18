package mx.grupohi.almacenes.almacensao;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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

import com.bixolon.printer.BixolonPrinter;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class EntradaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static Usuario usuario;
    OrdenCompra ordenCompra;
    Entrada entrada;
    EntradaDetalle entradaDetalle;
    DialogoRecepcion dialogoRecepcion;
    Intent main;

    Spinner spinner;
    private HashMap<String, String> spinnerMap;
    String idOrden;
    String nombre;
    String idMaterial;
    Integer idfolio;
    Button guardar;
    Button buttonImprimir;
    Button salir;
    ListView mList;
    lista_adaptador lista;
    ListView mListRecibido;
    ListaDialog listaRecibido;
    EditText referencia;
    EditText observaciones;
    Double existencia;
    Integer idOrdenCompra;
    private String folio;

    public static BixolonPrinter bixolonPrinterApi;
    private static final long PRINTING_TIME = 2100;
    private static final long PRINTING_SLEEP_TIME = 300;
    static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;
    private final int LINE_CHARS = 64;
    private boolean imprimir;
    private static Boolean connectedPrinter = false;
    private Toolbar toolbar;
    private String mConnectedDeviceName = null;
    static String espacio = "   ";


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
        main = new Intent(getApplicationContext(), MainActivity.class);
        usuario = new Usuario(getApplicationContext());
        usuario = usuario.getUsuario();
        dialogoRecepcion = new DialogoRecepcion(getApplicationContext());
        ordenCompra = new OrdenCompra(getApplicationContext());
        referencia = (EditText) findViewById(R.id.textReferencia);
        observaciones = (EditText) findViewById(R.id.textObservaciones);
        spinner = (Spinner) findViewById(R.id.spinner_ordencompra);
        guardar = (Button) findViewById(R.id.buttonGuardar);
        buttonImprimir = (Button) findViewById(R.id.ImprimirEntrada);
        salir = (Button) findViewById(R.id.salir);
        buttonImprimir.setVisibility(View.GONE);
        salir.setVisibility(View.GONE);

        bixolonPrinterApi = new BixolonPrinter(this, mHandler, null);

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
                        listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), idOrden, "null"));
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
                                        listaRecibido = new ListaDialog(getApplicationContext(), DialogoRecepcion.getRecepcion(getApplicationContext(), idOrden, "null"));
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

                System.out.println("add5: " + idOrden + nombre);

                ContentValues entradas = new ContentValues();
                Integer a = 0;
                String aux_orden;
                String aux_material;
                folio = Util.folio();
                if (idOrden.equals("0")) {
                    Toast.makeText(getApplicationContext(), R.string.error_orden, Toast.LENGTH_SHORT).show();
                } else if (referencia.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_referencia, Toast.LENGTH_SHORT).show();
                } else if (listaRecibido == null) {
                    Toast.makeText(getApplicationContext(), R.string.error_recibir, Toast.LENGTH_SHORT).show();
                } else if (observaciones.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_OB, Toast.LENGTH_SHORT).show();
                } else {

                    for (int z = 0; z < lista.getCount(); z++) {
                        a = z;
                        OrdenCompra ord = lista.getItem(z);
                        aux_orden = String.valueOf(ord.numerofolio);
                        aux_material = String.valueOf(ord.idmaterial);
                        //System.out.println("EXPYTY: "+listaRecibido.getCount());

                        entradas.clear();
                        entradas.put("referencia", referencia.getText().toString());
                        entradas.put("observacion", observaciones.getText().toString());
                        entradas.put("idorden", ord.idorden);
                        entradas.put("fecha", Util.timeStamp());
                        entradas.put("idmaterial", ord.idmaterial);
                        entradas.put("idobra", usuario.getIdObra());
                        entradas.put("folio", folio);

                        Integer e = entrada.create(entradas);
                        System.out.println("entrada: " + e + " " + entradas);

                        for (int l = 0; l < listaRecibido.getCount(); l++) {
                            DialogoRecepcion dr = listaRecibido.getItem(l);
                            System.out.println("ffff: " + aux_orden + " " + aux_material + " " + dr.idorden + " " + ord.idmaterial);
                            if (aux_material.equals(dr.idmaterial) && aux_orden.equals(dr.idorden)) {

                                entradas.clear();
                                entradas.put("idalmacen", dr.id_almacen);
                                entradas.put("cantidad", dr.cantidadRS);
                                entradas.put("identrada", e);
                                entradas.put("claveConcepto", dr.claveConcepto);
                                entradas.put("idcontratista", dr.idcontratista);
                                entradas.put("cargo", dr.cargo);
                                entradas.put("unidad", dr.unidad);
                                entradas.put("idmaterial", dr.idmaterial);
                                System.out.println("entradaDetalle: " + entradas);

                                if (!entradaDetalle.create(entradas)) {
                                    Toast.makeText(getApplicationContext(), R.string.error_entradadetalle, Toast.LENGTH_SHORT).show();
                                }

                            }

                        }

                        System.out.println("FUNCION: " + e);
                        if (!entradaDetalle.find(e)) {
                            Entrada.remove(getApplicationContext(), e);
                            System.out.println("remove: " + e);
                        }

                    }

                    a++;
                    if (a == lista.getCount()) {

                        Toast.makeText(getApplicationContext(), R.string.guardado, Toast.LENGTH_SHORT).show();
                        guardar.setEnabled(false);
                        guardar.setVisibility(View.GONE);
                        buttonImprimir.setVisibility(View.VISIBLE);
                        salir.setVisibility(View.VISIBLE);
                        mList.setEnabled(false);
                        mListRecibido.setEnabled(false);

                        //buttonImprimir.performClick();
                    }
                }
            }
        });


        buttonImprimir.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  if (entrada.folio(folio)) {
                                                      buttonImprimir.setEnabled(false);
                                                      imprimir = true;
                                                      if (!connectedPrinter) {
                                                          bixolonPrinterApi.findBluetoothPrinters();
                                                      }

                                                      new Handler().postDelayed(new Thread() {
                                                          @Override
                                                          public void run() {
                                                              super.run();
                                                              buttonImprimir.setEnabled(true);

                                                          }
                                                      }, PRINTING_TIME);

                                                      Thread t = new Thread() {
                                                          public void run() {
                                                              try {

                                                                  bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);

                                                                  // Thread.sleep(PRINTING_SLEEP_TIME);
                                                                  BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_logo);
                                                                  Bitmap bitmap = drawable.getBitmap();
                                                                  //bixolonPrinterApi.printBitmap(bitmap, BixolonPrinter.ALIGNMENT_CENTER, 220, 50, true);
                                                                 // bixolonPrinterApi.lineFeed(1, true);
                                                                  printheadproyecto(espacio+"COMPROBANTE RECEPCIÓN DE MATERIALES.",usuario.getObraActiva());
                                                                  System.out.println("IMPRIMIENDO");


                                                                  bixolonPrinterApi.lineFeed(1, true);
                                                                  //bixolonPrinterApi.printText("Folio: "+folio+" \n", BixolonPrinter.ALIGNMENT_RIGHT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                  printTextTwoColumns(espacio+"Folio: ", folio + " \n",0);
                                                                  printTextTwoColumns(espacio+"Orden Compra: ",idOrden + " \n",0);
                                                                  printTextTwoColumns(espacio+"Referencia: ", referencia.getText() + " \n",0);


                                                                  //printTextTwoColumns(espacio+"Observaciones: ", observaciones.getText() + "\n",0);
                                                                  printTextTwoColumns(espacio+"Checador: " + usuario.getNombre(), Util.formatoFecha() + "\n",0);
                                                                  JSONObject edetalle = entradaDetalle.getEntradasDetalle(getApplicationContext(), folio);
                                                                  System.out.println("JSON "+ edetalle);
                                                                  bixolonPrinterApi.lineFeed(1, true);
                                                                  bixolonPrinterApi.printText(espacio+"=============================================   ", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                                                                  bixolonPrinterApi.printText(espacio+"RELACIÓN DE MATERIALES RECIBIDOS. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                                                                  bixolonPrinterApi.printText(espacio+"============================================= \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                                                                  //bixolonPrinterApi.lineFeed(1, true);
                                                                  JSONObject aux;
                                                                  for (int i = 0; i < edetalle.length(); i++) {
                                                                      System.out.println("obras: " + edetalle.getJSONObject(String.valueOf(i)));
                                                                      JSONObject info = edetalle.getJSONObject(String.valueOf(i));
                                                                      if(i!=0) {
                                                                          aux = edetalle.getJSONObject(String.valueOf(i - 1));

                                                                          if (aux.getString("idalmacen") != info.getString("idalmacen")) {
                                                                              bixolonPrinterApi.printText(espacio+"_____________________________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                              bixolonPrinterApi.printText(espacio+"Almacén: " + info.getString("almacen") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                          }
                                                                      }
                                                                      else if (info.getString("almacen") != "null"){
                                                                          bixolonPrinterApi.printText(espacio+"Almacén: " + info.getString("almacen") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                      }

                                                                      if(info.getString("almacen") == "null"){
                                                                         /* if(i!=0){
                                                                              bixolonPrinterApi.lineFeed(1, true);
                                                                              //bixolonPrinterApi.printText("_____________________________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                          }*/
                                                                          bixolonPrinterApi.printText(espacio+"Clave Concepto: " + info.getString("clave") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                                                                      }
                                                                      //bixolonPrinterApi.printText(info.getString("material").toUpperCase()+" \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                      printTextTwoColumns(espacio+info.getString("material").toUpperCase(), info.getString("cantidad")+ " "+info.getString("unidad")+ "\n",1);

                                                                      if(info.getString("contratista") != "null"){

                                                                          bixolonPrinterApi.printText(espacio+"Contratista: " + info.getString("contratista")+"\n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                                                                          if(info.getString("cargo").equals("1")){
                                                                              bixolonPrinterApi.printText(espacio+"(CON CARGO)", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                                                          }
                                                                      }

                                                                      bixolonPrinterApi.lineFeed(1, true);
                                                                  }
                                                                  printTextTwoColumns(espacio+"Observaciones: ", observaciones.getText() + "\n",0);

                                                                  printfoot();

                                                                  // }
                                                                  //bixolonPrinterApi.lineFeed(1,true);
                                                                 //
                                                                  //bixolonPrinterApi.printQrCode("NUEVO", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.QR_CODE_MODEL2, 5, false);

                                                                  bixolonPrinterApi.lineFeed(2, true);

                                                              } catch (Exception e) {
                                                                  Toast.makeText(getApplicationContext(), R.string.error_impresion, Toast.LENGTH_LONG).show();
                                                              }

                                                          }
                                                      };
                                                      t.start();

                                                  }
                                              }
                                          });
        System.out.println("activo= "+buttonImprimir.isClickable());
        onPause();
        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);

        salir.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         dialogoRecepcion.destroy();
                                         startActivity(main);
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
        if (id == R.id.action_conectar) {
            imprimir = false;
            bixolonPrinterApi.findBluetoothPrinters();
        } else if (id == R.id.action_settings) {
            bixolonPrinterApi.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkEnabled();
    }

    @Override
    protected void onPause() {
        if (bixolonPrinterApi != null) {
            bixolonPrinterApi.disconnect();
        }

        super.onPause();
    }

    public static void printheadproyecto(String text,String usuario) {
        int alignment = BixolonPrinter.ALIGNMENT_CENTER;

        int attribute = 0;
        attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;

        int size = 0;
        bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);
        bixolonPrinterApi.printText(text, alignment, attribute, size, false);
        bixolonPrinterApi.lineFeed(1, true);
        bixolonPrinterApi.printText(usuario, alignment, attribute, size, false);
        bixolonPrinterApi.lineFeed(1, false);
        bixolonPrinterApi.printText(Util.formatoFecha(), BixolonPrinter.ALIGNMENT_RIGHT, attribute,  size, false);
        bixolonPrinterApi.cutPaper(true);
        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);
    }

    /**
     * Print the common two columns ticket style text. Label+Value.
     *
     * @param leftText
     * @param rightText
     */
    private void printTextTwoColumns(String leftText, String rightText, Integer valor) {
        if (leftText.length() + rightText.length() + 1 > LINE_CHARS) {
            int alignment = BixolonPrinter.ALIGNMENT_LEFT;
            int attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(leftText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);

            alignment = BixolonPrinter.ALIGNMENT_RIGHT;
            attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(rightText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        } else {
            int padding = LINE_CHARS - leftText.length() - rightText.length();
            String paddingChar = "";
            for (int i = 0; i < padding; i++) {
                if(valor == 0){
                    paddingChar = paddingChar.concat(" ");
                }else{
                    paddingChar = paddingChar.concat(".");
                }

            }

            int alignment = BixolonPrinter.ALIGNMENT_CENTER;
            int attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            bixolonPrinterApi.printText(leftText + paddingChar + rightText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        }
    }

    public static void printfoot() {
        int alignment = BixolonPrinter.ALIGNMENT_LEFT;
        int attribute = 1;
        attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_A;
        int size = 0;

        bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);
        // bixolonPrinterApi.printText(text, alignment, attribute, size, false);
       /* bixolonPrinterApi.lineFeed(1, false);
        bixolonPrinterApi.print1dBarcode(codex.toUpperCase(), BixolonPrinter.BAR_CODE_ITF, BixolonPrinter.ALIGNMENT_CENTER, 4, 200, BixolonPrinter.HRI_CHARACTER_NOT_PRINTED, true);
        // bixolonPrinterApi.formFeed(true);
        bixolonPrinterApi.printText(codex.toUpperCase(), BixolonPrinter.ALIGNMENT_CENTER, attribute, size, false);*/
        bixolonPrinterApi.lineFeed(3, true);
        bixolonPrinterApi.printText(espacio+usuario.getNombre()+"\n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_B, 0, false);
        bixolonPrinterApi.printText(espacio+"_______________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
        bixolonPrinterApi.printText(espacio+"Nombre y Firma del Encargado de Recepción. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
        bixolonPrinterApi.lineFeed(2, true);
        bixolonPrinterApi.printText(espacio+"_______________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
        bixolonPrinterApi.printText(espacio+"Nombre y Firma del Encargado de Entrega. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
        String cadena = "\n\nEste documento es un comprobante de recepción \n"+espacio+"de materiales del Sistema de Administración de \n"+espacio+"Obra, no representa un compromiso de pago hasta \n"+espacio+"su validación contra las remisiones del \n"+espacio+"proveedor y la revisión de factura.";
        bixolonPrinterApi.printText(espacio+cadena, BixolonPrinter.ALIGNMENT_CENTER, attribute, size, false);
        bixolonPrinterApi.lineFeed(1, false);
        bixolonPrinterApi.cutPaper(true);
        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);



    }

    private void checkEnabled() {
        Boolean enabled = Bluetooth.statusBluetooth();
        if (!enabled) {
            new android.app.AlertDialog.Builder(EntradaActivity.this)
                    .setTitle(getString(R.string.text_warning_blue_is_off))
                    .setMessage(getString(R.string.text_turn_on_bluetooth))
                    .setCancelable(true)
                    .setPositiveButton(
                            getString(R.string.text_update_settingsB),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                                }
                            })
                    .create()
                    .show();
        }
    }

    Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            Log.i("Handler: - ", msg.what + " " + msg.arg1 + " " + msg.arg2);

            switch (msg.what) {

                case BixolonPrinter.MESSAGE_STATE_CHANGE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_STATE_CHANGE");
                    switch (msg.arg1) {
                        case BixolonPrinter.STATE_CONNECTED:
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTED");

                           // Toast.makeText(getApplicationContext(), "Impresora Contectada " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            //toolbar.setSubtitle("Impresora Contectada " + mConnectedDeviceName);
                            connectedPrinter = true;
                            if(imprimir) {
                                buttonImprimir.performClick();
                            }
                            break;

                        case BixolonPrinter.STATE_CONNECTING:
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTING");
                            Toast.makeText(getApplicationContext(), R.string.title_connecting, Toast.LENGTH_SHORT).show();
                           // toolbar.setSubtitle(R.string.title_connecting);
                            EntradaActivity.connectedPrinter = false;

                            break;

                        case BixolonPrinter.STATE_NONE:
                            //toolbar.setSubtitle(R.string.title_not_connected);
                            Toast.makeText(getApplicationContext(),R.string.title_not_connected, Toast.LENGTH_SHORT).show();
                            Log.i("Handler", "BixolonPrinter.STATE_NONE");
                            connectedPrinter = false;

                            break;
                    }
                    break;

                case BixolonPrinter.MESSAGE_WRITE:
                    switch (msg.arg1) {
                        case BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_SINGLE_BYTE_FONT");
                            break;

                        case BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT:
                            Log.i("Handler", "BixolonPrinter.PROCESS_SET_DOUBLE_BYTE_FONT");
                            break;

                        case BixolonPrinter.PROCESS_DEFINE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_DEFINE_NV_IMAGE");
                            break;

                        case BixolonPrinter.PROCESS_REMOVE_NV_IMAGE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_REMOVE_NV_IMAGE");
                            break;

                        case BixolonPrinter.PROCESS_UPDATE_FIRMWARE:
                            Log.i("Handler", "BixolonPrinter.PROCESS_UPDATE_FIRMWARE");
                            break;
                    }
                    break;

                case BixolonPrinter.MESSAGE_READ:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_READ");
                    break;

                case BixolonPrinter.MESSAGE_DEVICE_NAME:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_DEVICE_NAME - " + msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME));
                    mConnectedDeviceName = msg.getData().getString(BixolonPrinter.KEY_STRING_DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Impresora Conectada como: " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case BixolonPrinter.MESSAGE_TOAST:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_TOAST - " + msg.getData().getString("toast"));
                    break;

                // The list of paired printers
                case BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_BLUETOOTH_DEVICE_SET");
                    if (msg.obj == null) {
                        Toast.makeText(getApplicationContext(), "No paired device",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Set<BluetoothDevice> pairedDevices = (Set<BluetoothDevice>) msg.obj;

                        DialogManager.showBluetoothDialog(EntradaActivity.this, bixolonPrinterApi, (Set<BluetoothDevice>) msg.obj);
                    }
                    break;

                case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_PRINT_COMPLETE");

                    Toast.makeText(getApplicationContext(),"Impresión Completa.", Toast.LENGTH_SHORT).show();
                    break;

                case BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_COMPLETE_PROCESS_BITMAP");

                    break;

                case MESSAGE_START_WORK:
                    Log.i("Handler", "MESSAGE_START_WORK");
                    Toast.makeText(getApplicationContext(),"Iniciando Impresión ", Toast.LENGTH_LONG).show();
                    break;

                case MESSAGE_END_WORK:
                    Log.i("Handler", "MESSAGE_END_WORK");
                    Toast.makeText(getApplicationContext(),"Finalizado ", Toast.LENGTH_LONG).show();
                    break;

                case BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET:
                    Log.i("Handler", "BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET");
                    if (msg.obj == null) {
                        Toast.makeText(getApplicationContext(), "No connectable device", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DialogoRecepcion d= new DialogoRecepcion(getApplicationContext());

        if (id == R.id.nav_inicio) {
            Intent inicio = new Intent(this, MainActivity.class);
            d.destroy();
            startActivity(inicio);

        } else if (id == R.id.nav_imprimir) {
            try {
                Util.copyDataBase(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_entrada) {

            Intent intent = getIntent();
            d.destroy();
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_salida) {
            Intent salida = new Intent(getApplicationContext(), SalidaActivity.class);
            d.destroy();
            startActivity(salida);

        } else if (id == R.id.nav_trans) {
            Intent t = new Intent(getApplicationContext(), TransferenciaActivity.class);
            startActivity(t);
        } else if (id == R.id.nav_syn) {

        } else if (id == R.id.nav_cambio) {
            Intent seleccionar = new Intent(this, SeleccionaObraActivity.class);
            d.destroy();
            startActivity(seleccionar);

        } else if (id == R.id.nav_cerrar) {
            usuario.destroy();
            d.destroy();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package mx.grupohi.almacenes.almacensao;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SalidaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static Usuario usuario;
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
    Button buttonImprimir;
    Button salir;

    String nombrealmacen;
    String idAlmacen;
    String folio;
    Integer idMaterial;
    Integer posicion_almacen;
    Double cantidad;
    ListView mListRecibido;
    ListaDialog listaRecibido;

    SalidaFragment salidaFragment;


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
        buttonImprimir = (Button) findViewById(R.id.ImprimirEntrada);
        salir = (Button) findViewById(R.id.salir);
        buttonImprimir.setVisibility(View.GONE);
        salir.setVisibility(View.GONE);

        almacen_spinner = (Spinner) findViewById(R.id.spinner_almacen);

        bixolonPrinterApi = new BixolonPrinter(this, mHandler, null);

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
        }

        if (almacen_spinner != null) {
            almacen_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    nombrealmacen = almacen_spinner.getSelectedItem().toString();
                    idAlmacen = spinnerMap.get(nombrealmacen);
                    posicion_almacen = position;


                    materiales = (ListView) findViewById(R.id.listView_lista_materiales);
                    lista = new ListaMaterialAdaptador(getApplicationContext(), MaterialesAlmacen.getMateriales(getApplicationContext(), idAlmacen, usuario.getIdObra()));
                    materiales.setAdapter(lista);




                    materiales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MaterialesAlmacen m = lista.getItem(position);

                            idMaterial = m.id_material;
                            cantidad = Double.valueOf(m.cantidad);
                            if(cantidad != 0) {
                               // System.out.println(idMaterial + "Click orden: " + m.id_material +": "+m.id_almacen +" posicion" + position);
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
                                DialogoRecepcion.remove(getApplicationContext(), idDialogo);

                                lista = new ListaMaterialAdaptador(getApplicationContext(), MaterialesAlmacen.getMateriales(getApplicationContext(), idAlmacen, usuario.getIdObra()));
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
                        //System.out.println(dialogR.contratista + "Adaptador Dialog: posicion" + position);
                    }
                });
            }
        }


        botonSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salida = new Salida(getApplicationContext());
                salidaDetalle = new SalidaDetalle(getApplicationContext());


                ContentValues salidas = new ContentValues();
                Integer a = 0;
                String aux_material;
                String aux_almacen;
                folio = Util.folio();
                //System.out.println("almacen:  "+idAlmacen.toString());
                if (concepto.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_concepto, Toast.LENGTH_SHORT).show();
                } else if (referenica_salida.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_referencia, Toast.LENGTH_SHORT).show();
                } else if (idAlmacen.equals("0")) {
                    Toast.makeText(getApplicationContext(), R.string.error_almacen, Toast.LENGTH_SHORT).show();
                } else if (listaRecibido == null) {
                    Toast.makeText(getApplicationContext(), R.string.error_recibir, Toast.LENGTH_SHORT).show();
                } else if (observacion_salida.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_OB, Toast.LENGTH_SHORT).show();
                } else {
                    for (int z = 0; z < lista.getCount(); z++) {
                        a = z;
                        MaterialesAlmacen ord = lista.getItem(z);
                        aux_almacen = String.valueOf(ord.id_almacen);
                        aux_material = String.valueOf(ord.id_material);


                        salidas.clear();
                        salidas.put("referencia", referenica_salida.getText().toString());
                        salidas.put("observacion", observacion_salida.getText().toString());
                        salidas.put("idalmacen", ord.id_almacen);
                        salidas.put("fecha", Util.timeStamp());
                        salidas.put("concepto", concepto.getText().toString());
                        salidas.put("idobra",usuario.getIdObra());
                        salidas.put("folio", folio);


                        Integer e = salida.create(salidas);
                        System.out.println("salidas: " + e + " " + salidas);

                        for (int l = 0; l < listaRecibido.getCount(); l++) {
                            DialogoRecepcion dr = listaRecibido.getItem(l);
                            System.out.println("ffff: " + aux_almacen + " " + aux_material + " " + dr.id_almacen + " " + ord.id_material);
                            if (aux_almacen.equals(dr.id_almacen) && aux_material.equals(dr.idmaterial)) {

                                salidas.clear();
                                salidas.put("cantidad", dr.cantidadRS);
                                salidas.put("idsalida", e);
                                salidas.put("claveConcepto", dr.claveConcepto);
                                salidas.put("idcontratista", dr.idcontratista);
                                salidas.put("cargo", dr.cargo);
                                salidas.put("unidad", dr.unidad);
                                salidas.put("idmaterial", dr.idmaterial);

                                System.out.println("entradaDetalle: " + salidas);

                                if (!salidaDetalle.create(salidas)) {
                                    Toast.makeText(getApplicationContext(), R.string.error_entradadetalle, Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                        System.out.println("FUNCION: " + e);
                        if (!salidaDetalle.find(e)) {
                            Salida.remove(getApplicationContext(), e);
                            System.out.println("remove: " + e);
                        }

                    }
                }
                a++;
                if (a == lista.getCount()) {
                    Toast.makeText(getApplicationContext(), R.string.guardado, Toast.LENGTH_SHORT).show();
                    botonSalida.setEnabled(false);
                    botonSalida.setVisibility(View.GONE);
                    buttonImprimir.setVisibility(View.VISIBLE);
                    salir.setVisibility(View.VISIBLE);
                    materiales.setEnabled(false);
                    mListRecibido.setEnabled(false);

                }
            }
        });


        buttonImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (salida.folio(folio)) {
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
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
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
            new android.app.AlertDialog.Builder(SalidaActivity.this)
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
                            SalidaActivity.connectedPrinter = false;

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

                        DialogManager.showBluetoothDialog(SalidaActivity.this, bixolonPrinterApi, (Set<BluetoothDevice>) msg.obj);
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
            //d.destroy();

            try {
                Util.copyDataBase(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_entrada) {

            Intent entrada = new Intent(getApplicationContext(), EntradaActivity.class);
            d.destroy();
            startActivity(entrada);

        } else if (id == R.id.nav_salida) {
            Intent intent = getIntent();
            d.destroy();
            finish();
            startActivity(intent);
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

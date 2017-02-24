package mx.grupohi.almacenes.almacensao;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bixolon.printer.BixolonPrinter;

import org.json.JSONObject;

import java.util.Set;

import static mx.grupohi.almacenes.almacensao.R.id.folio;
import static mx.grupohi.almacenes.almacensao.R.id.useLogo;
import static mx.grupohi.almacenes.almacensao.R.id.usuario;

public class ImpresionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static Usuario usuario;
    static Entrada entrada;
    static Salida salida;
    static  Transferencia transferencia;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    private static ViewPager mViewPager;

    private static AppBarLayout appBar;
    private static TabLayout pestanas;

    public static BixolonPrinter bixolonPrinterApi;
    private static final long PRINTING_TIME = 2100;
    private static final long PRINTING_SLEEP_TIME = 300;
    static final int MESSAGE_START_WORK = Integer.MAX_VALUE - 2;
    static final int MESSAGE_END_WORK = Integer.MAX_VALUE - 3;
    private static final int LINE_CHARS = 64;
    private static boolean imprimir;
    private static Boolean connectedPrinter = false;
    private Toolbar toolbar;
    private String mConnectedDeviceName = null;
    static String espacio = "   ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = new Usuario(getApplicationContext());
        usuario = usuario.getUsuario();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        pestanas = (TabLayout) findViewById(R.id.tabs);
        appBar = (AppBarLayout) findViewById(R.id.appbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        bixolonPrinterApi = new BixolonPrinter(this, mHandler, null);
        checkEnabled();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (drawer != null)
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
    protected void onResume() {
        super.onResume();
        checkEnabled();
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
            checkEnabled();
            bixolonPrinterApi.findBluetoothPrinters();
        } else if (id == R.id.action_settings) {
            bixolonPrinterApi.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {

            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);

        } else if (id == R.id.nav_imprimir) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_entrada) {

            Intent entrada = new Intent(getApplicationContext(), EntradaActivity.class);
            startActivity(entrada);

        } else if (id == R.id.nav_salida) {
            Intent salida = new Intent(getApplicationContext(), SalidaActivity.class);
            startActivity(salida);
        } else if (id == R.id.nav_trans) {
            Intent t = new Intent(getApplicationContext(), TransferenciaActivity.class);
            startActivity(t);
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

    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                View root = inflater.inflate(R.layout.fragment_imprimir_entrada, container, false);
                ListView mViajesList = (ListView) root.findViewById(R.id.list);
                final AdaptadorInicio adapter = new AdaptadorInicio(getActivity(), Entrada.getEntradas(getContext(),usuario.getIdObra()));
                mViajesList.setAdapter(adapter);
                mViajesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        entrada = adapter.getItem(position);
                        imprimirEntrada(getContext());
                    }
                });

                return root;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                View root = inflater.inflate(R.layout.fragment_imprimir_salida, container, false);
                ListView mViajesList = (ListView) root.findViewById(R.id.list);
                final AdaptadorSalidas adapter = new AdaptadorSalidas(getActivity(), Salida.getSalida(getContext(),usuario.getIdObra()));
                mViajesList.setAdapter(adapter);
                System.out.println("FRAME");
                mViajesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        salida = adapter.getItem(position);
                        imprimirSalida(getContext());
                    }
                });

                return root;
            } else {
                View root = inflater.inflate(R.layout.fragment_imprimir_transferencia, container, false);
                ListView mViajesList = (ListView) root.findViewById(R.id.list);
                final AdaptadorTransferencia adapter = new AdaptadorTransferencia(getActivity(), Transferencia.getTransferencia(getContext(),usuario.getIdObra()));
                mViajesList.setAdapter(adapter);
                System.out.println("FRAME");
                mViajesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        transferencia = adapter.getItem(position);
                        imprimirTrans(getContext());
                    }
                });
                return root;
            }

        }

    }

        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
         * one of the sections/tabs/pages.
         */
        public static class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }
            @Override

            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "ENTRADA";
                    case 1:
                        return "SALIDA";
                    case 2:
                        return "TRANSFERENCIA";
                }
                return null;
            }
        }
    @Override
    protected void onPause() {
        if (bixolonPrinterApi != null) {
            bixolonPrinterApi.disconnect();
        }

        super.onPause();
    }
    public static void imprimirEntrada(final Context context) {
        imprimir = true;
        if (!connectedPrinter) {
            bixolonPrinterApi.findBluetoothPrinters();
        }

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                super.run();

            }
        }, PRINTING_TIME);

        Thread t = new Thread() {
            public void run() {
                try {

                    bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);
                   // BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_logo);
                  //  Bitmap bitmap = drawable.getBitmap();
                    //bixolonPrinterApi.printBitmap(bitmap, BixolonPrinter.ALIGNMENT_CENTER, 220, 50, true);
                    // bixolonPrinterApi.lineFeed(1, true);
                    printheadproyecto(espacio + "COMPROBANTE RECEPCIÓN DE MATERIALES.", usuario.getObraActiva());
                    System.out.println("IMPRIMIENDO");
                    bixolonPrinterApi.lineFeed(1, true);
                    printTextTwoColumns(espacio + "Folio: ", entrada.folio + " \n", 0);
                    printTextTwoColumns(espacio + "Orden Compra: ", entrada.numerofolio + " \n", 0);
                    printTextTwoColumns(espacio + "Referencia: ", entrada.referencia + " \n", 0);


                    //printTextTwoColumns(espacio+"Observaciones: ", observaciones.getText() + "\n",0);
                    printTextTwoColumns(espacio + "Checador: " + usuario.getNombre(),entrada.fecha + "\n", 0);

                    EntradaDetalle entradaDetalle = new EntradaDetalle(context);
                    JSONObject edetalle = entradaDetalle.getEntradasDetalle(context, entrada.folio);
                    System.out.println("JSON " + edetalle);
                    bixolonPrinterApi.lineFeed(1, true);
                    bixolonPrinterApi.printText(espacio + "=============================================   ", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio + "RELACIÓN DE MATERIALES RECIBIDOS. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio + "============================================= \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    //bixolonPrinterApi.lineFeed(1, true);
                    JSONObject aux;
                    for (int i = 0; i < edetalle.length(); i++) {
                        System.out.println("obras: " + edetalle.getJSONObject(String.valueOf(i)));
                        JSONObject info = edetalle.getJSONObject(String.valueOf(i));
                        if (i != 0) {
                            aux = edetalle.getJSONObject(String.valueOf(i - 1));

                            if (aux.getString("idalmacen") != info.getString("idalmacen")) {
                                bixolonPrinterApi.printText(espacio + "_____________________________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                bixolonPrinterApi.printText(espacio + "Almacén: " + info.getString("almacen") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                        } else if (info.getString("almacen") != "null") {
                            bixolonPrinterApi.printText(espacio + "Almacén: " + info.getString("almacen") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }

                        if (info.getString("almacen") == "null") {
                            bixolonPrinterApi.printText(espacio + "Clave Concepto: " + info.getString("clave") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                        }
                        //bixolonPrinterApi.printText(info.getString("material").toUpperCase()+" \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        printTextTwoColumns(espacio + info.getString("material").toUpperCase(), info.getString("cantidad") + " " + info.getString("unidad") + "\n", 1);

                        if (info.getString("contratista") != "null") {

                            bixolonPrinterApi.printText(espacio + "Contratista: " + info.getString("contratista"), BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                            if (info.getString("cargo").equals("1")) {
                                bixolonPrinterApi.printText(espacio + "(CON CARGO)", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                            bixolonPrinterApi.printText("\n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }
                    }
                    bixolonPrinterApi.lineFeed(1, true);
                    printTextTwoColumns(espacio + "Observaciones: ", entrada.observacion + "\n", 0);
                    printfoot();
                    bixolonPrinterApi.lineFeed(2, true);

                } catch (Exception e) {
                    Toast.makeText(context, R.string.error_impresion, Toast.LENGTH_LONG).show();
                }

            }
        };
        t.start();

      //  onPause();
        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);
    }

    public static void imprimirSalida(final Context context){
        imprimir = true;
        if (!connectedPrinter) {
            bixolonPrinterApi.findBluetoothPrinters();
        }

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                super.run();

            }
        }, PRINTING_TIME);

        Thread t = new Thread() {
            public void run() {
                try {

                    bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);

                   /* BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_logo);
                    Bitmap bitmap = drawable.getBitmap();*/
                    printheadproyecto(espacio+"COMPROBANTE SALIDA DE MATERIALES.",usuario.getObraActiva());
                    System.out.println("IMPRIMIENDO");


                    bixolonPrinterApi.lineFeed(1, true);

                    printTextTwoColumns(espacio+"Folio: ", salida.folio + " \n",0);
                    printTextTwoColumns(espacio+"Almacén: ",salida.almacen + " \n",0);
                    printTextTwoColumns(espacio+"Referencia: ", salida.referencia + " \n",0);
                    printTextTwoColumns(espacio+"Concepto: ",salida.concepto+"\n",0);


                    printTextTwoColumns(espacio+"Checador: " + usuario.getNombre(), salida.fecha + "\n",0);
                    SalidaDetalle salidaDetalle = new SalidaDetalle(context);
                    JSONObject edetalle = salidaDetalle.getSalidaDetalle(context, salida.folio);
                    System.out.println("JSON "+ edetalle);
                    bixolonPrinterApi.lineFeed(1, true);
                    bixolonPrinterApi.printText(espacio+"=============================================   ", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio+"RELACIÓN DE SALIDA DE MATERIALES. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio+"============================================= \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);

                    JSONObject aux;
                    for (int i = 0; i < edetalle.length(); i++) {
                        System.out.println("obras: " + edetalle.getJSONObject(String.valueOf(i)));
                        JSONObject info = edetalle.getJSONObject(String.valueOf(i));
                        if(i!=0) {
                            aux = edetalle.getJSONObject(String.valueOf(i - 1));

                            if (!aux.getString("clave").equals(info.getString("clave"))) {
                                bixolonPrinterApi.printText(espacio+"_____________________________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                bixolonPrinterApi.printText(espacio+"Clave Concepto: " + info.getString("clave") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                        }
                        else{
                            bixolonPrinterApi.printText(espacio+"Clave Concepto: " + info.getString("clave") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }


                        printTextTwoColumns(espacio+info.getString("material").toUpperCase(), info.getString("cantidad")+ " "+info.getString("unidad")+ "\n",1);

                        if(info.getString("contratista") != "null"){

                            bixolonPrinterApi.printText(espacio+"Contratista: " + info.getString("contratista"), BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                            if(info.getString("cargo").equals("1")){
                                bixolonPrinterApi.printText(espacio+"(CON CARGO)", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                            bixolonPrinterApi.printText("\n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }

                    }
                    bixolonPrinterApi.lineFeed(1, true);
                    printTextTwoColumns(espacio+"Observaciones: ", salida.observacion + "\n",0);

                    printfoot();

                    bixolonPrinterApi.lineFeed(2, true);

                } catch (Exception e) {
                    Toast.makeText(context, R.string.error_impresion, Toast.LENGTH_LONG).show();
                }

            }
        };
        t.start();

        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);
    }

    public static void imprimirTrans(final Context context){
        imprimir = true;
        if (!connectedPrinter) {
            bixolonPrinterApi.findBluetoothPrinters();
        }

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                super.run();

            }
        }, PRINTING_TIME);

        Thread t = new Thread() {
            public void run() {
                try {

                    bixolonPrinterApi.setSingleByteFont(BixolonPrinter.CODE_PAGE_858_EURO);

                   /*BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_logo);
                    Bitmap bitmap = drawable.getBitmap();*/
                    printheadproyecto(espacio+"COMPROBANTE DE TRANSFERENCIA DE MATERIALES.",usuario.getObraActiva());
                    System.out.println("IMPRIMIENDO");


                    bixolonPrinterApi.lineFeed(1, true);

                    printTextTwoColumns(espacio+"Folio: ", transferencia.folio + " \n",0);
                    printTextTwoColumns(espacio+"Almacén Salida: ",transferencia.almacenOrigen + " \n",0);
                    printTextTwoColumns(espacio+"Referencia: ", transferencia.referencia + " \n",0);


                    printTextTwoColumns(espacio+"Checador: " + usuario.getNombre(),transferencia.fecha+ "\n",0);
                    TransferenciaDetalle transferenciaDetalle = new TransferenciaDetalle(context);
                    JSONObject edetalle = transferenciaDetalle.getTransferenciaDetalle(context, transferencia.folio);
                    System.out.println("JSON "+ edetalle);
                    bixolonPrinterApi.lineFeed(1, true);
                    bixolonPrinterApi.printText(espacio+"=============================================   ", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio+"RELACIÓN DE TRANSFERENCIA DE MATERIALES. \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);
                    bixolonPrinterApi.printText(espacio+"============================================= \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, 0, false);

                    JSONObject aux;
                    for (int i = 0; i < edetalle.length(); i++) {
                        System.out.println("obras: " + edetalle.getJSONObject(String.valueOf(i)));
                        JSONObject info = edetalle.getJSONObject(String.valueOf(i));
                        if(i!=0) {
                            aux = edetalle.getJSONObject(String.valueOf(i - 1));

                            if (!aux.getString("almacenDestino").equals(info.getString("almacenDestino"))) {
                                bixolonPrinterApi.printText(espacio+"_____________________________________________________________ \n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                                bixolonPrinterApi.printText(espacio+"Almacén Destino: " + info.getString("almacenDestino") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                        }
                        else{
                            bixolonPrinterApi.printText(espacio+"Almacén Destino: " + info.getString("almacenDestino") + " \n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }


                        printTextTwoColumns(espacio+info.getString("material").toUpperCase(), info.getString("cantidad")+ " "+info.getString("unidad")+ "\n",1);

                        if(info.getString("contratista") != "null"){

                            bixolonPrinterApi.printText(espacio+"Contratista: " + info.getString("contratista"), BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);

                            if(info.getString("cargo").equals("1")){
                                bixolonPrinterApi.printText(espacio+"(CON CARGO)", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                            }
                            bixolonPrinterApi.printText("\n", BixolonPrinter.ALIGNMENT_LEFT, BixolonPrinter.TEXT_ATTRIBUTE_FONT_C, 0, false);
                        }

                    }
                    bixolonPrinterApi.lineFeed(1, true);
                    printTextTwoColumns(espacio+"Observaciones: ",transferencia.observacion+ "\n",0);

                    printfoot();

                    bixolonPrinterApi.lineFeed(2, true);

                } catch (Exception e) {
                    Toast.makeText(context, R.string.error_impresion, Toast.LENGTH_LONG).show();
                }

            }
        };
        t.start();

        bixolonPrinterApi.kickOutDrawer(BixolonPrinter.DRAWER_CONNECTOR_PIN5);
    }



    private void checkEnabled() {
        Boolean enabled = Bluetooth.statusBluetooth();
        if (!enabled) {
            new android.app.AlertDialog.Builder(ImpresionActivity.this)
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
    private static void printTextTwoColumns(String leftText, String rightText, Integer valor) {
        if (leftText.length() + rightText.length() + 1 > LINE_CHARS) {
            int alignment = BixolonPrinter.ALIGNMENT_LEFT;
            int attribute = 0;
            int suma;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            suma = leftText.length() - LINE_CHARS ;
            suma = LINE_CHARS - (suma + rightText.length());
            bixolonPrinterApi.printText(leftText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
            System.out.println("mensaje: "+rightText+"IMM = "+ LINE_CHARS+" - "+leftText.length()+" - "+rightText.length()+"suma: "+suma);
            alignment = BixolonPrinter.ALIGNMENT_RIGHT;
            attribute = 0;
            attribute |= BixolonPrinter.TEXT_ATTRIBUTE_FONT_C;
            String paddingChar = "";

            for(int i=0; i<suma; i++){
                if(valor == 0){
                    paddingChar = paddingChar.concat(" ");
                }else{
                    paddingChar = paddingChar.concat(".");
                }
            }
            bixolonPrinterApi.printText(paddingChar+rightText, alignment, attribute, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, false);
        } else {
            int padding = LINE_CHARS - leftText.length() - rightText.length();
            System.out.println("mensaje: "+rightText+"IMMMM: "+padding +" = "+ LINE_CHARS+" - "+leftText.length()+" - "+rightText.length());
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
                            connectedPrinter = true;
                            if(imprimir) {
                               // buttonImprimir.performClick();
                                if(entrada != null) {
                                    imprimirEntrada(getApplicationContext());
                                }else if (salida != null){
                                    imprimirSalida(getApplicationContext());
                                }
                                else{
                                    imprimirTrans(getApplicationContext());
                                }
                            }
                            break;

                        case BixolonPrinter.STATE_CONNECTING:
                            Log.i("Handler", "BixolonPrinter.STATE_CONNECTING");
                            Toast.makeText(getApplicationContext(), R.string.title_connecting, Toast.LENGTH_SHORT).show();
                            // toolbar.setSubtitle(R.string.title_connecting);
                            ImpresionActivity.connectedPrinter = false;

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

                        DialogManager.showBluetoothDialog(ImpresionActivity.this, bixolonPrinterApi, (Set<BluetoothDevice>) msg.obj);
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
}


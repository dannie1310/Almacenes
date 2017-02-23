package mx.grupohi.almacenes.almacensao;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImpresionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Usuario usuario;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    private static ViewPager mViewPager;

    private static AppBarLayout appBar;
    private static TabLayout pestanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impresion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = new Usuario(getApplicationContext());
        usuario = usuario.getUsuario();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        pestanas = (TabLayout) findViewById(R.id.tabs);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
       /* mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               // Toast.makeText(ImpresionActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                if( position == 0){

                            ImprimirEntradaFragment fragment = (ImprimirEntradaFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                                fragment = ImprimirEntradaFragment.newInstance();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
        System.out.println("pantalla: " + mViewPager.getCurrentItem() + " o " + mSectionsPagerAdapter.getCount());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


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
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                View root = inflater.inflate(R.layout.fragment_imprimir_entrada, container, false);
                ListView mViajesList = (ListView) root.findViewById(R.id.list);
                final AdaptadorInicio adapter = new AdaptadorInicio(getActivity(), EntradaDetalle.getEntradas(getContext()));
                mViajesList.setAdapter(adapter);
                System.out.println("FRAME");
                mViajesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        EntradaDetalle viajeActual = adapter.getItem(position);
                        System.out.println("press: " + position);
               /* Intent intent = new Intent(con, SuccessDestinoActivity.class);
                intent.putExtra("idViaje", viajeActual.idViaje);
                intent.putExtra("list", 1);
                startActivity(intent);*/
                    }
                });

                return root;
            }else{
                View rootView = inflater.inflate(R.layout.fragment_imprimir_tab, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;
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
            private final List<Fragment> fragmentos = new ArrayList<>();
            private final List<String> titulosFragmentos = new ArrayList<>();
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
            public void addFragment(android.support.v4.app.Fragment fragment, String title) {
                fragmentos.add(fragment);
                titulosFragmentos.add(title);
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
}


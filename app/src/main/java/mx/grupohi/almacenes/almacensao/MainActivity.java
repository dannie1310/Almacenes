package mx.grupohi.almacenes.almacensao;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Usuario usuario;
    ImageButton entrada;
    ImageButton salida;
    ImageButton trans;
    ImageButton imprimir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        usuario = new Usuario(getApplicationContext());
        usuario = usuario.getUsuario();
        String x = usuario.getObraActiva();
        System.out.println("obra : "+x);
        try {
            Util.copyDataBase(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        entrada = (ImageButton) findViewById(R.id.button_entrada);
        salida = (ImageButton) findViewById(R.id.button_salida);
        trans = (ImageButton) findViewById(R.id.button_trans);
        imprimir = (ImageButton) findViewById(R.id.button_imprimir);

        entrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent entrada = new Intent(getApplicationContext(), EntradaActivity.class);
               // entrada.putExtra("cargo","id2");
                startActivity(entrada);
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salida = new Intent(getApplicationContext(), SalidaActivity.class);
                startActivity(salida);
            }
        });

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_imprimir) {

        } else if (id == R.id.nav_entrada) {

            Intent entrada = new Intent(getApplicationContext(), EntradaActivity.class);
            startActivity(entrada);

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

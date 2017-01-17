package mx.grupohi.almacenes.almacensao;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsuarioView;
    private TextInputLayout formLayout;
    private EditText mPasswordView;
    private ProgressDialog mProgressDialog;
    Intent mainActivity;
    Usuario user;
    Button mIniciarSesionButton;

    private DBScaSqlite db_sca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user = new Usuario(this);
        db_sca = new DBScaSqlite(getApplicationContext(), "sca", null, 1);
        if(user.get()) {
            nextActivity();
        }
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_login_activity);
        setContentView(R.layout.activity_login);
        checkPermissions();
        mUsuarioView = (AutoCompleteTextView) findViewById(R.id.usuario);
        mPasswordView = (EditText) findViewById(R.id.password);
        formLayout = (TextInputLayout) findViewById(R.id.layout);
        mIniciarSesionButton = (Button) findViewById(R.id.iniciar_sesion_button);


        mIniciarSesionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNetworkStatusAvialable(getApplicationContext())) {
                    attemptLogin();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();
                }
            }
        });

        mUsuarioView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                }
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mIniciarSesionButton.performClick();
                }
                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void nextActivity() {
        mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    protected void onStart() {

        super.onStart();
        if(user.get()) {
            nextActivity();
        }
    }
    private Boolean checkPermissions() {

        Boolean permission_read_phone_state = true;
        Boolean permission_read_external = true;
        Boolean permission_write_external = true;
        Boolean internet = true;

        /*if(ContextCompat.checkSelfPermission(LoginActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            permission_fine_location = false;
        }*/

        if(ContextCompat.checkSelfPermission(LoginActivity.this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            permission_read_phone_state =  false;
        }

        if(ContextCompat.checkSelfPermission(LoginActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            permission_read_external =  false;
            permission_write_external = false;
        }

       /* if(!Util.isGpsEnabled(getApplicationContext())) {
            gps.showSettingsAlert();
            gps = new GPSTracker(LoginActivity.this);
            _gps = false;
        }*/

        if(!Util.isNetworkStatusAvialable(getApplicationContext())) {
            Toast.makeText(LoginActivity.this, R.string.error_internet, Toast.LENGTH_LONG).show();
            internet = false;
        }
        return (permission_read_phone_state &&  internet && permission_read_external && permission_write_external);
    }

    private void attemptLogin() {

        // Resetear errores.
        mUsuarioView.setError(null);
        mPasswordView.setError(null);
        formLayout.setError(null);

        // Store values at the time of the login attempt.
        final String usuario = mUsuarioView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if(TextUtils.isEmpty(usuario)) {
            mUsuarioView.setError(getString(R.string.error_field_required));
            focusView = mUsuarioView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "Autenticando", "Por favor espere...", true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mAuthTask = new UserLoginTask(usuario, password);
                    mAuthTask.execute((Void) null);
                }
            }).run();
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String user;
        private final String pass;

        UserLoginTask(String email, String password) {
            user = email;
            pass = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            ContentValues data = new ContentValues();
            data.put("usr", user);
            data.put("pass", pass);
            data.put("controlador","Session");
            data.put("accion","iniciaSesionMovil");
            try {
                URL url = new URL(getApplicationContext().getString(R.string.url));
                System.out.println("url: "+ url + " "+ data);
                final JSONObject JSON = HttpConnection.POST(url, data);
                db_sca.deleteCatalogos();
                if(JSON.has("error")) {
                    Toast.makeText(getApplicationContext(),(String) JSON.get("error"), Toast.LENGTH_SHORT);
                    return false;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.setTitle("Actualizando");
                            mProgressDialog.setMessage("Actualizando datos de usuario...");
                        }
                    });
                    System.out.println("usuarios: "+ JSON);
                    Usuario usuario = new Usuario(getApplicationContext());

                    data.clear();
                    data.put("idusuario", JSON.getString("IdUsuario"));
                    data.put("nombre", JSON.getString("Descripcion"));
                    data.put("user", user);
                    data.put("pass", pass);
                    data.put("usuarioCADECO", JSON.getString("UsuarioCADECO"));

                    if (!usuario.create(data)) {
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.general_exception),Toast.LENGTH_SHORT);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}


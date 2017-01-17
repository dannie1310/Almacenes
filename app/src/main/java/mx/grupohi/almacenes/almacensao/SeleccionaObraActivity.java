package mx.grupohi.almacenes.almacensao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

public class SeleccionaObraActivity extends AppCompatActivity {

    private Spinner spinner;
    private HashMap<String, String> spinnerMap;
    private String idObra;
    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_obra);

        spinner = (Spinner) findViewById(R.id.spinner);

        user = new Usuario(getApplicationContext());

        final ArrayList<String> nombres = Obras.getArrayListNombres(getApplicationContext());
        final ArrayList <String> ids = Obras.getArrayListId(getApplicationContext());
        if(spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String placa = spinner.getSelectedItem().toString();
                    idObra = spinnerMap.get(nombres);
                    System.out.print("add: "+idObra);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        String[] spinnerArray = new String[ids.size()];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            spinnerMap.put(nombres.get(i), ids.get(i));
            spinnerArray[i] = nombres.get(i);
        }

        final ArrayAdapter<String> a = new ArrayAdapter<>(this,R.layout.text_layout, spinnerArray);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(a);



    }
}

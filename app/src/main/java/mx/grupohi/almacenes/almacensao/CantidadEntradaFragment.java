package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class CantidadEntradaFragment extends DialogFragment {


    // TODO: Rename and change types of parameters
    private static String tituloT;
    private static String cantidadT;
    private static String unidadT;
    String idAlmacen;
    String nombre;
    String empresa;
    String idContratista;
    Double cantidadRecibida;
    private Button guardar;
    private Button cancelar;
    private CheckBox almacen;
    private CheckBox contratista;
    private CheckBox cargo;
    Spinner spinner_almacenes;
    Spinner spinnercontratista;
    private HashMap<String, String>  spinnerMap;
    private HashMap<String, String>  spinnerMapContra;
    TextView titulo;
    TextView cantidad;
    TextView textCargo;
    EditText textclave;
    EditText recibido;
    LinearLayout spinneralmacen;
    LinearLayout vistacontratista;
    LinearLayout clave;

    Almacen catalmacenes;
    Contratista empresas;


    public CantidadEntradaFragment() {
        // Required empty public constructor
       System.out.println("cont "+nombre);



    }

    // TODO: Rename and change types and number of parameters
    public static CantidadEntradaFragment newInstance(String titulo, String cantidad, String unidad) {
        CantidadEntradaFragment frag = new CantidadEntradaFragment();
        Bundle args = new Bundle();
        args.putString("title", titulo);
        frag.setArguments(args);
        tituloT = titulo;
        cantidadT = cantidad;
        unidadT = unidad;

        return frag;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return inflater.inflate(R.layout.fragment_cantidad_entrada, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        catalmacenes = new Almacen(getContext());
        empresas = new Contratista(getContext());
        final ArrayList<String> nombres = catalmacenes.getArrayListAlmacenes();
        final ArrayList<String> ids = catalmacenes.getArrayListId();
        final ArrayList<String> contratistas = empresas.getArrayListContratistas();
        final ArrayList<String> idContra = empresas.getArrayListId();

        guardar = (Button) view.findViewById(R.id.buttonRecibir);
        cancelar = (Button) view.findViewById(R.id.buttoncancelar);
        titulo = (TextView) view.findViewById(R.id.textTitulo);
        cantidad = (TextView) view.findViewById(R.id.textcantidad);
        recibido = (EditText) view.findViewById(R.id.textrecibido);
        almacen = (CheckBox) view.findViewById(R.id.checkBox);
        contratista = (CheckBox) view.findViewById(R.id.checkContratista);
        cargo = (CheckBox) view.findViewById(R.id.cargo);
        spinneralmacen = (LinearLayout) view.findViewById(R.id.almacen);
        clave = (LinearLayout) view.findViewById(R.id.clave);
        spinner_almacenes = (Spinner) view.findViewById(R.id.spinner_almacen);
        spinnercontratista = (Spinner) view.findViewById(R.id.spinner_contratista);
        textclave = (EditText) view.findViewById(R.id.textclave);
        textCargo =(TextView) view.findViewById(R.id.textCargo);

        clave.setVisibility(View.GONE);
        titulo.setText(tituloT);
        cantidad.setText(cantidadT+" "+unidadT);
        recibido.setText(cantidadT);
        cargo.setVisibility(View.GONE);
        textCargo.setVisibility(View.GONE);
        spinnercontratista.setVisibility(View.GONE);

       // mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        String[] spinnerArray = new String[ids.size()];
        spinnerMap = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            spinnerMap.put(nombres.get(i), ids.get(i));
            spinnerArray[i] = nombres.get(i);
        }

        final ArrayAdapter<String> a = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerArray);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinner_almacenes.setAdapter(a);

        if (spinner_almacenes != null) {
            spinner_almacenes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    nombre = spinner_almacenes.getSelectedItem().toString();
                    idAlmacen = spinnerMap.get(nombre);
                    System.out.println("almacenes: " + idAlmacen + nombre);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        String[] array = new String[idContra.size()];
        spinnerMapContra = new HashMap<>();

        for (int i = 0; i < idContra.size(); i++) {
            spinnerMapContra.put(contratistas.get(i), idContra.get(i));
            array[i] = contratistas.get(i);
        }

        final ArrayAdapter<String> aux = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, array);
        a.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinnercontratista.setAdapter(aux);

        if (spinnercontratista != null) {
            spinnercontratista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    empresa = spinnercontratista.getSelectedItem().toString();
                    idContratista = spinnerMapContra.get(nombre);
                    System.out.println("almacenes: " + empresas + idContratista);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        almacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Almacen: " + recibido.getText());
                //spinneralmacen.setVisibility(LinearLayout.VISIBLE);
                if(almacen.isChecked()){
                    spinneralmacen.setVisibility(View.VISIBLE);
                    clave.setVisibility(View.GONE);

                }else{
                    spinneralmacen.setVisibility(View.GONE);
                    spinner_almacenes.getFirstVisiblePosition();
                    spinner_almacenes.setSelection(0);
                    clave.setVisibility(View.VISIBLE);
                }
            }

        });

        contratista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contratista.isChecked()){
                    cargo.setVisibility(View.VISIBLE);
                    textCargo.setVisibility(View.VISIBLE);
                    spinnercontratista.setVisibility(View.VISIBLE);

                }else{
                    cargo.setVisibility(View.GONE);
                    textCargo.setVisibility(View.GONE);
                    spinnercontratista.setVisibility(View.GONE);
                    spinner_almacenes.setSelection(0);
                }


            }

        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Recibir: " + recibido.getText()+textclave.getText());
                cantidadRecibida = Double.valueOf(recibido.getText().toString());
                onFinishEditDialog(String.valueOf(cantidadRecibida));

                getDialog().dismiss();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().cancel();

            }
        });

    }
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(getContext(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }




}

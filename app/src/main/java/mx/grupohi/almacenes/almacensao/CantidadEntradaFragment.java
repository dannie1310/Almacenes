package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CantidadEntradaFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private static String tituloT;
    private static String cantidadT;
    private static String unidadT;
    String idAlmacen;
    String nombre;
    private Button guardar;
    private CheckBox almacen;
    private CheckBox contratista;
    private CheckBox cargo;
    Spinner  spinner_almacenes;
    private HashMap<String, String>  spinnerMap;
    TextView titulo;
    TextView cantidad;
    EditText textclave;
    EditText recibido;
    LinearLayout spinneralmacen;
    LinearLayout vistacontratista;
    LinearLayout clave;

    Almacen catalmacenes;


    public CantidadEntradaFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_cantidad_entrada, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        catalmacenes = new Almacen(getContext());
        final ArrayList<String> nombres = catalmacenes.getArrayListAlmacenes();
        final ArrayList<String> ids = catalmacenes.getArrayListId();

        guardar = (Button) view.findViewById(R.id.buttonRecibir);
        titulo = (TextView) view.findViewById(R.id.textTitulo);
        cantidad = (TextView) view.findViewById(R.id.textcantidad);
        recibido = (EditText) view.findViewById(R.id.textrecibido);
        almacen = (CheckBox) view.findViewById(R.id.checkBox);
        contratista = (CheckBox) view.findViewById(R.id.checkContratista);
        cargo = (CheckBox) view.findViewById(R.id.cargo);
        spinneralmacen = (LinearLayout) view.findViewById(R.id.almacen);
        clave = (LinearLayout) view.findViewById(R.id.clave);
        spinner_almacenes = (Spinner) view.findViewById(R.id.spinner_almacen);
        textclave = (EditText) view.findViewById(R.id.textclave);

        clave.setVisibility(View.GONE);
        titulo.setText(tituloT);
        cantidad.setText(cantidadT+" "+unidadT);
        recibido.setText(cantidadT);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
       // mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

        almacen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Almacen: " + recibido.getText());
                //spinneralmacen.setVisibility(LinearLayout.VISIBLE);
                if(almacen.isChecked()){
                    spinneralmacen.setVisibility(View.VISIBLE);
                    clave.setVisibility(View.GONE);
                    spinner_almacenes.clearFocus();
                }else{
                    spinneralmacen.setVisibility(View.GONE);
                    clave.setVisibility(View.VISIBLE);
                }
            }

        });

        contratista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contratista.isChecked()){
                    spinneralmacen.setVisibility(View.VISIBLE);
                    clave.setVisibility(View.GONE);
                    spinner_almacenes.clearFocus();


                }else{
                    spinneralmacen.setVisibility(View.GONE);
                    clave.setVisibility(View.VISIBLE);
                }


            }

        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Recibir: " + recibido.getText()+textclave.getText());
            }
        });

    }

}

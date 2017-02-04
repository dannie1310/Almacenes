package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class SalidaFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



        // TODO: Rename and change types of parameters
        private static String tituloT;
        private static String cantidadT;
        private static String unidadT;
        static String idordenT;
        String idAlmacen;
        String nombre;
        String empresa;
        String idContratista;
        static String observacion;
        static String conceptoT;
        static String referencia;
        static Integer idmaterial;
        static Integer idalmacen;
        static Integer posicion_in;
        Double cantidadRecibida;
        private Button guardar;
        private Button cancelar;
        private CheckBox contratista;
        private CheckBox cargo;
        Spinner spinnercontratista;
        private HashMap<String, String>  spinnerMapContra;
        TextView titulo;
        TextView cantidad;
        TextView textCargo;
        TextView error;
        EditText textclave;
        EditText recibido;
        LinearLayout clave;

        ContentValues data;

        Contratista empresas;
        DialogoRecepcion dialogoCreate;

        Intent prueba;


        public SalidaFragment() {
            // Required empty public constructor
            System.out.println("cont "+nombre);



        }

        // TODO: Rename and change types and number of parameters
        public static SalidaFragment newInstance(String cantidad, String unidad, Integer material,String titulo, Integer almacen, String concepto, String ref, String obs, Integer posicion){
            SalidaFragment frag = new SalidaFragment();
            Bundle args = new Bundle();
            frag.isResumed();
            args.putString("title", "aasc");
            frag.setArguments(args);
            tituloT = titulo;
            cantidadT = cantidad;
            unidadT = unidad;
            idmaterial = material;
            idalmacen = almacen;
            conceptoT = concepto;
            referencia = ref;
            observacion = obs;
            posicion_in = posicion;

            return frag;


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            return inflater.inflate(R.layout.fragment_salida, container, false);
        }

        @Override
        public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Get field from view
            data = new ContentValues();
            empresas = new Contratista(getContext());
            dialogoCreate = new DialogoRecepcion(getContext());
            final ArrayList<String> contratistas = empresas.getArrayListContratistas();
            final ArrayList<String> idContra = empresas.getArrayListId();

            guardar = (Button) view.findViewById(R.id.buttonRecibirSalida);
            cancelar = (Button) view.findViewById(R.id.buttoncancelarSalida);
            titulo = (TextView) view.findViewById(R.id.textTituloSalida);
            cantidad = (TextView) view.findViewById(R.id.textcantidadSalida);
            error = (TextView) view.findViewById(R.id.textErrorSalida);
            recibido = (EditText) view.findViewById(R.id.textrecibidoSalida);
            contratista = (CheckBox) view.findViewById(R.id.checkContratistaSalida);
            cargo = (CheckBox) view.findViewById(R.id.cargoSalida);
            clave = (LinearLayout) view.findViewById(R.id.claveSalida);
            spinnercontratista = (Spinner) view.findViewById(R.id.spinner_contratistaSalida);
            textclave = (EditText) view.findViewById(R.id.textclaveSalida);
            textCargo =(TextView) view.findViewById(R.id.textCargoSalida);


            titulo.setText(tituloT);
            cantidad.setText(cantidadT+" "+unidadT);
            recibido.setText(cantidadT);
            cargo.setVisibility(View.GONE);
            textCargo.setVisibility(View.GONE);
            spinnercontratista.setVisibility(View.GONE);

            // mEditText.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



            String[] array = new String[idContra.size()];
            spinnerMapContra = new HashMap<>();

            for (int i = 0; i < idContra.size(); i++) {
                spinnerMapContra.put(contratistas.get(i), idContra.get(i));
                array[i] = contratistas.get(i);
            }

            final ArrayAdapter<String> aux = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, array);
            aux.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spinnercontratista.setAdapter(aux);

            if (spinnercontratista != null) {
                spinnercontratista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        empresa = spinnercontratista.getSelectedItem().toString();
                        idContratista = spinnerMapContra.get(empresa);
                        System.out.println("contratista: " + empresa + idContratista);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }


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
                    }


                }

            });

            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer idcargo=0;
                    System.out.println("clave: "+textclave.getText().toString());
                    if (recibido.getText().toString().isEmpty()){
                        error.setText("DEBE ESCRIBIR LA CANTIDAD RECIBIDA.");
                    }
                    else if(Double .valueOf(cantidadT) < Double .valueOf(recibido.getText().toString())){
                        error.setText("SOBRE PASA LA CANTIDAD DE LA ORDEN DE COMPRA.");
                    }
                    else if(contratista.isChecked() && Integer.valueOf(idContratista) == 0){
                        error.setText("DEBE SELECCIONAR UN CONTRATISTA.");
                    }
                    else if(textclave.getText().toString().isEmpty()){
                        error.setText("DEBE ESCRIBIR UNA CLAVE DE CONCEPTO.");
                    }
                    else {
                        error.setText("");
                        if(cargo.isChecked() == true){
                            idcargo = 1;
                        }
                        System.out.println("Recibir: " + recibido.getText() + textclave.getText());
                        cantidadRecibida = Double.valueOf(recibido.getText().toString());

                        data.put("idalmacen", idalmacen);
                        data.put("almacen", "null");
                        data.put("material", tituloT);
                        data.put("cantidadTotal", cantidadT);
                        data.put("cantidadRS", recibido.getText().toString());
                        data.put("unidad", unidadT);
                        data.put("claveConcepto", textclave.getText().toString());
                        data.put("idcontratista", idContratista);
                        data.put("contratista", empresa);
                        data.put("cargo", idcargo);
                        data.put("idorden", "null");
                        data.put("idmaterial", idmaterial);
                        System.out.println(idalmacen+"datos: "+ data);

                        if(!dialogoCreate.create(data)){
                            error.setText("ERROR INTENTE DE NUEVO.");
                        }else {
                            System.out.println("GUARDO");
                            Intent intent = new Intent(getContext(), SalidaActivity.class);
                            intent.putExtra("observacion",observacion);
                            intent.putExtra("referencia", referencia);
                            intent.putExtra("concepto", conceptoT);
                            intent.putExtra("posicion", String.valueOf(posicion_in));
                            startActivityForResult(intent,1);
                            startActivity(intent);
                            getDialog().dismiss();
                        }
                    }
                }
            });

            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getDialog().cancel();

                }
            });

        }


}

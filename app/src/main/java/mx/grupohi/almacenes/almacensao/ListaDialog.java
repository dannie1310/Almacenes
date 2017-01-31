package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Usuario on 31/01/2017.
 */

public class ListaDialog extends ArrayAdapter<DialogoRecepcion> {


    private Context context;

    ListaDialog(Context context, List<DialogoRecepcion> objects) {
        super(context, 0, objects);

        System.out.println("AQUI");
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.itemrecibir, parent,false);
        }

        TextView descripcion = (TextView) convertView.findViewById(R.id.superiordialog);
        TextView cantidad = (TextView) convertView.findViewById(R.id.inferiordialog);
        TextView almacen = (TextView) convertView.findViewById(R.id.datalmacen);
        TextView contra = (TextView) convertView.findViewById(R.id.datcontratista);


        DialogoRecepcion dialogoRecepcion = getItem(position);

        assert dialogoRecepcion != null;
        descripcion.setText(dialogoRecepcion.cantidadRS+ " - " + dialogoRecepcion.unidad);
        cantidad.setText(dialogoRecepcion.material);
        if(dialogoRecepcion.claveConcepto.isEmpty()){
            almacen.setText(dialogoRecepcion.almacen);
        }else {
            almacen.setText(dialogoRecepcion.claveConcepto);
        }
        if(dialogoRecepcion.cargo == 1){
            contra.setText("Contratista: "+dialogoRecepcion.contratista+" (CON CARGO).");
        }else{
            contra.setText("Contratista: "+dialogoRecepcion.contratista+".");
        }

        convertView.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            convertView.getOverScrollMode();
        }
        return convertView;
    }



}

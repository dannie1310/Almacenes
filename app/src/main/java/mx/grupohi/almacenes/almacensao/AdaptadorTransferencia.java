package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Usuario on 23/02/2017.
 */

public class AdaptadorTransferencia extends ArrayAdapter<Transferencia> {
    private Context context;

    AdaptadorTransferencia(Context context, List<Transferencia> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_imprimir_e,
                    parent,
                    false);
        }

        TextView camion = (TextView) convertView.findViewById(R.id.folio);
        TextView material = (TextView) convertView.findViewById(R.id.nombre);
        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);
        TextView orden = (TextView) convertView.findViewById(R.id.orden);

        Transferencia en = getItem(position);

        assert en != null;
        camion.setText("Folio: " + en.folio);
        fecha.setText("Fecha: " + en.fecha);
        material.setText("Almacén: "+en.almacenOrigen);
        orden.setText("Observación : " + en.observacion);

        return convertView;
    }

}
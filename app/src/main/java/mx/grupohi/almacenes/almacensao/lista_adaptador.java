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
 * Created by Usuario on 23/01/2017.
 */

public class lista_adaptador  extends ArrayAdapter<OrdenCompra> {

    private Context context;

    lista_adaptador(Context context, List<OrdenCompra> objects) {
            super(context, 0, objects);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                convertView = inflater.inflate(
                        R.layout.item,
                        parent,
                        false);
            }

            TextView descripcion = (TextView) convertView.findViewById(R.id.textView_superior);
            TextView cantidad = (TextView) convertView.findViewById(R.id.textView_inferior);


            OrdenCompra orden = getItem(position);

            assert orden != null;
            descripcion.setText(orden.descripcion);
            cantidad.setText(orden.existencia + " " + orden.unidad);

            return convertView;
        }
}

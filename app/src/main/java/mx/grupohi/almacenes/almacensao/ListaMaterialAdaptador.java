package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Usuario on 02/02/2017.
 */

public class ListaMaterialAdaptador extends ArrayAdapter<MaterialesAlmacen> {

    private Context context;

    ListaMaterialAdaptador(Context context, List<MaterialesAlmacen> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item, parent,false);
        }

        TextView descripcion = (TextView) convertView.findViewById(R.id.textView_superior);
        TextView cantidad = (TextView) convertView.findViewById(R.id.textView_inferior);


        MaterialesAlmacen material = getItem(position);

        assert material != null;
        descripcion.setText(material.descripcion);
        cantidad.setText(material.cantidad + " " +material.unidad);
        convertView.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            convertView.getOverScrollMode();
        }
        return convertView;
    }
}

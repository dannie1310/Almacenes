package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Usuario on 18/01/2017.
 */

public class MaterialesAlmacen {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id_material;
    Integer id_almacen;
    Integer id_obra;
    Double cantidad;
    String unidad;
    String descripcion;



    MaterialesAlmacen(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("material_almacen", null, data) > -1;
        if (result) {

            this.id_material = Integer.valueOf(data.getAsInteger("id_material"));
            this.id_almacen = Integer.valueOf(data.getAsInteger("id_almacen"));
            this.id_obra = Integer.valueOf(data.getAsInteger("id_obra"));
            this.cantidad = data.getAsDouble("cantidad");
            this.unidad = data.getAsString("unidad");

        }
        System.out.println("material_almacen: "+ result + " i "+id_material+unidad);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM material_almacen");
        db.close();
    }

    public MaterialesAlmacen find(Integer id, Integer almacen, Integer idobra){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM materiales m LEFT JOIN material_almacen ma  ON m.id_material = ma.id_material LEFT JOIN entradadetalle ed ON ed.idmaterial = m.id_material LEFT JOIN transferenciadetalle td ON td.idmaterial = m.id_material WHERE m.id_material = '"+id+"' and ma.id_almacen = '"+almacen+"' or ed.idalmacen = '"+almacen+"' or td.idalmacenDestino = '"+almacen+"'", null);
        EntradaDetalle e = new EntradaDetalle(context);
        DialogoRecepcion d = new DialogoRecepcion(context);
        SalidaDetalle s = new SalidaDetalle(context);
        TransferenciaDetalle td = new TransferenciaDetalle(context);

        Double cantidad_entrada;
        Double cantidad_almacen;
        Double salida;
        Double salidasDetalle;
        Double aux_cantidad = 0.0;
        Double aux_transferenciaEntrada;
        Double aux_transferenciaSalida;
        System.out.println("PROBANDO: "+ c!= null);
        try{
            if(c != null && c.moveToFirst()){
                cantidad_entrada = e.getCantidad(id,almacen, idobra);
                cantidad_almacen = getCantidad(id,almacen, idobra);
                salidasDetalle = s.getCantidad(id, almacen, idobra);
                salida =  d.valorSalida(context,id, almacen);
                aux_transferenciaEntrada = td.getCantidadEntrada(id,almacen,idobra);
                aux_transferenciaSalida = td.getCantidadSalida(id,almacen,idobra);

                this.id_material = c.getInt(0);
                if(c.getInt(6)==0){
                    this.id_almacen = c.getInt(13);
                }else{
                    this.id_almacen = c.getInt(6);
                }
                this.id_obra = c.getInt(c.getColumnIndex("id_obra"));
                if(cantidad_almacen != 0){
                    aux_cantidad = aux_cantidad +cantidad_almacen;
                }
                if(cantidad_entrada != 0){
                    aux_cantidad = aux_cantidad + cantidad_entrada;
                }
                if(aux_transferenciaEntrada != 0){
                    aux_cantidad =aux_cantidad + aux_transferenciaEntrada;
                }
                if(salida != 0){
                    aux_cantidad = aux_cantidad - salida;
                }
                if(salidasDetalle !=0){
                    aux_cantidad = aux_cantidad - salidasDetalle;
                }
                if(aux_transferenciaSalida != 0){
                    aux_cantidad = aux_cantidad - aux_transferenciaSalida;
                }
                this.cantidad = aux_cantidad;

                if(c.getString(7) == null){
                    this.unidad = c.getString(17);
                }else{
                    this.unidad = c.getString(7);
                }
                this.id_obra = idobra;

                this.descripcion = c.getString(c.getColumnIndex("descripcion"));
                System.out.println("valores: "+cantidad_almacen+" + "+cantidad_entrada+" + "+aux_transferenciaEntrada+" - "+salida+ " - "+ salidasDetalle +" - "+aux_transferenciaSalida+" = "+cantidad);
                System.out.println("datos: "+id_material+" "+cantidad+" "+unidad+" "+id_obra+ " "+ descripcion);
            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }

    public static List<MaterialesAlmacen> getMateriales(Context context, String almacen, Integer idobra){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM materiales m LEFT JOIN material_almacen ma  ON m.id_material = ma.id_material LEFT JOIN entradadetalle ed ON ed.idmaterial = m.id_material LEFT JOIN entrada e ON e.id = ed.identrada  LEFT JOIN transferenciadetalle td ON td.idmaterial = m.id_material  WHERE ma.id_almacen = '"+almacen+"' or ed.idalmacen = '"+almacen+"' or td.idalmacenDestino = '"+almacen+"' GROUP BY ma.id_material",null);
        ArrayList ordenes = new ArrayList<MaterialesAlmacen>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    MaterialesAlmacen material = new MaterialesAlmacen(context);
                    material = material.find(c.getInt(0),Integer.valueOf(almacen),idobra);
                    ordenes.add(material);

                }
                Collections.sort(ordenes, new Comparator<MaterialesAlmacen>() {
                    @Override
                    public int compare(MaterialesAlmacen v1, MaterialesAlmacen v2) {
                        return Integer.valueOf(v2.id_obra).compareTo(Integer.valueOf(v1.id_obra));
                    }
                });

                return ordenes;
            }
            else {
                return new ArrayList<>();
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public Double getCantidad(Integer material, Integer almacen, Integer idobra){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(cantidad) FROM material_almacen WHERE id_material = '"+material+"'  and id_almacen = '"+almacen+"' and id_obra = '"+idobra+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                return c.getDouble(0);
            }
            else{
                return 0.0;
            }
        } finally {
            c.close();
            db.close();
        }
    }
}

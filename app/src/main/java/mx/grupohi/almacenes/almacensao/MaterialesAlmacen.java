package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
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

    public MaterialesAlmacen find(Integer id, Integer almacen){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM materiales m LEFT JOIN material_almacen ma  ON m.id_material = ma.id_material LEFT JOIN entradadetalle ed ON ed.idmaterial = m.id_material WHERE m.id_material = '"+id+"'", null);
        EntradaDetalle e = new EntradaDetalle(context);
        Double cantidad_entrada;
        Double cantidad_almacen;
        try{
            if(c != null && c.moveToFirst()){
                cantidad_entrada = e.getCantidad(id,almacen);
                cantidad_almacen = getCantidad(id,almacen);
                System.out.println("almacen: "+cantidad_almacen +" "+cantidad_entrada+almacen);

                this.id_material = c.getInt(c.getColumnIndex("id_material"));
                this.id_almacen = c.getInt(c.getColumnIndex("id_almacen"));
                this.id_obra = c.getInt(c.getColumnIndex("id_obra"));
                if(cantidad_almacen != 0 || cantidad_entrada != 0){
                    this.cantidad = cantidad_almacen +cantidad_entrada;
                }else{
                    this.cantidad = c.getDouble(c.getColumnIndex("cantidad"));
                }
                if(c.getString(c.getColumnIndex("unidad")) == null){
                    this.unidad = c.getString(18);
                }else{
                    this.unidad = c.getString(c.getColumnIndex("unidad"));
                }

                this.descripcion = c.getString(c.getColumnIndex("descripcion"));

            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }

    public static List<MaterialesAlmacen> getMateriales(Context context, String almacen){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM materiales m LEFT JOIN material_almacen ma  ON m.id_material = ma.id_material LEFT JOIN entradadetalle ed ON ed.idmaterial = m.id_material WHERE ma.id_almacen = '"+almacen+"' or ed.idalmacen = '"+almacen+"' GROUP BY ma.id_material",null);
        ArrayList ordenes = new ArrayList<MaterialesAlmacen>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    MaterialesAlmacen material = new MaterialesAlmacen(context);
                    material = material.find(c.getInt(0),Integer.valueOf(almacen));
                    ordenes.add(material);
                    System.out.println("ordenes:aaaaa "+c.getInt(0));
                }
                Collections.sort(ordenes, new Comparator<MaterialesAlmacen>() {
                    @Override
                    public int compare(MaterialesAlmacen v1, MaterialesAlmacen v2) {
                        return Integer.valueOf(v2.id_almacen).compareTo(Integer.valueOf(v1.id_almacen));
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

    public Double getCantidad(Integer material, Integer almacen){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(cantidad) FROM material_almacen WHERE id_material = '"+material+"'  and id_almacen = '"+almacen+"'", null);
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

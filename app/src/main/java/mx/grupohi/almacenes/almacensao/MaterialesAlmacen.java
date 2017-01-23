package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    Integer cantidad;
    String unidad;


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
            this.cantidad = Integer.valueOf(data.getAsInteger("cantidad"));
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

    public MaterialesAlmacen find(Integer id){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SSELECT * FROM material_almacen WHERE id_material = '"+id+"'", null);
        try{
            if(c != null && c.moveToFirst()){

                this.id_material = c.getInt(c.getColumnIndex("id_material"));
                this.id_almacen = c.getInt(c.getColumnIndex("id_almacen"));
                this.id_obra = c.getInt(c.getColumnIndex("id_obra"));
                this.cantidad = c.getInt(c.getColumnIndex("cantidad"));
                this.unidad = c.getString(c.getColumnIndex("unidad"));

            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }
}

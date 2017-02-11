package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 01/02/2017.
 */

public class Entrada {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;


    String idorden;
    String referencia;
    String observacion;
    String idmaterial;
    String fecha;
    Integer idobra;

   Entrada(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    Integer create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("entrada", null, data) > -1;
        if (result) {

            Cursor c = db.rawQuery("SELECT ID FROM entrada WHERE fecha = '" + data.getAsString("fecha") + "'  ORDER BY ID DESC LIMIT 1", null);
            try {
                if(c != null && c.moveToFirst()) {
                    this.id = c.getInt(0);
                    this.referencia = data.getAsString("referencia");
                    this.observacion = data.getAsString("observacion");
                    this.idorden = data.getAsString("idorden");
                    this.fecha = data.getAsString("fecha");
                    this.idmaterial = data.getAsString("idmaterial");
                    this.idobra = data.getAsInteger("idobra");
                }
            } finally {
                c.close();
                db.close();
            }


        }
        System.out.println("resultAlmacen: "+ id + " y "+fecha+" material "+idmaterial);
        return id;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM entrada");
        db.close();
    }

    public static void remove(Context context, Integer id) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        System.out.println("eliminar: ENTRADA: "+id);
        db.execSQL("DELETE FROM entrada WHERE ID = '" + id + "'");
        db.close();
    }
}

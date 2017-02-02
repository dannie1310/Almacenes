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

   Entrada(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    Integer create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("entrada", null, data) > -1;
        if (result) {

            Cursor c = db.rawQuery("SELECT ID FROM entrada WHERE fecha = '" + data.getAsString("fecha") + "'", null);
            try {
                if(c != null && c.moveToFirst()) {
                    this.id = c.getInt(0);
                    this.referencia = data.getAsString("referencia");
                    this.observacion = data.getAsString("observacion");
                    this.idorden = data.getAsString("idorden");
                    this.fecha = data.getAsString("fecha");
                    this.idmaterial = data.getAsString("idmaterial");
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

}

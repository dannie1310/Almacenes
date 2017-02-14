package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 13/02/2017.
 */

public class Transferencia {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;


    String idalmacenOrigen;
    String referencia;
    String observacion;
    String idalmacenNew;
    String fecha;
    Integer idobra;

    Transferencia(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }


    Integer create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("transferencia: "+data);
        Boolean result = db.insert("transferencia", null, data) > -1;
        if (result) {

            Cursor c = db.rawQuery("SELECT ID FROM transferencia WHERE fecha = '" + data.getAsString("fecha") + "' ORDER BY ID DESC LIMIT 1", null);
            try {
                if(c != null && c.moveToFirst()) {
                    this.id = c.getInt(0);
                    this.referencia = data.getAsString("referencia");
                    this.observacion = data.getAsString("observacion");
                    this.idalmacenOrigen = data.getAsString("idalmacenOrigen");
                    this.fecha = data.getAsString("fecha");
                    this.idobra = data.getAsInteger("idobra");
                }
            } finally {
                c.close();
                db.close();
            }
        }
        return id;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM transferencia");
        db.close();
    }


    public static void remove(Context context, Integer id) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        System.out.println("eliminar: transferencia: "+id);
        db.execSQL("DELETE FROM transferencia WHERE ID = '" + id + "'");
        db.close();
    }
}

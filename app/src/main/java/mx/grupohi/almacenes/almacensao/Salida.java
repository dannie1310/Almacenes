package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 08/02/2017.
 */

public class Salida {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;


    String idalmacen;
    String referencia;
    String observacion;
    String concepto;
    String fecha;
    String folio;
    Integer idobra;

    Salida(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    Integer create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("salida", null, data) > -1;
        if (result) {

            Cursor c = db.rawQuery("SELECT ID FROM salida WHERE fecha = '" + data.getAsString("fecha") + "' ORDER BY ID DESC LIMIT 1", null);
            try {
                if(c != null && c.moveToFirst()) {
                    this.id = c.getInt(0);
                    this.referencia = data.getAsString("referencia");
                    this.observacion = data.getAsString("observacion");
                    this.idalmacen = data.getAsString("idalmacen");
                    this.fecha = data.getAsString("fecha");
                    this.concepto = data.getAsString("concepto");
                    this.idobra = data.getAsInteger("idobra");
                    this.folio = data.getAsString("folio");
                }
            } finally {
                c.close();
                db.close();
            }


        }
        System.out.println("resultAlmacen: "+ id + " y "+fecha+" material "+concepto);
        return id;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM salida");
        db.close();
    }

    public static void remove(Context context, Integer id) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        System.out.println("eliminar: salida: "+id);
        db.execSQL("DELETE FROM salida WHERE ID = '" + id + "'");
        db.close();
    }

    Boolean folio(String folio){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salida WHERE folio  = '"+folio+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                System.out.println("folio? : "+folio +" "+true);
                return true;
            }
            else{
                return false;
            }
        } finally {
            c.close();
            db.close();
        }
    }
}

package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Usuario on 18/01/2017.
 */

public class Contratista {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer idempresa;
    String razonsocial;

    Contratista(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("contratistas", null, data) > -1;
        if (result) {
            this.idempresa = Integer.valueOf(data.getAsInteger("idempresa"));
            this.razonsocial = data.getAsString("razonsocial");

        }
        System.out.println("contratistas: "+ result + " i "+idempresa+razonsocial);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM contratistas");
        db.close();
    }

    ArrayList<String> getArrayListContratistas() {

        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contratistas ORDER BY razonsocial ASC", null);
        if (c != null && c.moveToFirst())
            try {

                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("razonsocial")));
                    System.out.println("razonsocial: "+ c.getString(0) + c.getString(1));
                } else {
                    data.add("-- Seleccione --");
                    data.add(c.getString(c.getColumnIndex("razonsocial")));
                    System.out.println("razonsocial: "+ c.getString(0) + c.getString(1));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("razonsocial")));
                        System.out.println("razonsocial: "+ c.getString(0) + c.getString(1));
                    }
                }
            } finally {
                c.close();
                db.close();
            }

        return data;
    }
    ArrayList<String> getArrayListId() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contratistas ORDER BY razonsocial ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("idempresa")));
                    System.out.println("idempresa ID: "+ c.getString(0));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("idempresa")));
                    System.out.println("idempresa ID: "+ c.getString(0));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("idempresa")));
                        System.out.println("idempresa ID: "+ c.getString(0));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }
}

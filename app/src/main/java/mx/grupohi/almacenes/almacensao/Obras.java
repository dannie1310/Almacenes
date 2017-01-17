package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Usuario on 17/01/2017.
 */

public class Obras {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id_obra;
    Integer id_base;
    String nombre;
    String base;

    Obras(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("obras", null, data) > -1;
        if (result) {
            this.id_base = Integer.valueOf(data.getAsInteger("idbase"));
            this.nombre = data.getAsString("nombre");
            this.id_obra = Integer.valueOf(data.getAsInteger("idobra"));
            this.base = data.getAsString("base");
        }
        System.out.println("result: "+ result + " i "+base+nombre);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM obras");
        db.close();
    }

    static ArrayList<String> getArrayListNombres(Context context) {
        System.out.println("obra?? ");
        ArrayList<String> data = new ArrayList<>();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM obras ORDER BY ID ASC", null);

        if (c != null && c.moveToFirst())
            try {
                data.add("-- Seleccione --");
                data.add(c.getString(c.getColumnIndex("id")) + " [" + c.getString(c.getColumnIndex("nombre")) + "]");
                System.out.println("obras: "+ c.getColumnIndex("id")+c.getColumnIndex("nombre"));
                while (c.moveToNext()) {
                    data.add(c.getString(c.getColumnIndex("id")) + " [" + c.getString(c.getColumnIndex("nombre")) + "]");
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }
    static ArrayList<String> getArrayListId(Context context) {
        ArrayList<String> data = new ArrayList<>();
        String query;
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        query = "SELECT * FROM obras ORDER BY ID ASC";


        Cursor c = db.rawQuery(query, null);
        try {
            if (c != null && c.moveToFirst()) {
                data.add("0");
                data.add(c.getString(c.getColumnIndex("id")));
                while (c.moveToNext()) {
                    data.add(c.getString(c.getColumnIndex("id")));
                }
            }
            return data;
        } finally {
            c.close();
            db.close();
        }
    }

}

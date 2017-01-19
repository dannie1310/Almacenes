package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Usuario on 17/01/2017.
 */

public class Obra {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;
    Integer id_obra;
    Integer id_base;
    String nombre;
    String base;

    Obra(Context context) {
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

   ArrayList<String> getArrayListNombres() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM obras ORDER BY nombre ASC", null);
        if (c != null && c.moveToFirst())
            try {

                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("nombre"))+" ["+c.getString(c.getColumnIndex("base"))+"].");
                    System.out.println("obras: "+ c.getString(0) + c.getString(2));
                } else {
                    data.add("-- Seleccione --");
                    data.add(c.getString(c.getColumnIndex("nombre"))+" ["+c.getString(c.getColumnIndex("base"))+"].");
                    System.out.println("obras: "+ c.getString(0) + c.getString(2));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("nombre"))+" ["+c.getString(c.getColumnIndex("base"))+"].");
                        System.out.println("obras: "+ c.getString(0) + c.getString(2));
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
        Cursor c = db.rawQuery("SELECT * FROM obras ORDER BY nombre ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("ID")));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("ID")));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("ID")));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    public Obra find(Integer id){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM obras WHERE ID = '"+id+"'", null);
        try{
            if(c != null && c.moveToFirst()){
                this.id_obra = c.getInt(c.getColumnIndex("idobra"));
                this.id_base = c.getInt(c.getColumnIndex("idbase"));
                this.nombre = c.getString(c.getColumnIndex("nombre"));
                this.base = c.getString(c.getColumnIndex("base"));

            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }



}

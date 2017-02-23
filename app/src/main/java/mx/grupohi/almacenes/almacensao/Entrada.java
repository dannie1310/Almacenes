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
 * Created by Usuario on 01/02/2017.
 */

public class Entrada {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;


    String idorden;
    String folio;
    String referencia;
    String observacion;
    String idmaterial;
    String fecha;
    Integer idobra;
    Integer numerofolio;

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
                    this.folio = data.getAsString("folio");
                    this.numerofolio = data.getAsInteger("numerofolio");
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

    Boolean folio(String folio){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entrada WHERE folio  = '"+folio+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
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
    public static List<Entrada> getEntradas(Context context){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entrada ORDER BY fecha", null);
        ArrayList entradas = new ArrayList<Entrada>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    Entrada entrada = new Entrada(context);
                    entrada = entrada.findE(c.getInt(0));
                    entradas.add(entrada);

                }
                Collections.sort(entradas, new Comparator<Entrada>() {
                    @Override
                    public int compare(Entrada v1, Entrada v2) {
                        return String.valueOf(v2.folio).compareTo(String.valueOf(v1.folio));
                    }
                });
                System.out.println("entraas: "+entradas);
                return entradas;
            }
            else {
                return new ArrayList<>();
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public Entrada findE (Integer id) {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entrada WHERE id='"+id+"'", null);
        try {
            if (c != null && c.moveToFirst()) {
                this.id = c.getInt(0);
                this.referencia = c.getString(3);
                this.observacion = c.getString(4);
                this.idorden = c.getString(1);
                this.fecha = c.getString(5);
                this.idmaterial = c.getString(2);
                this.idobra = c.getInt(6);
                this.folio = c.getString(7);
                this.numerofolio = c.getInt(8);

                return this;
            } else {
                return null;
            }
        } finally {
            c.close();
            db.close();
        }
    }
}

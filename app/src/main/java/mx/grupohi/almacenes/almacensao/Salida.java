package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    String almacen;
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
    public static List<Salida> getSalida(Context context){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salida  ORDER BY fecha", null);
        ArrayList sa = new ArrayList<Salida>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    Salida s = new Salida(context);
                    s = s.findS(c.getInt(0));
                    sa.add(s);
                }
               Collections.sort(sa, new Comparator<Salida>() {
                    @Override
                    public int compare(Salida v1, Salida v2) {
                        return String.valueOf(v2.folio).compareTo(String.valueOf(v1.folio));
                    }
                });
                System.out.println("entraas: "+sa);
                return sa;
            }
            else {
                return new ArrayList<>();
            }
        } finally {
            c.close();
            db.close();
        }
    }
    public Salida findS (Integer id) {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salida s LEFT JOIN almacenes a ON s.idalmacen = a.id_almacen  WHERE s.id='"+id+"'", null);
        try {
            if (c != null && c.moveToFirst()) {
                this.id = c.getInt(0);
                this.fecha = c.getString(5);
                this.idalmacen = c.getString(1);
                this.referencia = c.getString(2);
                this.observacion = c.getString(3);
                this.concepto =c.getString(4);
                this.folio = c.getString(7);

                if(c.getString(9)== null){
                    this.almacen = "null";
                }else{
                    this.almacen = c.getString(9);
                }


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

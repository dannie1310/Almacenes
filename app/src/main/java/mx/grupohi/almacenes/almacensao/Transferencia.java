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
    String almacenOrigen;
    Integer idobra;
    String folio;

    Transferencia(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }


    Integer create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("transferencia: "+data);
        Boolean result = db.insert("transferencia", null, data) > -1;
        if (result) {

            Cursor c = db.rawQuery("SELECT ID FROM transferencia WHERE folio = '" + data.getAsString("folio") + "' ORDER BY ID DESC LIMIT 1", null);
            try {
                if(c != null && c.moveToFirst()) {
                    this.id = c.getInt(0);
                    this.referencia = data.getAsString("referencia");
                    this.observacion = data.getAsString("observacion");
                    this.idalmacenOrigen = data.getAsString("idalmacenOrigen");
                    this.fecha = data.getAsString("fecha");
                    this.idobra = data.getAsInteger("idobra");
                    this.folio = data.getAsString("folio");
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

    Boolean folio(String folio){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferencia WHERE folio  = '"+folio+"'", null);
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

    public static List<Transferencia> getTransferencia(Context context, Integer idobra){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferencia WHERE idobra = '"+idobra+"' ORDER BY fecha", null);
        ArrayList sa = new ArrayList<Transferencia>();
        try {
            if (c != null){
                while (c.moveToNext()) {
                    Transferencia s = new Transferencia(context);
                    s = s.findT(c.getInt(0),c.getInt(5));
                    sa.add(s);
                }
                Collections.sort(sa, new Comparator<Transferencia>() {
                    @Override
                    public int compare(Transferencia v1, Transferencia v2) {
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

    public Transferencia findT (Integer id, Integer idobra) {
        db = db_sca.getWritableDatabase();
        Almacen a = new Almacen(context);
        Cursor c = db.rawQuery("SELECT * FROM transferencia WHERE id='"+id+"' and  idobra = '"+idobra+"' ", null);
        try {
            if (c != null && c.moveToFirst()) {

                this.idalmacenOrigen = c.getString(1);
                this.fecha =c.getString(4);
                this.almacenOrigen = a.getIdAlmacen(c.getInt(1));
                this.referencia = c.getString(2);
                this.observacion = c.getString(3);
                this.idobra = c.getInt(5);
                this.folio = c.getString(6);


                return this;
            } else {
                return null;
            }
        } finally {
            c.close();
            db.close();
        }
    }
    static Boolean isSync(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Boolean result = true;
        Cursor c = db.rawQuery("SELECT * FROM transferencia", null);
        try {
            if(c != null && c.moveToFirst()) {
                result = false;
            }
            return result;
        } finally {
            c.close();
            db.close();
        }
    }
    static Integer getCount(Context context) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferencia",null);
        try {
            return c.getCount();
        } finally {
            c.close();
            db.close();
        }
    }
}

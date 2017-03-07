package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Usuario on 13/02/2017.
 */

public class TransferenciaDetalle {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;

    Integer id;
    Double cantidad;
    String idcontratista;
    Integer cargo;
    Integer idtransferencia;
    String fecha;
    String unidad;
    String idmaterial;
    String idalmacenOrigen;
    String idalmacenDestino;
    String  almacenOrigen;
    String almacenDestino;
    String referencia;
    String observacion;
    String folio;
    String material;
    String contratista;
    Integer idobra;

    TransferenciaDetalle(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("transferenciadetalle: "+data);
        Boolean result = db.insert("transferenciadetalle", null, data) > -1;
        if (result) {

            this.idmaterial = data.getAsString("idmaterial");
            this.cantidad = data.getAsDouble("cantidad");
            this.idtransferencia = data.getAsInteger("idtransferencia");
            this.idalmacenOrigen = data.getAsString("idalmacenOrigen");
            this.idalmacenDestino = data.getAsString("idalmacenDestino");
            this.idcontratista = data.getAsString("idcontratista");
            this.cargo = data.getAsInteger("cargo");
            this.unidad = data.getAsString("unidad");
            this.fecha = data.getAsString("fecha");

        }

        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM transferenciadetalle");
        db.close();
    }

    Boolean find(Integer idsalida){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferenciadetalle WHERE idtransferencia  = '"+idsalida+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                System.out.println("encontrar true"+idsalida + c.getString(2));
                return true;
            }
            else{
                System.out.println("encontrar false"+idsalida+c.toString());
                return false;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public  Double getCantidadEntrada(Integer material, Integer almacen, Integer idobra){

        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(td.cantidad) FROM transferenciadetalle td INNER JOIN transferencia t ON t.id = td.idtransferencia WHERE td.idmaterial = '"+material+"' and td.idalmacenDestino = '"+almacen+"' and t.idobra = '"+idobra+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                return c.getDouble(0);
            }
            else{
                return 0.0;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public  Double getCantidadSalida(Integer material, Integer almacen, Integer idobra){

        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(td.cantidad) FROM transferenciadetalle td INNER JOIN transferencia t ON t.id = td.idtransferencia WHERE td.idmaterial = '"+material+"' and t.idalmacenOrigen = '"+almacen+"' and t.idobra = '"+idobra+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                return c.getDouble(0);
            }
            else{
                return 0.0;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    static JSONObject getTransferenciaDetalle(Context context, String folio) {
        JSONObject JSON = new JSONObject();
        Almacen a = new Almacen(context);
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferenciadetalle td INNER JOIN transferencia t ON t.id = td.idtransferencia LEFT JOIN almacenes a ON td.idalmacenDestino = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = td.idContratista INNER JOIN materiales m ON m.id_material = td.idmaterial WHERE t.folio = '"+folio+"' ORDER BY a.id_almacen", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id", c.getString(0));
                    json.put("idalmacenOrigen", c.getString(2));
                    json.put("almacenOrigen", a.getIdAlmacen(c.getInt(2)));
                    json.put("cantidad", c.getString(4));
                    json.put("cargo", c.getString(7));
                    json.put("unidad", c.getString(8));
                    json.put("fecha", c.getString(9));
                    json.put("referencia", c.getString(12));
                    json.put("observacion", c.getString(13));
                    json.put("idobra", c.getString(15));
                    json.put("folio", c.getString(16));
                    json.put("almacenDestino", c.getString(18));

                    if(c.getString(20) == null){
                        json.put("contratista", "null");
                    }else{
                        json.put("contratista", c.getString(20));
                    }
                    System.out.println("contrat: "+c.getString(20));

                    json.put("material", c.getString(24));

                    JSON.put(i + "", json);
                    i++;

                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
        }
        //git System.out.println("JSON: "+JSON);
        return JSON;
    }

    static JSONObject getJSON(Context context) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM  transferencia t LEFT JOIN almacenes a ON t.idalmacenorigen = a.id_almacen ORDER BY t.id", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();
                    json.put("idtransferencia", c.getString(0));
                    json.put("folio", c.getString(6));
                    json.put("idalmacenOrigen", c.getString(1));
                    json.put("almacenOrigen", c.getString(8));
                    json.put("referencia", c.getString(2));
                    json.put("observacion", c.getString(3));
                    json.put("fecha", c.getString(4));
                    json.put("idobra", c.getString(5));
                    json.put("detalle", getJSONDetalle(context, c.getInt(0)));

                    JSON.put(i + "", json);
                    i++;

                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
        }
        //System.out.println("JSONTransferencia: "+JSON);
        return JSON;
    }

    static JSONObject getJSONDetalle(Context context, Integer id) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM transferenciadetalle td LEFT JOIN almacenes a ON td.idalmacenDestino = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = td.idContratista INNER JOIN materiales m ON m.id_material = td.idmaterial WHERE td.idtransferencia = '"+id+"' ORDER BY td.id", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id", c.getString(0));
                    json.put("idtransferencia", c.getString(1));
                    json.put("idalmacenOrigen", c.getString(2));
                    json.put("idalmacenDestino", c.getString(3));
                    json.put("almacenDestino", c.getString(11));
                    json.put("cantidad", c.getString(4));
                    json.put("idmaterial", c.getString(5));
                    json.put("material", c.getString(17));
                    json.put("idcontratista", c.getString(6));
                    json.put("contratista", c.getString(13));
                    json.put("cargo", c.getString(7));
                    json.put("unidad", c.getString(8));
                    json.put("fecha", c.getString(9));

                    JSON.put(i + "", json);
                    i++;

                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
        }
        //System.out.println("JSONDetalle: "+JSON);
        return JSON;
    }
}

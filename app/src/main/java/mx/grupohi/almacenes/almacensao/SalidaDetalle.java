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
 * Created by Usuario on 08/02/2017.
 */

public class SalidaDetalle {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;

    Integer id;
    Double cantidad;
    String claveConcepto;
    String idcontratista;
    Integer cargo;
    Integer idsalida;
    String fecha;
    String unidad;
    String idmaterial;
    Integer idalmacen;
    String almacen;
    String referencia;
    String observacion;
    String concepto;
    String contratista;
    String folio;
    String material;


    SalidaDetalle(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("salidadetalle", null, data) > -1;
        if (result) {

            this.idmaterial = data.getAsString("idmaterial");
            this.cantidad = data.getAsDouble("cantidad");
            this.idsalida = data.getAsInteger("idsalida");
            this.claveConcepto = data.getAsString("claveConcepto");
            this.idcontratista = data.getAsString("idcontratista");
            this.cargo = data.getAsInteger("cargo");
            this.unidad = data.getAsString("unidad");

        }
        System.out.println("resultAlmacen: "+ idsalida + " i "+idmaterial);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM salidadetalle");
        db.close();
    }

    public  Double getCantidad(Integer material, Integer almacen, Integer idobra){

        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(sd.cantidad) FROM salidadetalle sd INNER JOIN salida s ON s.id = sd.idsalida WHERE sd.idmaterial = '"+material+"' and s.idalmacen = '"+almacen+"' and s.idobra = '"+idobra+"'", null);
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

    Boolean find(Integer idsalida){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salidadetalle WHERE idsalida  = '"+idsalida+"'", null);
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

    static JSONObject getSalidaDetalle(Context context, String folio) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salidadetalle sd INNER JOIN salida s ON s.id = sd.idsalida LEFT JOIN almacenes a ON s.idalmacen = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = sd.idContratista INNER JOIN materiales m ON m.id_material = sd.idmaterial WHERE s.folio = '"+folio+"' ORDER BY a.id_almacen", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id", c.getString(0));
                    json.put("idsalida", c.getString(1));
                    json.put("cantidad", c.getString(2));
                    json.put("idmaterial", c.getInt(3));
                    json.put("clave", c.getString(4).replaceAll(" +"," ").trim());
                    json.put("idcontratista", c.getString(5));
                    json.put("cargo", c.getString(6));
                    json.put("unidad", c.getString(7));
                    json.put("fecha", c.getString(8));
                    json.put("idalmacen", c.getString(10));
                    json.put("referencia", c.getString(11));
                    json.put("observacion", c.getString(12));
                    json.put("concepto", c.getString(13));

                    json.put("folio", c.getString(16));

                    if(c.getString(18)== null){
                        json.put("almacen","null");
                    }else{
                        json.put("almacen", c.getString(18));
                    }
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
        System.out.println("JSON: "+JSON);
        return JSON;
    }

    static JSONObject getJSON(Context context) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salida s LEFT JOIN almacenes a ON s.idalmacen = a.id_almacen ORDER BY s.id", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();
                    json.put("idsalida", c.getString(0));
                    json.put("folio", c.getString(7));
                    json.put("referencia", c.getString(2));
                    json.put("observacion", c.getString(3));
                    json.put("concepto", c.getString(4));
                    json.put("fecha", c.getString(5));
                    json.put("idobra", c.getString(6));
                    json.put("idalmacen", c.getString(8));
                    json.put("almacen", c.getString(9));
                    json.put("detalle", getJSONDetalle(context,c.getInt(0)));

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
        System.out.println("JSONSalida: "+JSON);
        return JSON;
    }

    static JSONObject getJSONDetalle(Context context, Integer id) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salidadetalle sd LEFT JOIN contratistas c ON c.idempresa = sd.idContratista INNER JOIN materiales m ON m.id_material = sd.idmaterial WHERE sd.idsalida = '"+id+"' ORDER BY sd.id", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id", c.getString(0));
                    json.put("idsalida", c.getString(1));
                    json.put("cantidad", c.getString(2));
                    json.put("idmaterial", c.getString(3));
                    json.put("material", c.getString(14));
                    json.put("clave", c.getString(4));
                    json.put("idcontratista", c.getString(5));
                    json.put("contratista", c.getString(10));
                    json.put("cargo", c.getString(6));
                    json.put("unidad", c.getString(7));
                    json.put("fecha", c.getString(8));


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
        System.out.println("JSONDetalleS: "+JSON);
        return JSON;
    }

}

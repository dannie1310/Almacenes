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
 * Created by Usuario on 01/02/2017.
 */

public class EntradaDetalle {


    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;

    Integer id;
    Double cantidad;
    String claveConcepto;
    String idcontratista;
    Integer cargo;
    Integer identrada;
    String idalmacen;
    String almacen;
    String fecha;
    String unidad;
    String idmaterial;
    String idorden;
    String folio;
    String referencia;
    String observacion;
    String material;
    String contratista;
    Integer idobra;


    EntradaDetalle(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("entradadetalle", null, data) > -1;
        if (result) {

            this.idalmacen = data.getAsString("idalmacen");
            this.cantidad = data.getAsDouble("cantidadTotal");
            this.identrada = data.getAsInteger("identrada");
            this.claveConcepto = data.getAsString("claveConcepto");
            this.idcontratista = data.getAsString("idcontratista");
            this.cargo = data.getAsInteger("cargo");
            this.unidad = data.getAsString("unidad");
            this.idmaterial = data.getAsString("idmaterial");

        }
        System.out.println("resultAlmacen: "+ identrada + " i "+idalmacen);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM entradadetalle");
        db.close();
    }

    public Double sumaCantidad(Integer idorden, Integer material){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Usuario usuario = new Usuario(context);
        Integer idobra = usuario.getIdObra();
        System.out.println("idobra: "+idobra);
        Cursor c = db.rawQuery("SELECT SUM(cantidad) as resta FROM  entradadetalle ed INNER JOIN entrada e ON e.id = ed.identrada WHERE e.idorden = '"+idorden+"' and e.idmaterial = '"+material+"' and e.idobra = '"+idobra+"' GROUP BY e.idmaterial", null);
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
    public  Double getCantidad(Integer material, Integer almacen, Integer idobra){

        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(cantidad) FROM entradadetalle ed INNER JOIN entrada e ON e.id = ed.identrada WHERE ed.idmaterial = '"+material+"' and ed.idalmacen = '"+almacen+"' and e.idobra = '"+idobra+"'", null);
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
        Cursor c = db.rawQuery("SELECT * FROM entradadetalle WHERE identrada  = '"+idsalida+"'", null);
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

   static JSONObject getEntradasDetalle(Context context, String folio) {
        JSONObject JSON = new JSONObject();
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entradadetalle ed INNER JOIN entrada e ON e.id = ed.identrada LEFT JOIN almacenes a ON ed.idalmacen = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = ed.idContratista INNER JOIN materiales m ON m.id_material = ed.idmaterial WHERE e.folio = '"+folio+"' ORDER BY a.id_almacen", null);
        try {
            if(c != null && c.moveToFirst()) {
                Integer i = 0;
                do {

                    JSONObject json = new JSONObject();

                    json.put("id", c.getString(0));
                    json.put("identrada", c.getString(1));
                    json.put("cantidad", c.getString(2));
                    json.put("idalmacen", c.getInt(3));
                    json.put("clave", c.getString(4));
                    json.put("idcontratista", c.getString(5));
                    json.put("cargo", c.getString(6));
                    json.put("unidad", c.getString(7));
                    json.put("idmaterial", c.getString(8));
                    json.put("fecha", c.getString(9));
                    json.put("idorden", c.getString(11));
                    json.put("idmaterialE", c.getString(12));
                    json.put("referencia", c.getString(13));
                    json.put("observacion", c.getString(14));
                    json.put("fecha", c.getString(15));
                    json.put("idobra", c.getString(16));
                    json.put("folio", c.getString(17));
                    json.put("numerofolio", c.getString(18));

                    if(c.getString(20)== null){
                        json.put("almacen","null");
                    }else{
                        json.put("almacen", c.getString(20));
                    }
                    if(c.getString(22) == null){
                        json.put("contratista", "null");
                    }else{
                        json.put("contratista", c.getString(22));
                    }

                    json.put("material", c.getString(26));

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
}

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

    public static List<SalidaDetalle> getSalida(Context context){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salidadetalle  ORDER BY fecha", null);
        ArrayList sa = new ArrayList<SalidaDetalle>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    SalidaDetalle s = new SalidaDetalle(context);
                    s = s.findS(c.getInt(0));
                    sa.add(s);
                }
                Collections.sort(sa, new Comparator<SalidaDetalle>() {
                    @Override
                    public int compare(SalidaDetalle v1, SalidaDetalle v2) {
                        return Integer.valueOf(v2.id).compareTo(Integer.valueOf(v1.id));
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

    public SalidaDetalle findS (Integer id) {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM salidadetalle sd INNER JOIN salida s ON s.id = sd.idsalida LEFT JOIN almacenes a ON s.idalmacen = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = sd.idContratista INNER JOIN materiales m ON m.id_material = sd.idmaterial WHERE sd.id='"+id+"'", null);
        try {
            if (c != null && c.moveToFirst()) {


                this.idsalida = c.getInt(1);
                this.cantidad = c.getDouble(2);
                this.idmaterial = c.getString(3);
                this.claveConcepto = c.getString(4);
                this.idcontratista = c.getString(5);
                this.cargo =c.getInt(6);
                this.unidad = c.getString(7);
                this.fecha = c.getString(8);
                this.idalmacen = c.getInt(10);
                this.referencia = c.getString(11);
                this.observacion = c.getString(12);
                this.concepto =c.getString(13);
                this.folio = c.getString(16);

                this.material =c.getString(24);

                if(c.getString(18)== null){
                    this.almacen = "null";
                }else{
                    this.almacen = c.getString(18);
                }
                if(c.getString(20) == null){
                    this.contratista = "null";
                }else{
                    this.contratista = c.getString(20);
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

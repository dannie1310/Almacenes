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

                    if(c.getString(19)== null){
                        json.put("almacen","null");
                    }else{
                        json.put("almacen", c.getString(19));
                    }
                    if(c.getString(21) == null){
                        json.put("contratista", "null");
                    }else{
                        json.put("contratista", c.getString(21));
                    }
                    System.out.println("contrat: "+c.getString(19));

                    json.put("material", c.getString(25));

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

    public static List<EntradaDetalle> getEntradas(Context context){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entradadetalle  ORDER BY fecha", null);
        ArrayList entradas = new ArrayList<EntradaDetalle>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    EntradaDetalle entrada = new EntradaDetalle(context);
                    entrada = entrada.findE(c.getInt(0));
                    entradas.add(entrada);
                }
                Collections.sort(entradas, new Comparator<EntradaDetalle>() {
                    @Override
                    public int compare(EntradaDetalle v1, EntradaDetalle v2) {
                        return Integer.valueOf(v2.id).compareTo(Integer.valueOf(v1.id));
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

    public EntradaDetalle findE (Integer id) {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM entradadetalle ed INNER JOIN entrada e ON e.id = ed.identrada LEFT JOIN almacenes a ON ed.idalmacen = a.id_almacen LEFT JOIN contratistas c ON c.idempresa = ed.idContratista INNER JOIN materiales m ON m.id_material = ed.idmaterial WHERE ed.id='"+id+"'", null);
        try {
            if (c != null && c.moveToFirst()) {
                this.idalmacen = c.getString(3);
                this.cantidad = c.getDouble(2);
                this.identrada = c.getInt(1);
                this.claveConcepto = c.getString(4);
                this.idcontratista = c.getString(5);
                this.cargo =c.getInt(6);
                this.unidad = c.getString(7);
                this.idmaterial = c.getString(8);
                this.fecha = c.getString(9);
                this.referencia = c.getString(13);
                this.observacion = c.getString(14);
                this.idorden = c.getString(11);
                this.idobra =c.getInt(16);
                this.folio = c.getString(17);

                this.material =c.getString(25);

                if(c.getString(19)== null){
                    this.almacen = "null";
                }else{
                    this.almacen = c.getString(19);
                }
                if(c.getString(21) == null){
                   this.contratista = "null";
                }else{
                    this.contratista = c.getString(21);
                }
                System.out.println("contrat: "+c.getString(19));

               this.material = c.getString(25);

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

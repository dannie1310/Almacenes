package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 30/01/2017.
 */

public class DialogoRecepcion {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id;
    String cantidadtotal;
    String cantidadRS;
    String claveConcepto;
    String idcontratista;
    Integer cargo;
    String id_almacen;
    String idorden;
    String almacen;
    String material;
    String unidad;
    String contratista;
    String idmaterial;

    DialogoRecepcion(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        System.out.println("datos: "+data);
        Boolean result = db.insert("dialogo_recepcion", null, data) > -1;
        if (result) {
            this.id_almacen = data.getAsString("idalmacen");
            this.cantidadtotal = data.getAsString("cantidadTotal");
            this.cantidadRS = data.getAsString("cantidadRS");
            this.claveConcepto = data.getAsString("claveConcepto");
            this.idcontratista = data.getAsString("idcontratista");
            this.cargo = data.getAsInteger("cargo");
            this.idorden = data.getAsString("idorden");
            this.almacen = data.getAsString("almacen");
            this.material = data.getAsString("material");
            this.unidad = data.getAsString("unidad");
            this.contratista = data.getAsString("contratista");
            this.idmaterial = data.getAsString("idmaterial");

        }
        System.out.println("resultAlmacen: "+ idorden + " i "+id_almacen+claveConcepto);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM dialogo_recepcion");
        db.close();
    }
    public static List<DialogoRecepcion> getRecepcion(Context context, String idorden, String idmaterial){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        System.out.println("folioRecepcion: "+idorden+idmaterial+idorden.equals(null));
        SQLiteDatabase db = db_sca.getWritableDatabase();
        String consulta= null;
        if(!idorden.equals(null)){
            consulta = "SELECT * FROM dialogo_recepcion WHERE idorden ='"+idorden+"'";
        }
        else if(!idmaterial.equals(null)){
            consulta ="SELECT * FROM dialogo_recepcion WHERE idmaterial ='"+idmaterial+"'";
        }
        if (consulta != null) {
            System.out.println("consulta: "+consulta);
            Cursor c =  db.rawQuery(consulta,null);
            ArrayList ordenes = new ArrayList<DialogoRecepcion>();
            try {
                if (c != null){
                    while (c.moveToNext()){
                        DialogoRecepcion orden = new DialogoRecepcion(context);
                        orden = orden.find(c.getInt(0));
                        ordenes.add(orden);
                        System.out.println("dialog: "+c.getInt(0));
                    }
              /*  Collections.sort(ordenes, new Comparator<OrdenCompra>() {
                    @Override
                    public int compare(OrdenCompra v1, OrdenCompra v2) {
                        return Integer.valueOf(v2.numerofolio).compareTo(Integer.valueOf(v1.numerofolio));
                    }
                });*/

                    return ordenes;
                }
                else {
                    return new ArrayList<>();
                }
            } finally {
                c.close();
                db.close();
            }
        }  else {
            return new ArrayList<>();
        }
    }

    public DialogoRecepcion find(Integer id){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM dialogo_recepcion WHERE ID = '"+id+"'", null);
        try{
            if(c != null && c.moveToFirst()){

                this.id = c.getInt(c.getColumnIndex("ID"));
                this.id_almacen = c.getString(c.getColumnIndex("idalmacen"));
                this.cantidadtotal = c.getString(c.getColumnIndex("cantidadTotal"));
                this.cantidadRS = c.getString(c.getColumnIndex("cantidadRS"));
                this.claveConcepto = c.getString(c.getColumnIndex("claveConcepto"));
                this.idcontratista = c.getString(c.getColumnIndex("idContratista"));
                this.cargo = c.getInt(c.getColumnIndex("cargo"));
                this.idorden = c.getString(c.getColumnIndex("idorden"));
                this.almacen = c.getString(c.getColumnIndex("almacen"));
                this.material = c.getString(c.getColumnIndex("material"));
                this.unidad = c.getString(c.getColumnIndex("unidad"));
                this.contratista = c.getString(c.getColumnIndex("contratista"));
                this.idmaterial = c.getString(c.getColumnIndex("idmaterial"));

            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }

    public static String getCantidadRS(Context context, Integer id){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT cantidadRS FROM dialogo_recepcion WHERE ID  = '"+id+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                return c.getString(0);
            }
            else{
                return null;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public static void remove(Context context, Integer id) {
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();

        db.execSQL("DELETE FROM dialogo_recepcion WHERE ID = '" + id + "'");
        db.close();
    }

    public Double valor(Context context, String idMaterial, String idOrden){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        System.out.println("VALORES: "+idMaterial+" "+idOrden);
        Cursor c = db.rawQuery("SELECT SUM(cantidadRS) as suma from dialogo_recepcion WHERE idorden = '" + idOrden + "' and idmaterial = '"+idMaterial+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                System.out.print("suma: "+c.getInt(0));
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

    public  Integer getCount(){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * from dialogo_recepcion", null);
        try {
            if(c!=null && c.moveToFirst()){
                System.out.print("existe: "+c.getCount());
                return c.getCount();
            }
            else{
                return 0;
            }
        } finally {
            c.close();
            db.close();
        }
    }

    public Double valorSalida(Context context, Integer idMaterial, Integer idalmacen){ // revisar aqui!!!!
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        System.out.println("VALORES SALIDA: "+idMaterial+" "+idalmacen);

        Cursor c = db.rawQuery("SELECT SUM(cantidadRS) as suma from dialogo_recepcion WHERE idalmacen = '" + idalmacen + "' and idmaterial = '"+idMaterial+"'", null);
        try {
            if(c!=null && c.moveToFirst()){
                System.out.println("idalmacen: "+idalmacen+" "+c.getDouble(0));
                return c.getDouble(0);
            }
            else{
                Cursor x = db.rawQuery("SELECT SUM(cantidadRS) as suma from dialogo_recepcion WHERE auxalmacen = '" + idalmacen + "' and idmaterial = '"+idMaterial+"'", null);
                if(x!=null && x.moveToFirst()){
                    System.out.println("aux: "+idalmacen+" "+x.getDouble(0));
                    return x.getDouble(0);
                }else {
                    return 0.0;
                }
            }
        } finally {
            c.close();
            db.close();
        }
    }

}

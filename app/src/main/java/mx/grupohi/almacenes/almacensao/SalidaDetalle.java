package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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


}

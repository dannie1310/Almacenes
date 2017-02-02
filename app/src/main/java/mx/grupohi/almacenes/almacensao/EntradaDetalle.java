package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    String fecha;
    String unidad;
    String idmaterial;


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
        Cursor c = db.rawQuery(" SELECT SUM(cantidad) as resta FROM  entradadetalle INNER JOIN entrada e ON e.id = entradadetalle.identrada WHERE e.idorden = '"+idorden+"' and e.idmaterial = '"+material+"' GROUP BY e.idmaterial", null);
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
}

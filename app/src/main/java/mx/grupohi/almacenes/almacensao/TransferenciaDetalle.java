package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        Cursor c = db.rawQuery("SELECT SUM(td.cantidad) FROM transferenciadetalle td INNER JOIN transferencia t ON t.id = td.idtransferencia WHERE td.idmaterial = '"+material+"' and t.idalmacenOrigen = '"+almacen+"' and t                                                                                                           .idobra = '"+idobra+"'", null);
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

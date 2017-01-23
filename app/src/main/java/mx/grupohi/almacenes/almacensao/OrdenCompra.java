package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Usuario on 18/01/2017.
 */

public class OrdenCompra {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer idmaterial;
    Integer iditem;
    Integer numerofolio;
    Integer preciounitario;
    Integer idorden;
    String descripcion;
    String unidad;
    String existencia;
    String razonsocial;


    OrdenCompra(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("ordenescompra", null, data) > -1;
        if (result) {

            this.idmaterial = Integer.valueOf(data.getAsInteger("idmaterial"));
            this.descripcion = data.getAsString("descripcion");
            this.iditem = Integer.valueOf(data.getAsInteger("iditem"));
            this.unidad = data.getAsString("unidad");
            this.numerofolio = Integer.valueOf(data.getAsInteger("numerofolio"));
            this.existencia = data.getAsString("existencia");
            this.preciounitario = Integer.valueOf(data.getAsInteger("preciounitario"));
            this.razonsocial = data.getAsString("razonsocial");
            this.idorden = Integer.valueOf(data.getAsInteger("idorden"));
        }
        System.out.println("result: "+ result + " i "+idorden+descripcion);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM ordenescompra");
        db.close();
    }

    ArrayList<String> getArrayListOrdenes() {

        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ordenescompra GROUP BY numerofolio ORDER BY numerofolio ASC", null);
        if (c != null && c.moveToFirst())
            try {

                if (c.getCount() == 1) {
                    data.add("#"+c.getString(c.getColumnIndex("numerofolio"))+" - "+c.getString(c.getColumnIndex("razonsocial")));
                    System.out.println("ordenescompra: "+ c.getString(0) + c.getString(2));
                } else {
                    data.add("-- Seleccione --");
                    data.add("#"+c.getString(c.getColumnIndex("numerofolio"))+" - "+c.getString(c.getColumnIndex("razonsocial")));
                    System.out.println("ordenescompra: "+ c.getString(0) + c.getString(2));
                    while (c.moveToNext()) {
                        data.add("#"+c.getString(c.getColumnIndex("numerofolio"))+" - "+c.getString(c.getColumnIndex("razonsocial")));
                        System.out.println("ordenescompra: "+ c.getString(0) + c.getString(2));
                    }
                }
            } finally {
                c.close();
                db.close();
            }

        return data;
    }
    ArrayList<String> getArrayListId() {
        ArrayList<String> data = new ArrayList<>();
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ordenescompra GROUP BY numerofolio ORDER BY numerofolio ASC", null);
        if (c != null && c.moveToFirst())
            try {
                if (c.getCount() == 1) {
                    data.add(c.getString(c.getColumnIndex("numerofolio")));
                    System.out.println("ordenescompra ID: "+ c.getString(0));
                } else {
                    data.add("0");
                    data.add(c.getString(c.getColumnIndex("numerofolio")));
                    System.out.println("ordenescompra ID: "+ c.getString(0));
                    while (c.moveToNext()) {
                        data.add(c.getString(c.getColumnIndex("numerofolio")));
                        System.out.println("ordenescompra ID: "+ c.getString(0));
                    }
                }
            } finally {
                c.close();
                db.close();
            }
        return data;
    }

    public OrdenCompra find(Integer id){
        db=db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ordenescompra WHERE ID = '"+id+"'", null);
        try{
            if(c != null && c.moveToFirst()){

                this.idmaterial = c.getInt(c.getColumnIndex("idmaterial"));
                this.iditem = c.getInt(c.getColumnIndex("iditem"));
                this.numerofolio = c.getInt(c.getColumnIndex("numerofolio"));
                this.preciounitario = c.getInt(c.getColumnIndex("preciounitario"));
                this.idorden = c.getInt(c.getColumnIndex("idorden"));
                this.descripcion = c.getString(c.getColumnIndex("descripcion"));
                this.unidad = c.getString(c.getColumnIndex("unidad"));
                this.existencia = c.getString(c.getColumnIndex("existencia"));
                this.razonsocial = c.getString(c.getColumnIndex("razonsocial"));
            }
            return this;
        }finally {
            assert c != null;
            c.close();
            db.close();
        }
    }

    public static List<OrdenCompra> getOrden(Context context, String folio){
        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        System.out.println("folio: "+folio);
        SQLiteDatabase db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ordenescompra  o INNER JOIN materiales M ON O.idmaterial = M.id_material WHERE o.numerofolio ='"+folio+"'",null);
        ArrayList  ordenes = new ArrayList<OrdenCompra>();
        try {
            if (c != null){
                while (c.moveToNext()){
                    OrdenCompra orden = new OrdenCompra(context);
                    orden = orden.find(c.getInt(0));
                    ordenes.add(orden);
                    System.out.println("ordenes: "+ordenes);
                }
                Collections.sort(ordenes, new Comparator<OrdenCompra>() {
                    @Override
                    public int compare(OrdenCompra v1, OrdenCompra v2) {
                        return Integer.valueOf(v2.numerofolio).compareTo(Integer.valueOf(v1.numerofolio));
                    }
                });

                return ordenes;
            }
            else {
                return new ArrayList<>();
            }
        } finally {
            c.close();
            db.close();
        }
    }

}

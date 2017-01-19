package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
}

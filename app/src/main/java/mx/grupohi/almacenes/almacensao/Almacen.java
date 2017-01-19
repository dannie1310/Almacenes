package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 18/01/2017.
 */

public class Almacen {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id_almacen;
    String descripcion;


    Almacen(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("almacenes", null, data) > -1;
        if (result) {
            this.id_almacen = Integer.valueOf(data.getAsInteger("id_almacen"));
            this.descripcion = data.getAsString("descripcion");

        }
        System.out.println("resultAlmacen: "+ result + " i "+id_almacen+descripcion);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM almacenes");
        db.close();
    }
}

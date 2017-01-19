package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 18/01/2017.
 */

public class Material {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer id_material;
    Integer tipomaterial;
    Integer marca;
    String descripcion;


    Material(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("materiales", null, data) > -1;
        if (result) {
            this.id_material = Integer.valueOf(data.getAsInteger("id_material"));
            this.tipomaterial = Integer.valueOf(data.getAsInteger("tipomaterial"));
            this.marca = Integer.valueOf(data.getAsInteger("marca"));
            this.descripcion = data.getAsString("descripcion");

        }
        System.out.println("materiales: "+ result + " i "+id_material+descripcion);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM materiales");
        db.close();
    }
}

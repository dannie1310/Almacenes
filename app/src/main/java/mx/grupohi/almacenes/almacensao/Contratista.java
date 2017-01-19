package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Usuario on 18/01/2017.
 */

public class Contratista {

    Context context;
    private SQLiteDatabase db;
    private DBScaSqlite db_sca;
    Integer idempresa;
    String razonsocial;

    Contratista(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("contratistas", null, data) > -1;
        if (result) {
            this.idempresa = Integer.valueOf(data.getAsInteger("idempresa"));
            this.razonsocial = data.getAsString("razonsocial");

        }
        System.out.println("contratistas: "+ result + " i "+idempresa+razonsocial);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM contratistas");
        db.close();
    }
}

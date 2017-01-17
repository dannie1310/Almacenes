package mx.grupohi.almacenes.almacensao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creado por Usuario on 16/01/2017.
 */
 class DBScaSqlite extends SQLiteOpenHelper {


    DBScaSqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static String[] queries = new String[] {
            "CREATE TABLE user (idusuario INTEGER,  nombre, TEXT, user TEXT, pass TEXT, usuarioCADECO TEXT, idbase INTEGER, idobra INTEGER)",
            "CREATE TABLE obras (ID INTEGER PRIMARY KEY AUTOINCREMENT, idobra INTEGER, nombre TEXT, idbase INTEGER, base TEXT)",
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String query: queries){
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS obras");

        for (String query: queries){
            db.execSQL(query);
        }

        db.close();
    }

    void deleteCatalogos() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM user");
        db.execSQL("DELETE FROM obras");
        db.close();
    }



}

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
            "CREATE TABLE user (idusuario INTEGER,  nombre TEXT, user TEXT, pass TEXT, usuarioCADECO TEXT, idobraactiva INTEGER)",
            "CREATE TABLE obras (ID INTEGER PRIMARY KEY AUTOINCREMENT, idobra INTEGER, nombre TEXT, idbase INTEGER, base TEXT)",
            "CREATE TABLE ordenescompra (ID INTEGER PRIMARY KEY AUTOINCREMENT, descripcion TEXT, unidad TEXT, existencia DOUBLE, idmaterial INTEGER, iditem INTEGER, razonsocial TEXT, numerofolio INTEGER, preciounitario INTEGER, idorden INTEGER)",
            "CREATE TABLE almacenes (id_almacen INTEGER, descripcion TEXT)",
            "CREATE TABLE materiales (id_material INTEGER, tipomaterial INTEGER, marca INTEGER, descripcion TEXT)",
            "CREATE TABLE material_almacen(ID INTEGER PRIMARY KEY AUTOINCREMENT, id_material INTEGER, id_almacen INTEGER, unidad TEXT, id_obra INTEGER, cantidad INTEGER)",
            "CREATE TABLE contratistas (idempresa INTEGER, razonsocial TEXT)",
            "CREATE TABLE dialogo_recepcion (ID INTEGER PRIMARY KEY AUTOINCREMENT, cantidadTotal TEXT, cantidadRS TEXT, idalmacen TEXT, claveConcepto TEXT, idContratista TEXT, cargo INTEGER, idorden TEXT, almacen TEXT, material TEXT, unidad TEXT, contratista TEXT, idmaterial TEXT)",
            "CREATE TABLE entrada (ID INTEGER PRIMARY KEY AUTOINCREMENT,  idorden TEXT, idmaterial TEXT, referencia TEXT, observacion TEXT, fecha VARCHAR(8))",
            "CREATE TABLE entradadetalle (ID INTEGER PRIMARY KEY AUTOINCREMENT, identrada INTEGER, cantidad DOUBLE, idalmacen TEXT, claveConcepto TEXT, idContratista TEXT, cargo INTEGER,  unidad TEXT, idmaterial TEXT, fecha DATETIME DEFAULT CURRENT_TIMESTAMP)",
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
        db.execSQL("DROP TABLE IF EXISTS ordenescompra");
        db.execSQL("DROP TABLE IF EXISTS almacenes");
        db.execSQL("DROP TABLE IF EXISTS materiales");
        db.execSQL("DROP TABLE IF EXISTS material_almacen");
        db.execSQL("DROP TABLE IF EXISTS contratistas");
        db.execSQL("DROP TABLE IF EXISTS dialogo_recepcion");
        db.execSQL("DROP TABLE IF EXISTS entrada");
        db.execSQL("DROP TABLE IF EXISTS entradadetalle");

        for (String query: queries){
            db.execSQL(query);
        }

        db.close();
    }

    void deleteCatalogos() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM user");
        db.execSQL("DELETE FROM obras");
        db.execSQL("DELETE FROM ordenescompra");
        db.execSQL("DELETE FROM almacenes");
        db.execSQL("DELETE FROM materiales");
        db.execSQL("DELETE FROM material_almacen");
        db.execSQL("DELETE FROM contratistas");
        db.execSQL("DELETE FROM dialogo_recepcion");
        db.execSQL("DELETE FROM entrada");
        db.execSQL("DELETE FROM entradadetalle");

        db.close();
    }

    void deleteCatalogosObras() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM ordenescompra");
        db.execSQL("DELETE FROM almacenes");
        db.execSQL("DELETE FROM materiales");
        db.execSQL("DELETE FROM material_almacen");
        db.execSQL("DELETE FROM contratistas");
        db.execSQL("DELETE FROM dialogo_recepcion");
        db.execSQL("DELETE FROM entrada");
        db.execSQL("DELETE FROM entradadetalle");

        db.close();
    }


}

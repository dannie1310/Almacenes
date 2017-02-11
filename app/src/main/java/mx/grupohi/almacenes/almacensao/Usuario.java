package mx.grupohi.almacenes.almacensao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Creado por Usuario on 16/01/2017.
 */

class Usuario {

    Integer idUsuario;
    String usr;
    String pass;
    String nombre;
    String usuarioCADECO;
    Integer obraActiva;

    private Context context;

    private SQLiteDatabase db;
    private DBScaSqlite db_sca;

    Usuario(Context context) {
        this.context = context;
        db_sca = new DBScaSqlite(context, "sca", null, 1);
    }

    boolean create(ContentValues data) {
        db = db_sca.getWritableDatabase();
        Boolean result = db.insert("user", null, data) > -1;
        if (result) {
            this.idUsuario = Integer.valueOf(data.getAsString("idusuario"));
            this.nombre = data.getAsString("nombre");
            this.usr = data.getAsString("user");
            this.pass = data.getAsString("pass");
            this.usuarioCADECO = data.getAsString("usuarioCADECO");
        }
        System.out.println("result: "+ result + " i "+idUsuario+nombre);
        db.close();
        return result;
    }

    void destroy() {
        db = db_sca.getWritableDatabase();
        db.execSQL("DELETE FROM user");
        db.close();
    }

    boolean isAuth() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user LIMIT 1", null);
        try {
            return c != null && c.moveToFirst();
        } finally {
            c.close();
            db.close();
        }
    }

    public Integer getId() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user LIMIT 1", null);
        try {
            if(c != null && c.moveToFirst()) {
                this.idUsuario = c.getInt(0);
            }
            return this.idUsuario;
        } finally {
            c.close();
            db.close();
        }
    }

    Usuario getUsuario() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user LIMIT 1", null);
        try {
            if(c != null && c.moveToFirst()) {
                this.idUsuario = c.getInt(c.getColumnIndex("idusuario"));
                this.nombre = c.getString(c.getColumnIndex("nombre"));
                this.usr = c.getString(c.getColumnIndex("user"));
                this.pass = c.getString(c.getColumnIndex("pass"));
                this.usuarioCADECO = c.getString(c.getColumnIndex("usuarioCADECO"));
                this.obraActiva = c.getInt(c.getColumnIndex("idobraactiva"));

                return this;
            } else {
                return null;
            }
        }finally {
            c.close();
            db.close();
        }
    }

    String getNombre(){
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT nombre FROM user LIMIT 1", null);
        try {
            if(c!=null && c.moveToFirst()){
                this.nombre =  c.getString(0);
            }
            return this.nombre;
        } finally {
            c.close();
            db.close();
        }
    }

    boolean get() {
        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM user", null);
        try{
            return c != null && c.moveToFirst();
        } finally {
            c.close();
            db.close();
        }
    }


    static boolean update(String obraActiva,String id,  Context context) {
        boolean resp=false;
        ContentValues data = new ContentValues();

        DBScaSqlite db_sca = new DBScaSqlite(context, "sca", null, 1);
        SQLiteDatabase db = db_sca.getWritableDatabase();

        try {
            data.put("idobraactiva", obraActiva);

            db.update("user", data, "idusuario = '" + id + "'", null);
            resp = true;
        }finally {
            db.close();
        }

        return resp;
    }

    String getObraActiva(){

        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT  o.nombre FROM obras as o INNER JOIN user as u on u.idobraactiva = o.ID WHERE u.idobraactiva=o.ID", null);

        try {
            if(c!=null && c.moveToFirst() && c.getString(0) != String.valueOf(0)){
               return  c.getString(0);
            }else {
                return null;
            }
        } finally {
            c.close();
            db.close();

        }
    }

    Integer getIdObra(){

        db = db_sca.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT  o.idobra FROM obras as o INNER JOIN user as u on u.idobraactiva = o.ID WHERE u.idobraactiva=o.ID", null);

        try {
            if(c!=null && c.moveToFirst() && c.getString(0) != String.valueOf(0)){
                return  c.getInt(0);
            }else {
                return null;
            }
        } finally {
            c.close();
            db.close();

        }
    }
}


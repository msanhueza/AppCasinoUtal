package com.example.casinoutal.componentes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Esta clase es una extensión de SQLiteOpenHelper y se utiliza para manejar la base de datos local de la aplicación.
 */
public class BaseSQLiteCasinoUtalca extends SQLiteOpenHelper {
	private static final String CREATE_TABLE_ET = "CREATE TABLE entrada(nombre TEXT)";
	private static final String CREATE_TABLE_PF = "CREATE TABLE platofondo(nombre TEXT)"; 
	private static final String CREATE_TABLE_AC = "CREATE TABLE acompanamiento(nombre TEXT)";
	private static final String CREATE_TABLE_EN = "CREATE TABLE ensalada(nombre TEXT)";
	private static final String CREATE_TABLE_PO = "CREATE TABLE postre(nombre TEXT)";
	private static final String CREATE_TABLE_MENU = "CREATE TABLE menucreado(tipo TEXT, fecha TEXT)";
	private static final String CREATE_TABLE_USER = "CREATE TABLE user(id INTEGER, mail TEXT, nombre TEXT, is_admin TEXT)";

	/**
	 * Contructor de la clase.
	 * @param context Contexto de la aplicación. 
	 * @param name nombre de la base de datos.
	 * @param factory se encarga de almacenar los resultados de las consultas a la BD 
	 * (en este caso siempre se enviará null porque utilizaremos el Cursor por defecto de SQLite)
	 * @param version versión de la base de datos.
	 */
	public BaseSQLiteCasinoUtalca(Context context, String name, CursorFactory  factory, int version){    
		//TODO CONSTRUCTOR
		super (context, name, factory, version);
	}   
	
	/**
	 * Función que se llamará automáticamente en caso de que la base de datos no se encuentre creada en el dispositivo. 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO CREAR
		db.execSQL(CREATE_TABLE_ET);
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(CREATE_TABLE_PF);
		db.execSQL(CREATE_TABLE_AC);
		db.execSQL(CREATE_TABLE_EN);
		db.execSQL(CREATE_TABLE_PO);
		db.execSQL(CREATE_TABLE_MENU);
	}

	/**
	 * Función que se llamará automaticamente en caso de que las versiones de la base de datos sean distintas para que 
	 * los datos sean actualizados a la ultima versión
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO ACTUALIZAR 
		db.execSQL("DROP TABLE entrada");
		db.execSQL("DROP TABLE user");
		db.execSQL("DROP TABLE platofondo");
		db.execSQL("DROP TABLE acompanamiento");
		db.execSQL("DROP TABLE ensalada");
		db.execSQL("DROP TABLE postre");
		db.execSQL("DROP TABLE menucreado");
	    onCreate(db);

	}
}

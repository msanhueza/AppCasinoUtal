package com.example.casinoutal.autenticacion;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;
import com.example.casinoutal.crear_menu.SelectorCrearMenu;
import com.example.casinoutal.estadisticas.MenuEstadisticas;
import com.example.casinoutal.ver_menu.SelectorVerMenu;

/**
 * Actividad encargada de la lógica de las opciones del menú principal para un administrador
 * crear menú, ver menú, estadisticas, cambiar contraseña, cerrar sesión
 */
public class HomeAdmin extends ActionBarActivity{
	BaseSQLiteCasinoUtalca bbdd; // base de datos interna
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_admin);
		String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		if(db!=null){
			Cursor c = db.rawQuery("SELECT * FROM user", null);
			if(c.moveToFirst()){
				String nombre = c.getString(2); //obtenemos el nombre del usuario que inicio sesión
				TextView saludo = (TextView) findViewById(R.id.bienvenida);
				saludo.setText("Bienvenido: " + nombre.toUpperCase()); // seteamos el mensaje de bienvenida en las opciones de administrador
			}
		}
		db.close();
	    
		Button crearMenu = (Button) findViewById(R.id.btn_crear_menu);
		crearMenu.setOnClickListener(new OnClickListener() { //listener para el evento crear menú
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAdmin.this, SelectorCrearMenu.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
	
		Button verMenu = (Button) findViewById(R.id.btn_ver_menu);
		verMenu.setOnClickListener(new OnClickListener() { //listener para el evento de ver menú
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAdmin.this, SelectorVerMenu.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button cambiarContrasena = (Button) findViewById(R.id.btn_cambiar_contrasena);
		cambiarContrasena.setOnClickListener(new OnClickListener() { //listener para el evento cambiar contraseña
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAdmin.this, CambiarContrasena.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button estad = (Button) findViewById(R.id.btn_estadisticas);
		estad.setOnClickListener(new OnClickListener() { //listener para el evento ver menú
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAdmin.this, MenuEstadisticas.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button cerrarSesion = (Button) findViewById(R.id.btn_cerrar_sesion);
		cerrarSesion.setOnClickListener(new OnClickListener() { //listener para el evento cerrar sesión
			@Override
			public void onClick(View arg0) {
				String dbInterna = getString(R.string.db_interna);
				bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna , null, 1);
    			SQLiteDatabase db = bbdd.getWritableDatabase();
				if(db!=null){
					db.execSQL("DELETE FROM user"); // se elimina el usuario registrado en la db interna
				}
				db.close();
				Intent intent = new Intent(HomeAdmin.this, Autenticacion.class); // se envia a la activity de iniciar sesión
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}

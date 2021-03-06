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
import com.example.casinoutal.componentes.Encuesta;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;
import com.example.casinoutal.estadisticas.MenuEstadisticas;
import com.example.casinoutal.ver_menu.SelectorVerMenu;

/**
 * Actividad encargada de la l�gica de las opciones del men� principal para un usuario
 * crear men�, ver men�, encuesta, estadisticas, cambiar contrase�a, cerrar sesi�n
 */
public class HomeUser extends ActionBarActivity{
	BaseSQLiteCasinoUtalca bbdd; // base de datos interna
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_user);		
		String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		if(db!=null){
			Cursor c = db.rawQuery("SELECT * FROM user", null);
			if(c.moveToFirst()){
				String nombre = c.getString(2); //obtenemos el nombre del usuario que inicio sesi�n
				TextView saludo = (TextView) findViewById(R.id.bienvenida_user);
				saludo.setText("Bienvenido: "+nombre.toUpperCase()); // seteamos el mensaje de bienvenida en las opciones de usuario
			}
		}
		db.close();
		
		Button verMenu = (Button) findViewById(R.id.btn_ver_menu_user);
		verMenu.setOnClickListener(new OnClickListener() { //listener para el evento ver men�
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeUser.this, SelectorVerMenu.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button encuesta = (Button) findViewById(R.id.btn_encuesta);
		encuesta.setOnClickListener(new OnClickListener() { //listener para el evento encuesta
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeUser.this, Encuesta.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
	
		Button estad = (Button) findViewById(R.id.btn_estadisticas_user);
		estad.setOnClickListener(new OnClickListener() { //listener para el evento ver men�
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeUser.this, MenuEstadisticas.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button cambiarContrasena = (Button) findViewById(R.id.btn_cambiar_contrasena_user);
		cambiarContrasena.setOnClickListener(new OnClickListener() { //listener para el evento cambiar contrase�a
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeUser.this, CambiarContrasena.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button cerrarSesion = (Button) findViewById(R.id.btn_cerrar_sesion_user);
		cerrarSesion.setOnClickListener(new OnClickListener() { //listener para el evento cerrar sesi�n
			@Override
			public void onClick(View arg0) {
				String dbInterna = getString(R.string.db_interna);
				bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna , null, 1);
    			SQLiteDatabase db = bbdd.getWritableDatabase();
				if(db!=null){
					db.execSQL("DELETE FROM user"); // se elimina el usuario registrado en la db interna
				}
				db.close();
				Intent intent = new Intent(HomeUser.this, Autenticacion.class); // se envia a la activity de iniciar sesi�n
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

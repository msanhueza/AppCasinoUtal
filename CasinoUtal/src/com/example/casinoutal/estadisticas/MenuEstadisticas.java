package com.example.casinoutal.estadisticas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.casinoutal.R;

/**
 * Actividad encargada de la l�gica de las opciones del men� principal para un administrador
 * crear men�, ver men�, estadisticas, cambiar contrase�a, cerrar sesi�n
 */
public class MenuEstadisticas extends ActionBarActivity{
	Bundle bundle = new Bundle();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_estadisticas);
		
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Estad�sticas");

		
		Button mejorDia = (Button) findViewById(R.id.btn_mejor_dia); // boton mejor del dia
		mejorDia.setOnClickListener(new OnClickListener() { //listener
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuEstadisticas.this, VerEstadisticasLista.class);
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
				String hoy = df2.format(c.getTime());
				bundle.putString("busqueda", "1"); // busqueda s�lo por un d�a
				bundle.putString("fecha_inicio", hoy);
				bundle.putString("fecha_termino", "");
				bundle.putString("limite","1");
				bundle.putString("mes","");
				bundle.putString("titulo" , "Lo mejor del d�a");
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
	
		Button fiveMes = (Button) findViewById(R.id.btn_five_mes); // boton top 5 del mes
		fiveMes.setOnClickListener(new OnClickListener() { //listener
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuEstadisticas.this, SelectorMesTopFive.class);
				bundle.putString("busqueda", "3"); // busqueda s�lo por un d�a
				bundle.putString("titulo", "Top 5 Mes");
				bundle.putString("fecha_inicio", "");
				bundle.putString("fecha_termino", "");
				bundle.putString("limite","5");
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button fivePeriodo = (Button) findViewById(R.id.btn_five_periodo); // boton 5 mejores de un periodo
		fivePeriodo.setOnClickListener(new OnClickListener() { //listener
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuEstadisticas.this, SelectorFechasTopFive.class);
				bundle.putString("busqueda", "2"); // busqueda s�lo por un d�a
				bundle.putString("limite","5");
				bundle.putString("mes","");
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button resumen = (Button) findViewById(R.id.btn_resumen_mes); // boton resumen del mes
		resumen.setOnClickListener(new OnClickListener() { //listener
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuEstadisticas.this, SelectorMesTopFive.class);
				bundle.putString("busqueda", "3"); // busqueda s�lo por un d�a
				bundle.putString("titulo", "Resumen del Mes");
				bundle.putString("limite","9999");
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		Button resumen_encuesta = (Button) findViewById(R.id.btn_resumen_mes_encuesta); // boton resumen mes encuesta
		resumen_encuesta.setOnClickListener(new OnClickListener() { //listener
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuEstadisticas.this, SelectorMesEncuesta.class);
				bundle.putString("busqueda", "3"); // busqueda s�lo por un d�a
				bundle.putString("limite","9999");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});		
	}
	
	/**
	 * metodo que activa el boton atras
	 */
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
	}
	
	/**
	 * metodo que permite volver a la activity anterior 
	 */	
	@Override
	public Intent getSupportParentActivityIntent() {
		this.finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		return super.getSupportParentActivityIntent();
	}
}

package com.example.casinoutal.estadisticas;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.casinoutal.R;

public class SelectorFechasTopFive extends ActionBarActivity {
	Bundle bundle = new Bundle();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_periodo_dias);
		
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Seleccione Periodo");
	    
	    bundle = this.getIntent().getExtras();
	    DatePicker desde  = (DatePicker) findViewById(R.id.selector_fecha_desde);
		customDatePicker(desde);
		DatePicker hasta  = (DatePicker) findViewById(R.id.selector_fecha_hasta);
		customDatePicker(hasta);
	}

	/**
	 * metodo que permite customizar el datapicker con el formato de fuente del telefono
	 * @param datePicker actual
	 */
	private void customDatePicker(DatePicker datePicker) {
		String[] ids = {"day","month","year"};
		ViewGroup childpicker;
		EditText actualET;
		for (String s : ids) {
			childpicker = (ViewGroup)datePicker.findViewById(Resources.getSystem().getIdentifier(s, "id", "android"));
			actualET = (EditText)childpicker.getChildAt(1);// year widget
			if(actualET==null){
				actualET = (EditText)childpicker.getChildAt(0);// year widget
			}
			if(actualET!=null){
				actualET.setTextSize(16);
				actualET.setTextColor(Color.BLACK);
				actualET.setTypeface(Typeface.DEFAULT);
			}
		}
	}	
	
	/**
	 * metodo que pasar a la activity para ver las estadisticas
	 * @param v es la vista actual
	 */
	public void onClickSgte(View v){
		DatePicker desde = (DatePicker) findViewById(R.id.selector_fecha_desde);
		String fechaDesde = desde.getYear()+"-"+desde.getMonth()+"-"+desde.getDayOfMonth();
		DatePicker hasta = (DatePicker) findViewById(R.id.selector_fecha_hasta);
		String fechaHasta = hasta.getYear()+"-"+hasta.getMonth()+"-"+hasta.getDayOfMonth();
		if(fechaDesde.compareTo(fechaHasta)<=0){
			Intent intent = new Intent(SelectorFechasTopFive.this, VerEstadisticasLista.class);
			bundle.putString("fecha_inicio", fechaDesde);
			bundle.putString("fecha_termino", fechaHasta);
			bundle.putString("titulo" , "Top 5 por Periodo");
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		else{
			generarToast("periodo de fechas incorrecta");
		}
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
	
	/**
	* metodo que permite generar un mensaje informativo para una determinada acción (se muestra y luego desaparece)
	* @param mensaje indica el mensaje que se desea mostrar
	*/
	public void generarToast(CharSequence mensaje){
		   Context context = getApplicationContext();
		   CharSequence text = mensaje;
		   int duration = Toast.LENGTH_SHORT;
		   Toast toast = Toast.makeText(context, text, duration);
		   toast.setGravity(Gravity.CENTER, 0, 0);
		   toast.show();
	}
}

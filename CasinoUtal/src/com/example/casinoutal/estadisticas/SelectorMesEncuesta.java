package com.example.casinoutal.estadisticas;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.casinoutal.R;

public class SelectorMesEncuesta extends ActionBarActivity {
	Bundle bundle = new Bundle();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_mes_mean_estadisticas_encuesta);
		
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Seleccione un Mes");
	    
	    bundle = this.getIntent().getExtras();
	    
	    final String[] datos = new String[]{"<Seleccione Mes>","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciemre"};
	    ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.elemento_spinner, datos);
	    Spinner periodo = (Spinner) findViewById(R.id.seleccionMes_mean);
	    periodo.setAdapter(adaptador);
	}

	/**
	 * metodo que pasar a la activity para ver las estadisticas de encuesta
	 * @param v es la vista actual
	 */	
	public void onClickSgte(View v){

		Spinner sp = (Spinner) findViewById(R.id.seleccionMes_mean);
		String mes = (String) sp.getSelectedItem();
		if(!mes.equals("<Seleccione Mes>")){
			Intent intent = new Intent(SelectorMesEncuesta.this, VerListaEstadisticaEncuesta.class);
			HashMap<String, String> meses = new HashMap<>(); // contiene los meses
			meses.put("Enero","1");
			meses.put("Febrero","2");
			meses.put("Marzo","3");
			meses.put("Abril","4");
			meses.put("Mayo","5");
			meses.put("Junio","6");
			meses.put("Julio","7");
			meses.put("Agosto","8");
			meses.put("Septiembre","9");
			meses.put( "Octubre","10");
			meses.put( "Noviembre","11");
			meses.put( "Diciemre", "12");
			bundle.putString("mes",meses.get(mes));
			intent.putExtras(bundle);
			startActivity(intent);
		}
		else{
			generarToast("Seleccione un mes");
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

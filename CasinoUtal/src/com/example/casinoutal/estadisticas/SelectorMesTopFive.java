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

public class SelectorMesTopFive extends ActionBarActivity {
	Bundle bundle = new Bundle();
	String titulo = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_mes_top_five_estadisticas);
		
		bundle = this.getIntent().getExtras();
		
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    titulo = bundle.getString("titulo");
	    actionBar.setTitle(titulo);
	    
	    
	    
	    final String[] datos = new String[]{"<Seleccione Mes>","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciemre"};
	    ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.elemento_spinner, datos);
	    Spinner periodo = (Spinner) findViewById(R.id.seleccionMes);
	    periodo.setAdapter(adaptador);
	}

	/**
	 * metodo que pasa a la activity para ver la lista de estadisticas
	 * @param v es la vista actual
	 */	
	public void onClickSgte(View v){

		Spinner sp = (Spinner) findViewById(R.id.seleccionMes);
		String mes = (String) sp.getSelectedItem();
		if(!mes.equals("<Seleccione Mes>")){
			Intent intent = new Intent(SelectorMesTopFive.this, VerEstadisticasLista.class);
			HashMap<String, String> meses = new HashMap<>();
			meses.put("Enero","0");
			meses.put("Febrero","1");
			meses.put("Marzo","2");
			meses.put("Abril","3");
			meses.put("Mayo","4");
			meses.put("Junio","5");
			meses.put("Julio","6");
			meses.put("Agosto","7");
			meses.put("Septiembre","8");
			meses.put( "Octubre","9");
			meses.put( "Noviembre","10");
			meses.put( "Diciembre", "11");
			bundle.putString("mes",meses.get(mes));
			bundle.putString("titulo", titulo);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
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

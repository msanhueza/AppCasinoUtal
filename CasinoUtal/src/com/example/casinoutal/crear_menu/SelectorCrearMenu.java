package com.example.casinoutal.crear_menu;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;

public class SelectorCrearMenu extends ActionBarActivity {
	BaseSQLiteCasinoUtalca bbdd; // Variable global que permite utilizar la Base de Datos Local SQLite.
	private ProgressDialog pDialog; // Variable global que permite generar dialogos post-acción. 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_crear_menu);
		String dbInterna = getString(R.string.db_interna);
		// Verifica y asegura que no existan valores en la BD Local que pudieran generar error.
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		if(db != null){
			db.execSQL("DELETE FROM entrada;");
			db.execSQL("DELETE FROM platofondo;");
			db.execSQL("DELETE FROM acompanamiento;");
			db.execSQL("DELETE FROM ensalada;");
			db.execSQL("DELETE FROM postre;");
		}
		db.close();
		// Establece los valores para la interfaz
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Crear menú");
	    DatePicker datePicker  = (DatePicker) findViewById(R.id.selector_fecha_crear);
		customDatePicker(datePicker);
		cargarMenus();
	}
	
	@Override
	public Intent getSupportParentActivityIntent() {
		this.finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		return super.getSupportParentActivityIntent();
	}
	
	/**
	* Método que crea una instancia para descargar la información de la base de datos. 
	*/	
	public void cargarMenus(){
		Descargar desc = new Descargar();
		desc.execute();
	}
	
	/**
	* Método que permite editar las propiedades del DatePicker, ya sea
	* configurando el tamaño y color de la fuente, tipo de carátula, por nombrar algunos. 
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
	* Método que permite dar uso a cada uno de los componentes del layout.
	* Además, de retringir la creación de un nuevo menu para un mismo día.
	*/	
	public void onClickCrearMenu(View v){
		String fecha, menu="";
		DatePicker dp = (DatePicker) findViewById(R.id.selector_fecha_crear);
		fecha = dp.getYear()+"/"+dp.getMonth()+"/"+dp.getDayOfMonth();
		if(findViewById(R.id.btnBasico).isPressed()){
			menu = "1";
		}
		else if(findViewById(R.id.btnHipocalorico).isPressed()){
			menu="2";
		}
		else if(findViewById(R.id.btnVegetariano).isPressed()){
			menu="3";
		}
		else if(findViewById(R.id.btnEjecutivo).isPressed()){
			menu="4";
		}
		String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM menucreado WHERE '"+parsearFecha(fecha)+"'=fecha AND '"+menu+"'=tipo" , null);
		if(c.moveToFirst()){
			generarToast("Ya existe un menú creado para este día");
		}
		else{
			Intent intent = new Intent(SelectorCrearMenu.this, Entrada.class);
			intent.putExtra("fecha", fecha);
			intent.putExtra("menu", menu);
			if(db != null){
				db.execSQL("DELETE FROM entrada;");
				db.execSQL("DELETE FROM platofondo;");
				db.execSQL("DELETE FROM acompanamiento;");
				db.execSQL("DELETE FROM ensalada;");
				db.execSQL("DELETE FROM postre;");
			}
			startActivity(intent);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		db.close();
	}

	/**
	* Método que permite generar un mensaje informativo para una determinada acción 
	* (se muestra y luego desaparece)
	* @param Mensaje indica el mensaje que se desea mostrar
	*/	
	public void generarToast(CharSequence mensaje){
		   Context context = getApplicationContext();
		   CharSequence text = mensaje;
		   int duration = Toast.LENGTH_SHORT;
		   Toast toast = Toast.makeText(context, text, duration);
		   toast.setGravity(Gravity.CENTER, 0, 0);
		   toast.show();
	}
	
	/**
	 * Método que permite parsear la fecha que es obtenida desde un 
	 * Gson (Servicio Gson) para su posterior uso.
	 * @return La fecha respectivamente. 
	 */	
	public String parsearFecha(String str){
		String [] values = str.split("/");
		String anio = values[0];
		String mes = (values[1].length() == 1) ? ("0"+values[1]): (values[1]);
		String dia = (values[2].length() == 1) ? ("0"+values[2]): (values[2]);
		return anio+"-"+mes+"-"+dia;
	}
	
    /**
     * metodo que activa el boton hacia atras	
     */
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
	}
	
	/**
	 * Método que permite mostrar eventualmente todo los menus que se 
	 * encuentren actualmente asociados y almacenados a la fecha seleccionada.
	 * Para ello se comunica con el servicio correspondiente (listar_menu.php),
	 * para finalmente descargarlos.
	 * @return verdadero o falso dependiendo si la acción fue ejecutada con éxito. 
	 */	
	private class Descargar extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);
		
		public Descargar(){
			
		}
		
		@Override
        protected Boolean doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get;
			get =new HttpGet("http://"+ip+"/casino_utalca/modelo/listar_menu.php");
		    get.setHeader("content-type", "application/json");
		    try
		    {
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        SQLiteDatabase db = bbdd.getWritableDatabase();
		        db.execSQL("DELETE FROM menucreado;");
		        for(int i=0; i<respJSON.length(); i++)
		        {
		        	JSONObject obj = respJSON.getJSONObject(i);
		            String fecha = obj.getString("fecha_menu");
		            String tipo = obj.getString("tipo_menu");
		            db.execSQL("INSERT INTO menucreado (tipo, fecha) VALUES('"+tipo+"', '"+fecha+"')");
		            publishProgress(i*4);
		        }
		        db.close();
		        publishProgress(100);
		        return true;
		    }
		    catch(Exception ex){}
		    return false;
        }
		
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	 
	        pDialog.setProgress(progreso);
		}
		
		
		@Override
        protected void onPreExecute() {
			pDialog = new ProgressDialog(SelectorCrearMenu.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Descargando datos...");
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
		
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
		}
	}

}

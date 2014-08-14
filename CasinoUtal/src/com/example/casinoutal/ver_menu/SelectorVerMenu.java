package com.example.casinoutal.ver_menu;

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

/**
 * Esta clase verifica las fechas para las que hay menús creados y da el pase al usuario
 * para que pueda ver las alternativas de menu disponible
 */
public class SelectorVerMenu extends ActionBarActivity {
	BaseSQLiteCasinoUtalca bbdd;
	private ProgressDialog pDialog;
	
	/**
	 * Carga y prepara la vista para seleccionar el día y el menu a ver.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selector_ver_menu);
		String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna , null, 1);
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Ver menú");
	    DatePicker datePicker  = (DatePicker) findViewById(R.id.selector_fecha_ver_user);
		customDatePicker(datePicker);
		cargarMenus();
	}
	
	/**
	 * Función para volver atrás desde el icono de la aplicación
	 * @return
	 */
	@Override
	public Intent getSupportParentActivityIntent() {
		this.finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		return super.getSupportParentActivityIntent();
	}
	
	/**
	 * Cambia el estilo de un datePicker
	 * @param datePicker El componenente al que se le cambiará el estilo
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
	 * Invoca a la clase que descarga los tipos de menu disponibles (tarea que se realiza en segundo plano)
	 */
	public void cargarMenus(){
		Descargar desc = new Descargar();
		desc.execute();
	}
	
	
	/**
	 * Captura el touch realizado en alguno de los botones. Determina cual fue tocado, la fecha seleccionada
	 * y pasa a la Activity para mostrar el detalle correspondiente-
	 * @param v
	 */
	public void onClickVerMenu(View v){
		String fecha, menu = "";
		DatePicker dp = (DatePicker) findViewById(R.id.selector_fecha_ver_user);
		fecha = dp.getYear()+"/"+dp.getMonth()+"/"+dp.getDayOfMonth();
		if(findViewById(R.id.btnBasicoVer).isPressed()){
			menu = "1";
		}
		else if(findViewById(R.id.btnHipocaloricoVer).isPressed()){
			menu="2";
		}
		else if(findViewById(R.id.btnVegetarianoVer).isPressed()){
			menu="3";
		}
		else if(findViewById(R.id.btnEjecutivoVer).isPressed()){
			menu="4";
		}
		//verifica que exista un menu creado para ese día y fecha.
		String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna , null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM menucreado WHERE '"+parsearFecha(fecha)+"'=fecha AND '"+menu+"'=tipo" , null);
		if(c.moveToFirst()){
			if(!menu.equals("5")) {
				Intent intent = new Intent(SelectorVerMenu.this, VerMenu.class);
				intent.putExtra("fecha", fecha);
				intent.putExtra("menu", menu);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		}
		else{
			generarToast("No hay menús creados para este día");
		}
		db.close();
	}
	
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
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
	
	/**
	 * formatea la fecha al siguente estilo AAAA-MM-DD 
	 * @param fecha Fecha a formatear
	 * @return la fecha con el formato deseado
	 */
	public String parsearFecha(String fecha){
		String [] values = fecha.split("/");
		String anio = values[0];
		String mes = (values[1].length() == 1) ? ("0"+values[1]): (values[1]);
		String dia = (values[2].length() == 1) ? ("0"+values[2]): (values[2]);
		return anio+"-"+mes+"-"+dia;
	}

	/**
	 * Clase que se encargada de descargar los tipos de menus disponibles. Los almacena en la base de dato. 
	 */
	private class Descargar extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);
		
		public Descargar(){	
		}
		
		/**
		 * Se comunica con el servidor y descarga la info y la almacena en la BBDD local.
		 * @return
		 */
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
		
		/**
		 * informa del progreso al JDialog. para que este lo muestre por pantalla
		 */
		@Override
	    protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	 
	        pDialog.setProgress(progreso);
		}
		
		/**
		 * Prepara el JDialog antes de comenzar la descarga
		 */
		@Override
        protected void onPreExecute() {
			pDialog = new ProgressDialog(SelectorVerMenu.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Descargando datos...");
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
		
		/**
		 * Una vez finalizada la descarga cierra el JDialog
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
		}
	}
}

package com.example.casinoutal.estadisticas;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.AdaptadorListaComentariosEstadisticas;
import com.example.casinoutal.componentes.ObjetoComentarioEstadistica;

public class ListaComentarios extends ActionBarActivity {
	static Bundle bundle;
	private ProgressDialog pDialog;
	private ArrayList <ObjetoComentarioEstadistica> comentarios = new ArrayList<>();
	//static ListView lista_comentarios;
	private static AdaptadorListaComentariosEstadisticas adaptadorComentarios; // Comentarios
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ver_comentarios);
		
		bundle = this.getIntent().getExtras();
		final ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle(bundle.getString("nombre_elemento"));
	    
		adaptadorComentarios = new AdaptadorListaComentariosEstadisticas(this, comentarios);
		ListView lc = (ListView) findViewById(R.id.listview_comentarios);
		lc.setAdapter(adaptadorComentarios);
	    Descargar d = new Descargar();
	    d.execute();
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
	 * metodo que activa el boton atras
	 */
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
	}
	
	/**
	 * Clase encargada de descargar los comentarios
	 *
	 */
	private class Descargar extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);
		
		public Descargar(){	
		}
		
		/**
		 * Se conecta con el servidor y se encarga de cargar los elementos en los adaptadores de cada lista
		 * @param params
		 * @return
		 */
		@Override
        protected Boolean doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get;
			// construyo la direccion http con los elementos seleccionados en otras activities
			String asd = "http://"+ip+"/casino_utalca/modelo/listar_valoracion_fecha_mes_comentario.php?" +
					"busqueda="+bundle.getString("busqueda")+
					"&mes="+bundle.getString("mes")+
					"&limite="+bundle.getString("limite")+
					"&fecha_inicio="+bundle.getString("fecha_inicio")+
					"&fecha_termino="+bundle.getString("fecha_termino")+
					"&tipo_elemento="+bundle.getInt("tipo_componente")+
					"&id_elemento="+bundle.getInt("id_elemento");
			System.out.println(asd);
			get = new HttpGet(asd);
		    get.setHeader("content-type", "application/json");
		    try {	
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++){
		        	JSONObject obj = respJSON.getJSONObject(i);
		        	
		        	String nombre = obj.getString("nombre");
		        	nombre = nombre +" "+ obj.getString("apellidos");
		        	String comentario = obj.getString("comentario");
		        	String fecha = obj.getString("fecha");

		        	comentarios.add(new ObjetoComentarioEstadistica(nombre,fecha,comentario));
		        }
		        // si las listas no tienen elementos se crea un elemento que notifique que no hay alternativas disponibles.
		        if(comentarios.isEmpty()){
		        	comentarios.add(new ObjetoComentarioEstadistica("", "", "No hay comentarios registrados"));
		        	System.out.println("Comentarios esta vacio");
		        }
		        
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
			pDialog = new ProgressDialog(ListaComentarios.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Descargando comentarios...");
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

		/**
		 * Una vez finalizada la descarga cierra el JDialog y notifica a los adaptadores que se actualizaron sus datos
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			adaptadorComentarios.notifyDataSetChanged();
		}
	}
}

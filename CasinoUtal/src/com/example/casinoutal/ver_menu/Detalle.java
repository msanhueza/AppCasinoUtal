package com.example.casinoutal.ver_menu;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.AdaptadorListaComentarios;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;
import com.example.casinoutal.componentes.ObjetoComentario;

/**
 * Actividad encargada de mostrar las valoraciones y comentarios para un determinado componente de un menú
 */
public class Detalle extends ActionBarActivity {
	private ArrayList <ObjetoComentario> comentarios = new ArrayList<>();
	private static AdaptadorListaComentarios adaptadorListaComentarios; //adaptador para los comentarios
	private BaseSQLiteCasinoUtalca bbdd; //base de datos interna
	private Bundle bundle;
	private ProgressDialog pDialog; //Progress Dialog para mostrar la ventana de espera
	double promedio = 0;
	private String fecha;
	private boolean puedeValorar = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ver_detalle_elemento);	
		bundle = this.getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        String elemento = bundle.getString("ELEMENTO"); //recibimos el tipo de elemento (0-entrada, 1-plato de fondo, etc)
	    actionBar.setTitle(elemento);

        
        adaptadorListaComentarios = new AdaptadorListaComentarios(this, comentarios); //instanciamos el adaptador para la lista de comentarios
        cargarDatos();
        adaptadorListaComentarios.notifyDataSetChanged();
        
        ListView listaComentarios = (ListView) findViewById(R.id.listview_comentarios);
        listaComentarios.setAdapter(adaptadorListaComentarios); //agregamos el adaptador de lista de comentarios al listview que muestra los comentarios
	}	
	
	
	
	
	
    /**
     * El metodo se encarga de descargar los comentarios del día actual para un determinado componente del menú
     */
	private void cargarDatos() {
		DescargarComentarios dc = new DescargarComentarios();
		dc.execute();
	}

    /**
     * El metodo se encarga de mostrar un alertDialog para poder valorar y comentar un componente del menú
     * @param view
     */
	public void clickValorar(View view){
		if(puedeValorar){
			LayoutInflater inflater = getLayoutInflater();
			final View customView = inflater.inflate(R.layout.valoracion, null);
			final EditText edt_comentario = (EditText) customView.findViewById(R.id.edt_comentario);
			final RatingBar rtb_valoracion = (RatingBar) customView.findViewById(R.id.rtb_comentario);		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        //builder.setMessage("Test for preventing dialog close");
	        builder.setTitle("VALORAR");
	        builder.setCancelable(false);
	        builder.setView(customView)
	        .setPositiveButton("Valorar", new OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {}
	        })
	        .setNegativeButton("Cancelar", new OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {}
	        });
	        final AlertDialog dialog = builder.create();
	        dialog.show();
	        
	        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
			{    
	        	//crear la valoración con el botón aceptar
			    @Override
			    public void onClick(View v)
			    {
			    	int val = (int) rtb_valoracion.getRating();
			    	String com = edt_comentario.getText().toString();
			        Boolean cerrarDialog = false;
			        if(val!=0 && com.length()!=0){ // si se realizo una valoración y se agrego un comentario
			        	cerrarDialog = true;
			        }
			        if(cerrarDialog){
			        	String dbInterna = getString(R.string.db_interna);
			        	bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna, null, 1);
			        	SQLiteDatabase db = bbdd.getWritableDatabase();
			        	String id_user= "";
			        	if(db!=null){
			        		Cursor c = db.rawQuery("SELECT * FROM user", null);
			        		if(c.moveToFirst()){
			        			id_user = c.getString(0);
			        		}
			        		
			        	}
			        	db.close();
			            int idElemento = bundle.getInt("ID");
			            CargarComentario cc = new CargarComentario();
			            String fecha = bundle.getString("fecha").replace("/", "-");
			            int tipo = bundle.getInt("TIPO");
			            cc.execute(Integer.toString(tipo), id_user, Integer.toString(idElemento), Integer.toString(val), com, fecha);
			        	//cargo en la db de valorar
			        	dialog.dismiss();
			        }
			        else{
			        	generarToast("Debe valorar el componente y hacer un comentario");
			        }
			    }});
	        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
	        {            
	              @Override
	              public void onClick(View v)
	              {
	                  //salir del dialog con el botón cancelar
	            	  dialog.dismiss();
	              }
	          });
		}
		else{
			generarToast("Ya valoraste este elemento el día de hoy");
		}
	}
	
	/**
	 * metodo que permite generar un mensaje informativo para una determinada acción, se muestra y luego desaparece
	 * @param mensaje indica el mensaje que se desea mostrar
	 */		
	public void generarToast(CharSequence mensaje){
		   Context context = getApplicationContext();
		   CharSequence text = mensaje;
		   int duration = Toast.LENGTH_LONG;
		   Toast toast = Toast.makeText(context, text, duration);
		   toast.setGravity(Gravity.CENTER, 0, 0);
		   toast.show();
	}
	
	@Override
	public Intent getSupportParentActivityIntent() {
		this.finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		return super.getSupportParentActivityIntent();
	}
	

	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
	}

    /**
     * La clase CargarComentario se encarga de conectarse al servicio crear valoración
     * es la que agrega una valoración en la db
     */			
	public class CargarComentario extends AsyncTask<String, Integer, String>{
		String ip = getString(R.string.ip);
		
		public CargarComentario() {	}
		
		/**
		 * El metodo será el encargado de realizar la tarea en segundo plano para iniciar sesión
		 */			
		@Override
	    protected String doInBackground(String... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+ ip +"/casino_utalca/modelo/crear_valoracion.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			 try {
	             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	             nameValuePairs.add(new BasicNameValuePair("tipo_elemento", arg0[0]));
	             nameValuePairs.add(new BasicNameValuePair("id_usuario", arg0[1]));
	             nameValuePairs.add(new BasicNameValuePair("id_elemento", arg0[2]));
	             nameValuePairs.add(new BasicNameValuePair("valoracion", arg0[3]));
	             nameValuePairs.add(new BasicNameValuePair("comentario", arg0[4]));
	             nameValuePairs.add(new BasicNameValuePair("fecha", arg0[5]));
	             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	             HttpResponse resp = httpClient.execute(post);
	             String respStr = EntityUtils.toString(resp.getEntity());
	             JSONArray respJSON = new JSONArray("["+respStr+"]");
	             JSONObject obj = respJSON.getJSONObject(0);
	             int respuesta = obj.getInt("success");
	             String mensaje = obj.getString("message");
	             return respuesta+";"+mensaje;
	         } catch (Exception e) {
	        	 System.out.println("otro error "+ e);
	             e.printStackTrace();
	         }
			 return "";	
		}
		
		/**
		 * El metodo actualiza la interfaz mientras se ejecuta la tarea asincrona, para ello desde doInBackground deberemos
		 * llamar a publishProgress y pasarle los parametros oportunos
		 */		
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	        pDialog.setProgress(progreso);
		}
			
		/**
		 * El metodo se ejecutará antes de doInBackground, por lo que podremos modificar la interfaz para indicar el comienzo de la tarea (colocar un  cargando, desactivar botones, etc)
		 */		
		@Override
        protected void onPreExecute() {
			pDialog = new ProgressDialog(Detalle.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
			pDialog.setMessage("Almacenando comentario...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		/**
		 * El metodo se ejecuta tras terminar doInBackground y recibe como parámetro lo que este devuelva
		 */				
		@Override
        protected void onPostExecute(String result) {
			String[] aux = result.split(";");
			String mensaje = aux[1];
        	generarToast(mensaje);
        	pDialog.dismiss();  
        	DescargarComentarios dc = new DescargarComentarios();
        	dc.execute();
        }
		
	}
	
    /**
     * La clase DescargarComentarios se encarga de conectarse al servicio listar valoracion fecha tipo
     * es la encargada de descargar los comentarios del día actual para el componente de menú seleccionado
     */		
	private class DescargarComentarios extends AsyncTask<String, Integer, Boolean>{
		String ip = getString(R.string.ip);
		int una=0, dos=0, tres=0, cuatro=0, cinco=0; // Contadores de cada notas
		String idElemento = Integer.toString(bundle.getInt("ID"));
		String fecha =bundle.getString("fecha");
		String tipo_elemento = Integer.toString(bundle.getInt("TIPO"));		
		
		public DescargarComentarios(){}
		
		/**
		 * El metodo será el encargado de realizar la tarea en segundo plano para iniciar sesión
		 */			
		@Override
        protected Boolean doInBackground(String... params) {
			String dbInterna = getString(R.string.db_interna);
			bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna , null, 1);
        	SQLiteDatabase db = bbdd.getWritableDatabase();
        	String id_user= "";
        	if(db!=null){
        		Cursor c = db.rawQuery("SELECT * FROM user", null);
        		if(c.moveToFirst()){
        			id_user = c.getString(0);
        		}
        		
        	}
        	db.close();
			
			HttpGet get;
			
			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://"+ip+"/casino_utalca/modelo/listar_valoracion_fecha_tipo.php?" +
					"fecha=" + fecha.replace("/", "-") + 
					"&tipo_elemento=" + tipo_elemento +
					"&id_elemento=" + idElemento +
					"&id_user="+ id_user;
			
			get = new HttpGet(url);
		    get.setHeader("content-type", "application/json");
		    try{	
		    	HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++){
		        	JSONObject obj = respJSON.getJSONObject(i);
		            String nombre = obj.getString("nombre");
		            int val = Integer.parseInt(obj.getString("valoracion"));
		            int puede = Integer.parseInt(obj.getString("usuario_val"));
		            promedio = promedio + val; // se van sumando los valores de las distintas valoraciones
		            una = (val==1) ? una+1 : una; // si val == 1 entonces val++
		            dos = (val==2) ? dos+1 : dos; // si val == 2 entonces val++
		            tres = (val==3) ? tres+1 : tres; // si val == 3 entonces val++
		            cuatro = (val==4) ? cuatro+1 : cuatro; // si val == 4 entonces val++
		            cinco = (val==5) ? cinco+1 : cinco; // si val == 5 entonces val++
		            String comentario = obj.getString("comentario");
                    // cada valoración se agrega al arrayList de comentarios
		            comentarios.add(new ObjetoComentario(1, nombre, val, comentario));
		            if(puede==0){
		            	puedeValorar = true;
		            }
		            else{
		            	puedeValorar = false;
		            }
		        }
		        promedio = promedio / respJSON.length(); // al final se calcula el promedio final
		        publishProgress(100);
		        return true;
		    }
		    catch(Exception ex){}
		    return false;
        }	
		
		/**
		 * El metodo actualiza la interfaz mientras se ejecuta la tarea asincrona, para ello desde doInBackground deberemos
		 * llamar a publishProgress y pasarle los parametros oportunos
		 */		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	        pDialog.setProgress(progreso);
		}
			
		/**
		 * El metodo se ejecutará antes de doInBackground, por lo que podremos modificar la interfaz para indicar el comienzo de la tarea (colocar un  cargando, desactivar botones, etc)
		 */			
		@Override
        protected void onPreExecute() {
			pDialog = new ProgressDialog(Detalle.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.setMax(100);
			pDialog.setMessage("Descargando comentarios...");
			pDialog.setProgress(0);
            pDialog.show();
            comentarios.clear();
            adaptadorListaComentarios.notifyDataSetChanged();
            promedio = 0;
        }

		/**
		 * El metodo se ejecuta tras terminar doInBackground y recibe como parámetro lo que este devuelva
		 */			
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			adaptadorListaComentarios.notifyDataSetChanged();
			
			
			// actualizamos la nota del componente con 1 decimal
	        ((TextView) findViewById(R.id.nota_entrada)).setText(Double.toString(promedio).substring(0, 3));
	        
	        // actualizamos el ratingbar con el promedio
	        ((RatingBar) findViewById(R.id.ratingbar)).setRating((float)(promedio));
	        
	        // actualizamos el grafico de barras horizontal que representa las votaciones de los usuarios
			WebView webview = (WebView) findViewById(R.id.char_view);
			int max = Math.max(una, Math.max(dos, Math.max(tres,Math.max(cuatro,cinco))));
	        webview.loadUrl("http://chart.apis.google.com/chart?" + // url base
	        		"cht=bhg&" + // tipo de grafico
	        		"chs=150x110&" +// tamaño de la imagen
	        		"chd=t:"+cinco+","+cuatro+","+tres+","+dos+","+una+"&" + // valores
	        		"chxt=y,r&" + // ejes que se muestran
	        		"chxl=0:|1%20Estrella%20|2%20Estrellas|3%20Estrellas|4%20Estrellas|5%20Estrellas|" + // eje y (a la izquierda) (de abajo a arriba)
	        		"1:|"+una+" votos|"+dos+" votos|"+tres+" votos|"+cuatro+" votos|"+cinco+" votos"+ // eje y (a la derecha) -> (de arriba a abajo)
	        		"&chds=0,"+max+"&" + // escala de relacion de los graficos
	        		"chco=b9b9b9&" + // color de las barras
	        		"chbh=14,0,6" // dimension de las barras 
	        		);
		}
	}
}

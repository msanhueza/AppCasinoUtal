package com.example.casinoutal.componentes;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.autenticacion.HomeUser;

/**
 * Esta Activity se encarga de capturar los datos de la encuesta sobre la calidad del servicio.
 * Y una vez guardada la encuesta, muestra en una ventana emergente los datos.
 */
public class Encuesta extends ActionBarActivity {
	BaseSQLiteCasinoUtalca bbdd;
	private ProgressDialog pDialog;
	RatingBar rb1;
	RatingBar rb2;
	RatingBar rb3;
	double promedio1 = 0, promedio2 = 0, promedio3 = 0;
	int puno1 = 0, puno2 = 0, puno3 = 0, puno4 = 0, puno5 = 0;
	int pdos1 = 0, pdos2 = 0, pdos3 = 0, pdos4 = 0, pdos5 = 0;
	int ptres1 = 0, ptres2 = 0, ptres3 = 0, ptres4 = 0, ptres5 = 0;		
    
	/**
	 * Carga en la vista los elementos de la encuesta. 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encuesta);
		rb1 = (RatingBar) findViewById(R.id.ratingBar1);
		rb2 = (RatingBar) findViewById(R.id.ratingBar2);
		rb3 = (RatingBar) findViewById(R.id.ratingBar3);
		
		pDialog = new ProgressDialog(Encuesta.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        
        pDialog.setCancelable(true);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Encuesta Diaria");
	}
	
	public void onClickEnviarEncuesta(View v){
		if(validarEncuesta()){ //Si no hay errores registramos el usuario
        	RatingBar ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        	RatingBar ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        	RatingBar ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        	EditText comentario = (EditText) findViewById(R.id.editText_comment);
        	
        	String rb1 = Integer.toString((int) ratingBar1.getRating());
        	String rb2 = Integer.toString((int) ratingBar2.getRating());
        	String rb3 = Integer.toString((int) ratingBar3.getRating());
        	String comentarioAux = comentario.getText().toString();
        	
        	// obtenemos la información del usuario de nuestra BBDD Local
        	String idUsuario = "";
        	String dbInterna = getString(R.string.db_interna);
    		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
    		SQLiteDatabase db = bbdd.getWritableDatabase();
    		if(db!=null){
    			Cursor c = db.rawQuery("SELECT * FROM user", null);
    			if(c.moveToFirst()){
    				idUsuario = c.getString(0);
    				System.out.println("ID del Usuario: " + idUsuario);
    			}
    		}
    		db.close();
        	
    		// Cargamos los datos en el servidor.
        	Cargar c = new Cargar();
        	c.execute(idUsuario, rb1, rb2, rb3, comentarioAux);
		}
		else{
			generarToast("Faltan campos por completar");
		}		
	}

	/**
	 * función para volver atras con el ícono de la aplicación
	 */
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
	 * Muestra los votos de los otros usuarios que han realizado la encuestra el mismo día
	 */
	public void showEncuesta(){
		// cargo la vista base
		LayoutInflater inflater = getLayoutInflater();
		final View customView = inflater.inflate(R.layout.resultados_encuesta, null);
			
	// PREGUNTA 1
		// creo el webview que mostrará el grafico de resultados
        WebView webview1 = (WebView) customView.findViewById(R.id.char_view1);
        // determino la máx. cantidad de votos que ha reviso cada estrella de la pregunta.
        int max1 = Math.max(puno1, Math.max(puno2, Math.max(puno3,Math.max(puno4,puno5))));
        // formateo el promedio de nota a solo un decimal
        // cargo el promedio en el ratingbar y en el textView
        ((RatingBar) customView.findViewById(R.id.prom_rat1)).setRating((float)(promedio1));
        ((TextView) customView.findViewById(R.id.tvProm1)).setText(Double.toString(promedio1).substring(0, 3));
        // Llamo al servicio web de google para graficar y muestro la imagen en el webView
        webview1.loadUrl("http://chart.apis.google.com/chart?" + // URL BASE
        		"cht=bhg&" + // TIPO DE GRAFICO
        		"chs=150x110&" +// TAMAÑO IMAGEN
        		"chd=t:"+puno5+","+puno4+","+puno3+","+puno2+","+puno1+"&" + // VALORES
        		"chxt=y,r&" + // EJES QUE SE MUESTRAS
        		"chxl=0:|1%20Estrella%20|2%20Estrellas|3%20Estrellas|4%20Estrellas|5%20Estrellas|" + // EJE Y (A LA IZQUIERDA) (de abajo a arriba)
        		"1:|"+puno1+" votos|"+puno2+" votos|"+puno3+" votos|"+puno4+" votos|"+puno5+" votos"+ // EJE Y (A LA DERECHA) -> (de arriba a abajo)
        		"&chds=0,"+max1+"&" + // escala de relacion de los graficos
        		"chco=b9b9b9&" + // color de las barras
        		"chbh=14,0,6" // dimension de las barras 
        		);
    // PREGUNTA 2
        WebView webview2 = (WebView) customView.findViewById(R.id.char_view2);
        
        int max2 = Math.max(pdos1, Math.max(pdos2, Math.max(pdos3,Math.max(pdos4,pdos5))));
        ((RatingBar) customView.findViewById(R.id.prom_rat2)).setRating((float)(promedio2));
        ((TextView) customView.findViewById(R.id.tvProm2)).setText(Double.toString(promedio2).substring(0, 3));
        
        webview2.loadUrl("http://chart.apis.google.com/chart?" + // URL BASE
        		"cht=bhg&" + // TIPO DE GRAFICO
        		"chs=150x110&" +// TAMAÑO IMAGEN
        		"chd=t:"+pdos5+","+pdos4+","+pdos3+","+pdos2+","+pdos1+"&" + // VALORES
        		"chxt=y,r&" + // EJES QUE SE MUESTRAS
        		"chxl=0:|1%20Estrella%20|2%20Estrellas|3%20Estrellas|4%20Estrellas|5%20Estrellas|" + // EJE Y (A LA IZQUIERDA) (de abajo a arriba)
        		"1:|"+pdos1+" votos|"+pdos2+" votos|"+pdos3+" votos|"+pdos4+" votos|"+pdos5+" votos"+ // EJE Y (A LA DERECHA) -> (de arriba a abajo)
        		"&chds=0,"+max2+"&" + // escala de relacion de los graficos
        		"chco=b9b9b9&" + // color de las barras
        		"chbh=14,0,6" // dimension de las barras 
        		);	   
   // PREGUNTA 3
        WebView webview3 = (WebView) customView.findViewById(R.id.char_view3);
        
        int max3 = Math.max(ptres1, Math.max(ptres2, Math.max(ptres3,Math.max(ptres4,ptres5))));
        ((RatingBar) customView.findViewById(R.id.prom_rat3)).setRating((float)(promedio3));
        ((TextView) customView.findViewById(R.id.tvProm3)).setText(Double.toString(promedio3).substring(0, 3));
        
        webview3.loadUrl("http://chart.apis.google.com/chart?" + // URL BASE
        		"cht=bhg&" + // TIPO DE GRAFICO
        		"chs=150x110&" +// TAMAÑO IMAGEN
        		"chd=t:"+ptres5+","+ptres4+","+ptres3+","+ptres2+","+ptres1+"&" + // VALORES
        		"chxt=y,r&" + // EJES QUE SE MUESTRAS
        		"chxl=0:|1%20Estrella%20|2%20Estrellas|3%20Estrellas|4%20Estrellas|5%20Estrellas|" + // EJE Y (A LA IZQUIERDA) (de abajo a arriba)
        		"1:|"+ptres1+" votos|"+ptres2+" votos|"+ptres3+" votos|"+ptres4+" votos|"+ptres5+" votos"+ // EJE Y (A LA DERECHA) -> (de arriba a abajo)
        		"&chds=0,120&" + // escala de relacion de los graficos
        		"&chds=0,"+max3+"&" + // escala de relacion de los graficos
        		"chco=b9b9b9&" + // color de las barras
        		"chbh=14,0,6" // dimension de las barras 
        		);        
		//Cargo en un AlertDialog la vista que hemos generado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(customView)
		        .setTitle("Resultados Encuesta Diaria")
		        .setCancelable(false)
		        .setNeutralButton("Aceptar",new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                        dialog.cancel();
		        	            Intent intent = new Intent(Encuesta.this, HomeUser.class);
		        	            startActivity(intent);
		        	            overridePendingTransition(R.anim.right_in, R.anim.right_out);
		        	            finish();
		                    }
		                });
		// Muestro el alert dialog creado
		AlertDialog alert = builder.create();
		alert.show();		
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
	 * Captura los datos de la encuesta y valida que se hayan ingresado todos.
	 * @return true o false dependiendo de sí los datos ingresados son correctos o no-
	 */
	public boolean validarEncuesta() {
		rb1 = (RatingBar) findViewById(R.id.ratingBar1);
		rb2 = (RatingBar) findViewById(R.id.ratingBar2);
		rb3 = (RatingBar) findViewById(R.id.ratingBar3);
		EditText comment = (EditText) findViewById(R.id.editText_comment);
		
		int val1 = (int) rb1.getRating();
		int val2 = (int) rb2.getRating();
		int val3 = (int) rb3.getRating();
		String auxComment = comment.getText().toString();
		
		if(val1 != 0 && val2 != 0 && val3 != 0 && auxComment.length()!=0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Clase que permite descargar los datos de las demás encuestas registradas para un determinado día.
	 */
	private class Descargar extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);

		/**
		 * Constructor de la clase
		 */
		public Descargar(){
		}
		
		/**
		 * Se conecta al servidor web y solicita los datos de todas las encuestas registradas en el día actual (el servidor
		 * determina el día actual).  
		 */
		@Override
        protected Boolean doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get;
			get =new HttpGet("http://"+ip+"/casino_utalca/modelo/listar_encuesta_fecha.php");
		    get.setHeader("content-type", "application/json");
		    try
		    {
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++)
		        {
		        	JSONObject obj = respJSON.getJSONObject(i);
		        // PREGUNTA 1
		            int val1 = Integer.parseInt(obj.getString("puntuacion_1"));
		            // suma cada valor para luevo calcular el promedio
		            promedio1 = promedio1 + val1;
		            // cuenta los votos dependiendo la cantidad de estrallas asignadas. 
		            // (es una asignacion condicional)
		            puno1 = (val1==1) ? puno1+1 : puno1;
		            puno2 = (val1==2) ? puno2+1 : puno2;
		            puno3 = (val1==3) ? puno3+1 : puno3;
		            puno4 = (val1==4) ? puno4+1 : puno4;
		            puno5 = (val1==5) ? puno5+1 : puno5;		            
		        // PREGUNTA 3 
		            int val2 = Integer.parseInt(obj.getString("puntuacion_2"));
		            promedio2 = promedio2 + val2;
		            pdos1 = (val2==1) ? pdos1+1 : pdos1;
		            pdos2 = (val2==2) ? pdos2+1 : pdos2;
		            pdos3 = (val2==3) ? pdos3+1 : pdos3;
		            pdos4 = (val2==4) ? pdos4+1 : pdos4;
		            pdos5 = (val2==5) ? pdos5+1 : pdos5;		
           
		        // PREGUNTA 3
		            int val3 = Integer.parseInt(obj.getString("puntuacion_3"));
		            promedio3 = promedio3 + val3;
		            ptres1 = (val3==1) ? ptres1+1 : ptres1;
		            ptres2 = (val3==2) ? ptres2+1 : ptres2;
		            ptres3 = (val3==3) ? ptres3+1 : ptres3;
		            ptres4 = (val3==4) ? ptres4+1 : ptres4;
		            ptres5 = (val3==5) ? ptres5+1 : ptres5;		            
		            publishProgress(i*4);
		        }
		        //Calcula los promedios de cada pregunta
		        promedio1 = promedio1 / respJSON.length();
		        promedio2 = promedio2 / respJSON.length();
		        promedio3 = promedio3 / respJSON.length();
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
			pDialog.setMessage("Descargando resultados...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		/**
		 * Una vez finalizada la descarga cierra el JDialog y muestra los resultados.
		 */
		@Override
		protected void onPostExecute(Boolean result) {
            showEncuesta();			
			pDialog.dismiss();
		}
	}	
	
	/**
	 * Clase que se comunica con el servidor y le envía los datos de la encuesta realizada
	 */
	public class Cargar extends AsyncTask<String, Integer, String>{
		String ip = getString(R.string.ip);
		public Cargar(){
			
		}

		/**
		 * Se encarga de enviar los datos al servidos para almacenarlos
		 */
		@Override
	    protected String doInBackground(String... arg0) {			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+ ip +"/casino_utalca/modelo/crear_encuesta.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			 try {
				 // Ordeno los datos ingresados
	             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	             nameValuePairs.add(new BasicNameValuePair("id_usuario", arg0[0]));
	             nameValuePairs.add(new BasicNameValuePair("puntuacion_1", arg0[1]));
	             nameValuePairs.add(new BasicNameValuePair("puntuacion_2", arg0[2]));
	             nameValuePairs.add(new BasicNameValuePair("puntuacion_3", arg0[3]));
	             nameValuePairs.add(new BasicNameValuePair("comentario", arg0[4]));
	             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	             // los envío al servidor
	             HttpResponse resp = httpClient.execute(post);
	             // capturo la respuesta
	             String respStr = EntityUtils.toString(resp.getEntity());
	             JSONArray respJSON = new JSONArray("["+respStr+"]");
	             JSONObject obj = respJSON.getJSONObject(0);
	             int respuesta = obj.getInt("success");
	             String mensaje = obj.getString("message");
	             return respuesta+":"+mensaje;

	         } catch (Exception e) {
	        	 System.out.println("otro error "+ e);
	             e.printStackTrace();
	         }
			return "";
		}

		/**
		 * informa del progreso al JDialog. para que este lo muestre por pantalla
		 */
		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	        pDialog.setProgress(progreso);
		}
		

		/**
		 * Prepara el JDialog antes de comenzar la descarga
		 */
		@Override
        protected void onPreExecute() {
			pDialog.setMessage("Enviando Encuesta...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		/**
		 * Una vez finalizada la descarga cierra el JDialog y muestra los resultados del envío.
		 */
		@Override
        protected void onPostExecute(String result) {
			String[] aux = result.split(":");
			int resultado = Integer.parseInt(aux[0]);
			String mensaje = aux[1];
            if(resultado == 1){ 
                generarToast("Datos cargados exitosamente");
                Descargar d = new Descargar();
                d.execute();
                pDialog.dismiss();
            }
            else{
            	generarToast(mensaje);
            	pDialog.dismiss();
            }
        }
		
	}
}


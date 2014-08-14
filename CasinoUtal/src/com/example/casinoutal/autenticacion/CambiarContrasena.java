package com.example.casinoutal.autenticacion;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;

/**
 * Actividad encargada de poder cambiar la contraseña para un usuario
 */
public class CambiarContrasena extends ActionBarActivity {
	
	private ProgressDialog pDialog; //Progress Dialog para mostrar la ventana de espera
	BaseSQLiteCasinoUtalca bbdd; // base de datos interna
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cambiar_contrasena);	
		pDialog = new ProgressDialog(CambiarContrasena.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        
        pDialog.setCancelable(true);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Cambiar contraseña");
	}
	
	public void onClickCambiarContrasena(View v){
		
		if(validarFormulario()){ // validamos si los campos del formulario estan correctos                
           	EditText edtOldContrasena = (EditText) findViewById(R.id.cb_old_contrasena);
           	EditText edtNewContrasena = (EditText) findViewById(R.id.cb_new_contrasena1);
           	String dbInterna = getString(R.string.db_interna);
           	bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna, null, 1);
			SQLiteDatabase db = bbdd.getWritableDatabase();
			if(db!=null){
				Cursor c = db.rawQuery("SELECT * FROM user", null);
				if(c.moveToFirst()){
					String email = c.getString(1);
	               	String viejaContrasena = "";
	               	String nuevaContrasena = "";
					try {
						viejaContrasena = encrypMd5(edtOldContrasena.getText().toString()); //encripta la contraseña en md5
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					
					try {
						nuevaContrasena = encrypMd5(edtNewContrasena.getText().toString());
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
	               
	
	                Cargar cc = new Cargar();
	                cc.execute(email, viejaContrasena, nuevaContrasena);
				}
			}
			db.close();
		}
		
	}
	
	
	/**
	 * El metodo se encarga de validar si los componentes del formulario para cambiar contraseña son correctos
	 * @return true si son válidos y false si no
	 */
	public boolean validarFormulario(){
		boolean contrasenas = validar2Contrasena();
		if(!validarOldContrasena() || !contrasenas){
			return false;
		}
		return true;
	}
	
	
    /**
     * metodo que permite encriptar un string en md5
     * @param frase es la contraseña que se desea encriptar
     * @return la contraseña encriptada en md5
     */	
	public String encrypMd5(String frase) throws NoSuchAlgorithmException{
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] b = md.digest(frase.getBytes());
	    int size = b.length;
	    StringBuffer h = new StringBuffer(size);
	    //algoritmo y arreglo md5
	        for (int i = 0; i < size; i++) {
	            int u = b[i] & 255;
	                if (u < 16) {
	                    h.append("0" + Integer.toHexString(u));
	                }
	               else {
	                    h.append(Integer.toHexString(u));
	               }
	           }
	      //clave encriptada
	      return h.toString();
	}

	/**
	 * metodo que permite generar un mensaje informativo para una determinada acción, se muestra y luego desaparece
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
	 * El metodo se encarga de validar si la contraseña actual ingresada tiene la extensión minima de 6 caracteres
	 * @return true si la contraseña tiene un minimo de 6 caracteres sino false
	 */
	public boolean validarOldContrasena(){
		EditText edtPass = (EditText) findViewById(R.id.cb_old_contrasena);
		String pass = edtPass.getText().toString();
		if(pass.length()>=6){ // si es mayor a 6 caracteres
			edtPass.setError(null);
			return true;
		}
		else{ // si es menor a 6 caracteres
			edtPass.setError("La contraseña debe tener min 6 caracteres");
			return false;
		}
	}
	
	/**
	 * El metodo se encarga de validar si la contraseña actual y la de confirmación sin iguales y tienen el minimo de 6 caracteres
	 * @return true si las contraseñas cumplen los requisitos sino false
	 */
    public boolean validar2Contrasena(){
    	EditText edtPass1 = (EditText) findViewById(R.id.cb_new_contrasena1);
    	EditText edtPass2 = (EditText) findViewById(R.id.cb_new_contrasena2);
    	String pass1 = edtPass1.getText().toString(); 
    	String pass2 = edtPass2.getText().toString();
    	
    	// pass1 es mayor o igual a 6 y pass2 es mayor o igual a 6
    	if(pass1.length()>=6 && pass2.length()>=6){
    		if(pass1.equals(pass2)){ //son iguales
    			edtPass1.setError(null);
    			edtPass2.setError(null);
    			return true;
    		}
    		else{ //las contraseñas no coinciden
    			edtPass1.setError(null);
            	edtPass2.setError("Las contraseñas no coinciden");
    			return false;
    		}
    	}
        // pass1 es menor a 6 y pass2 es menor a 6 
    	else if(pass1.length()<6 && pass2.length()<6){
        	edtPass1.setError("La contraseña debe tener min 6 caracteres");
        	edtPass2.setError("La contraseña debe tener min 6 caracteres");
        	return false;
        }
        // pass1 es menor a 6 y pass2 es mayor a 6
        else if(pass1.length()<6 && pass2.length()>=6){
        	edtPass1.setError("La contraseña debe tener min 6 caracteres");
        	edtPass2.setError(null);
        	return false;
        }
        // pass1 es mayor o igual a 6 y pass2 es menor a 6
        else{
        	edtPass1.setError(null);
        	edtPass2.setError("La contraseña debe tener min 6 caracteres");
        	return false;
        }   
    }
    
    /**
     * La clase Cambiar Contraseña se encarga de conectarse al servicio cambiar contraseña
     * realizando el cambio en la db 
     */	    
    public class Cargar extends AsyncTask<String, Integer, String>{
		String ip = getString(R.string.ip);
		public Cargar(){
			
		}
		
		/**
		 * El metodo será el encargado de realizar la tarea en segundo plano para iniciar sesión
		 */			
		@Override
	    protected String doInBackground(String... arg0) {
			
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://"+ ip +"/casino_utalca/modelo/cambiar_password.php");
		post.setHeader("content-type", "application/x-www-form-urlencoded");
		 try {
             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
             nameValuePairs.add(new BasicNameValuePair("correo", arg0[0]));
             nameValuePairs.add(new BasicNameValuePair("vieja_contrasena", arg0[1]));
             nameValuePairs.add(new BasicNameValuePair("nueva_contrasena", arg0[2]));
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
			pDialog.setMessage("Cambiando Contraseña...");
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
        	finish();
        	overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
		
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
    
}
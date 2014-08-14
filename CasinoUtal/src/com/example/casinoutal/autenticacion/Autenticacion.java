package com.example.casinoutal.autenticacion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;

/**
 * Actividad encargada de realizar todo lo que tiene que ver con el inicio de sesión de un usuario
 * inicio de sesión, registrar, recuperar contraseña
 */
public class Autenticacion extends Activity {
	
	BaseSQLiteCasinoUtalca bbdd; //base de datos interna
	private ProgressDialog pDialog; //Progress Dialog para mostrar la ventana de espera
	private String correo="";
	private String contrasena="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inicio_sesion);			
		

		
		
		Button btnInicioSesion = (Button) findViewById(R.id.btn_iniciosesion);
		btnInicioSesion.setOnClickListener(new OnClickListener() { //Listener para botón inicio sesión
			//metodo que permite conectarse al servicio iniciar sesión para un usuario determinado
			@Override
			public void onClick(View arg0) {
				EditText edtEmail = (EditText) findViewById(R.id.email_inicio_sesion);
				EditText edtContrasena = (EditText) findViewById(R.id.contrasena_inicio_sesion);
				correo = edtEmail.getText().toString();
				try {
					contrasena = encrypMd5(edtContrasena.getText().toString()); //encriptación de contraseña en md5
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				pDialog = new ProgressDialog(Autenticacion.this);
		        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pDialog.setMessage("Iniciando sesión...");
		        pDialog.setCancelable(true);
		        pDialog.setMax(100);
		        Descargar c = new Descargar();
		        c.execute();

			}
		});			
		
		Button btnRegistro = (Button) findViewById(R.id.btn_registrarse);
		btnRegistro.setOnClickListener(new OnClickListener() { //Listener para botón registrar usuario
			//metodo que permite conectarse al servicio registrar usuario
			@Override
			public void onClick(View arg0) {
               Intent intent = new Intent(Autenticacion.this,RegistrarUsuario.class);
               startActivity(intent);
               overridePendingTransition(R.anim.left_in, R.anim.left_out);
               finish();
			}
		});
		
		TextView tvContrasena = (TextView) findViewById(R.id.recuperar_contrasena);
		tvContrasena.setOnClickListener(new OnClickListener() { //Listener para recuperar contraseña
			//metodo que permite conectarse al servicio recuperar contraseña
			@Override
			public void onClick(View arg0) {
				   LayoutInflater inflater = getLayoutInflater();
				   final View customView = inflater.inflate(R.layout.extra_contrasena, null);
				   final EditText edtRecuperarEmail = (EditText) customView.findViewById(R.id.edt_rcontrasena);
				   AlertDialog.Builder builder = new AlertDialog.Builder(Autenticacion.this);
				   builder.setCancelable(false);
				   builder.setIcon(android.R.drawable.ic_dialog_alert); 
				   builder.setTitle("Recuperar Contraseña");
				   builder.setMessage("Ingrese su correo electrónico para enviarle la nueva contraseña:");
				   builder.setView(customView)
				           .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
				        	   //botón aceptar que invoca al servicio de recuperar contraseña
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				           		pDialog = new ProgressDialog(Autenticacion.this);
				                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        
				                pDialog.setCancelable(true);
				                String email = edtRecuperarEmail.getText().toString();
			                	RecuperarContrasena rc = new RecuperarContrasena();
			                	rc.execute(email);
			               	
				               }
				           })
				           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				        	   //botón cancelar no hace nada
				               public void onClick(DialogInterface dialog, int id) {
				                   
				               }
				           });
				    builder.show();
				
			}
		});
	}
	
	
	@Override
	public void onBackPressed() {
		this.finish();
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
	 * metodo que permite validar si un email tiene el formato correcto
	 * @param edtEmail es el componente el cual se desea evaluar
	 * @return true si tiene el formato correcto, sino false
	 */		
    public boolean validarEmail(EditText edtEmail) {
    	String email = edtEmail.getText().toString();
    	String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
 
        Matcher matcher = pattern.matcher(email);
        if(edtEmail.equals("")){ // si no se ingreso nada
        	edtEmail.setError("Debe ingresar su correo electrónico");
        	return false;
        }
        else if(!matcher.matches()){ //email no sigue el formato correcto
        	edtEmail.setError("Correo electrónico inválido");
        	return false;
        }
        else{ //email válido
        	edtEmail.setError(null);
        	return true;
        }
        
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
     * La clase Descargar se encarga de conectarse al servicio iniciar sesión y validar si los datos
     * ingresados por el usuario son o no válidos 
     */		
    private class Descargar extends AsyncTask<Void, Integer, String>{
		String ip = getString(R.string.ip);
		
		public Descargar(){
			
		}
		
		/**
		 * El metodo será el encargado de realizar la tarea en segundo plano para iniciar sesión
		 */			
		@Override
        protected String doInBackground(Void... params) {
			String mensaje = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get;
			String ruta = "http://"+ip+"/casino_utalca/modelo/iniciar_sesion.php?correo="+correo+"&contrasena="+contrasena;
			get = new HttpGet(ruta);
		    get.setHeader("content-type", "application/json");
		    try
		    {
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        if(respStr.charAt(0) == '{'){
		        	respStr = "["+respStr+"]";
		        }
		        //obtengo cada uno de los parametros del usuario que inicio sesión en la aplicación
		        JSONArray respJSON = new JSONArray(respStr);
	        	JSONObject obj = respJSON.getJSONObject(0);
	            String id_usuario = obj.getString("id_usuario");
	            String nombre = obj.getString("nombre");
	            String apellidos = obj.getString("apellidos");
	            String email = obj.getString("email");
	            String is_admin = obj.getString("is_admin");
	            return id_usuario+":"+nombre+":"+apellidos+":"+email+":"+is_admin;
         } catch (Exception e) {
        	 try{
			        HttpResponse resp = httpClient.execute(get);
			        String respStr = EntityUtils.toString(resp.getEntity());
			        if(respStr.charAt(0) == '{'){
			        	respStr = "["+respStr+"]";
			        }
			        JSONArray respJSON = new JSONArray(respStr);
		        	JSONObject obj = respJSON.getJSONObject(0);
		        	mensaje = obj.getString("message");
		        	return mensaje;
        	 }
        	 catch (Exception ex){
        		 
        	 }
         }
			return "";
		
		    
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
            pDialog.setProgress(0);
            pDialog.show();
        }

		/**
		 * El metodo se ejecuta tras terminar doInBackground y recibe como parámetro lo que este devuelva
		 */			
		@Override
		protected void onPostExecute(String result) {
			String[] aux = result.split(":");
			int id_usuario = 0;
			String nombre = "";
			String email = "";
			boolean is_admin;
			String mensaje = "";
			if(aux.length == 1){ //mensaje de error
				mensaje = result;
            	generarToast(mensaje);
            	pDialog.dismiss();
			}
			else{ //usuario registrado
				id_usuario = Integer.parseInt(aux[0]);
				nombre = aux[1];
				email = aux[3];
				is_admin = (aux[4].equals("1"))? true : false;
                pDialog.dismiss();
                String dbInterna = getString(R.string.db_interna);
                bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna, null, 1);
    			SQLiteDatabase db = bbdd.getWritableDatabase();
    			if(db!=null){
    				db.execSQL("INSERT INTO user(id, mail, nombre, is_admin) VALUES ('"+id_usuario+"','"+email+"','"+nombre+"','"+is_admin+"')");
    			}
    			db.close();
                Intent intent;
                if(is_admin){ // si es administrador pasamos a la activity de administrador
                	intent = new Intent(Autenticacion.this, HomeAdmin.class);
                	startActivity(intent);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	finish();
                }
                else{ //sino pasamos a la activity de usuario
                	intent = new Intent(Autenticacion.this, HomeUser.class);
                	startActivity(intent);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	finish();
                }
	            // REGISTRAR EN BBDD INICIO DE SESION
			}

		}
	}	    
    
    /**
     * La clase RecuperarContraseña se encarga de conectarse al servicio actualizar password 
     * generando una nueva contraseña y enviandola por correo electronico al mail del usuario registrado
     */
    public class RecuperarContrasena extends AsyncTask<String, Integer, String>{
		String ip = getString(R.string.ip);
		public RecuperarContrasena(){
			
		}
		
		/**
		 * El metodo será el encargado de realizar la tarea en segundo plano para actualizar la contraseña del usuario
		 */				
		@Override
		    protected String doInBackground(String... arg0) {
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+ ip +"/casino_utalca/modelo/actualizar_password.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			 try {
	             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	             nameValuePairs.add(new BasicNameValuePair("correo", arg0[0]));
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
			pDialog.setMessage("Generando contraseña...");
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
        }
		
	}

}

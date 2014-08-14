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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

/**
 * Actividad encargada de registrar un nuevo usuario en el sistema
 */
public class RegistrarUsuario extends ActionBarActivity {
	
	private ProgressDialog pDialog; //Progress Dialog para mostrar la ventana de espera
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrarse);
		pDialog = new ProgressDialog(RegistrarUsuario.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        
        pDialog.setCancelable(true);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Registrar usuario");
	}
	
	/**
	 * El metodo se encarga de validar si los componentes del formulario para registrar un usuario son correctos
	 * @return true si son válidos y false si no
	 */	
	public boolean validarFormulario(){
		boolean nombre = validarNombre();
		boolean apellido = validarApellido();
		boolean email = validarEmail();
		//boolean ocupacion = validarOcupacion();
		boolean contrasenas = validar2Contrasena();
		if(!nombre || !apellido || !email || !contrasenas){ //si el nombre o el apellido o el email o las contraseñas no son correctas
			return false;
		}
		return true;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_postre, menu);
		return true;
	}
	
	public void onClickRegistrar(View v){
		
		if(validarFormulario()){ //si no hay errores registramos el usuario
        	EditText edtNombre = (EditText) findViewById(R.id.rg_nombre);
        	EditText edtApellido = (EditText) findViewById(R.id.rg_apellidos);
        	EditText edtEmail = (EditText) findViewById(R.id.rg_email);
        	EditText edtContrasena1 = (EditText) findViewById(R.id.rg_contrasena1);
        	
        	String nombre = edtNombre.getText().toString();
        	String apellido = edtApellido.getText().toString();
        	String email = edtEmail.getText().toString().toLowerCase();
            String contrasena;
			try {
				contrasena = encrypMd5(edtContrasena1.getText().toString()); //encriptamos la contraseña en md5
			} catch (NoSuchAlgorithmException e) { //
				contrasena = edtContrasena1.getText().toString();
				e.printStackTrace();
			}
        	Cargar c = new Cargar();
        	c.execute(nombre, apellido, email, contrasena); // pasamos los parametros requeridos para crear el usuario		
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
	 * El metodo se encarga se validar un nombre
	 * @return true si el nombre ingresado es valido, sino false
	 */
	public boolean validarNombre(){
    	EditText edtNombre = (EditText) findViewById(R.id.rg_nombre);
    	String nombre = edtNombre.getText().toString();
		if (edtNombre.length()<=0) { //si no se ha ingresado el nombre
			edtNombre.setError("Debe ingresar su nombre");
			return false;
		} else if (!nombre.matches("[a-zA-Z_áéíóúñÁÉÍÓÚÑ ]+")) { //si los caracteres del nombre no pertenecen al alfabeto
			edtNombre.setError("Acepta solo Alfabeto");
			return false;
		} else { //nombre correcto
			edtNombre.setError(null);
			return true;
		}    	
	}
	
	
	/**
	 * El metodo se encarga se validar los apellidos
	 * @return true si los apellidos ingresados son validos, sino false
	 */	
	public boolean validarApellido(){
    	EditText edtApellido = (EditText) findViewById(R.id.rg_apellidos);
    	String apellido = edtApellido.getText().toString();
		if (apellido.equals("")) { //si no se han ingresado apellidos
			edtApellido.setError("Debe ingresar sus apellidos");
			return false;
		} else if (!apellido.matches("[a-zA-Z_áéíóúñÁÉÍÓÚÑ ]+")) { //si los caracteres del nombre no pertenecen al alfabeto
			edtApellido.setError("Acepta solo Alfabeto");
			return false;
		} else { //nombre correcto
			edtApellido.setError(null);
			return true;
		}    	
	}	
	
	/**
	 * metodo que permite validar si un email tiene el formato correcto
	 * @param edtEmail es el componente el cual se desea evaluar
	 * @return true si tiene el formato correcto, sino false
	 */	
    public boolean validarEmail() {
    	EditText edtEmail = (EditText) findViewById(R.id.rg_email);
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
	 * El metodo se encarga de validar si la contraseña ingresada y la de confirmación sin iguales y tienen el minimo de 6 caracteres
	 * @return true si las contraseñas cumplen los requisitos sino false
	 */    
    public boolean validar2Contrasena(){
    	EditText edtPass1 = (EditText) findViewById(R.id.rg_contrasena1);
    	EditText edtPass2 = (EditText) findViewById(R.id.rg_contrasena2);
    	String pass1 = edtPass1.getText().toString(); 
    	String pass2 = edtPass2.getText().toString();
    	
    	// pass1 es mayor o igual a 6 y pass2 es mayor o igual a 6
    	if(pass1.length()>=6 && pass2.length()>=6){
    		if(pass1.equals(pass2)){ //son iguales
    			edtPass1.setError(null);
    			edtPass2.setError(null);
    			return true;
    		}
    		else{ //no coinciden las contraseñas
    			edtPass1.setError(null);
            	edtPass2.setError("La contraseñas no coinciden");
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
	
	/*
	 * La clase Cargar se encarga de conectarse al servicio crear usuario
	 * agregandolo en la db con sus respectivos parametros
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
			HttpPost post = new HttpPost("http://"+ ip +"/casino_utalca/modelo/crear_usuario.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			 try {
	             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	             nameValuePairs.add(new BasicNameValuePair("nombre", arg0[0]));
	             nameValuePairs.add(new BasicNameValuePair("apellidos", arg0[1]));
	             nameValuePairs.add(new BasicNameValuePair("email", arg0[2]));
	             nameValuePairs.add(new BasicNameValuePair("contrasena", arg0[3]));
	             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	             HttpResponse resp = httpClient.execute(post);
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
			pDialog.setMessage("Registrando Usuario...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		/**
		 * El metodo se ejecuta tras terminar doInBackground y recibe como parámetro lo que este devuelva
		 */			
		@Override
        protected void onPostExecute(String result) {
			String[] aux = result.split(":");
			int resultado = Integer.parseInt(aux[0]);
			String mensaje = aux[1];
            if(resultado == 1)
            {
                pDialog.dismiss();
                generarToast("Datos cargados exitosamente");
	            Intent intent = new Intent(RegistrarUsuario.this, Autenticacion.class);
	            startActivity(intent);
	            overridePendingTransition(R.anim.right_in, R.anim.right_out);
	            finish();
            }
            else{
            	generarToast(mensaje);
            	pDialog.dismiss();
            }
        }
		
	}
	
}


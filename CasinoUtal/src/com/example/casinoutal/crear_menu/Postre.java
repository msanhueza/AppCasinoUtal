package com.example.casinoutal.crear_menu;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casinoutal.R;
import com.example.casinoutal.autenticacion.HomeAdmin;
import com.example.casinoutal.componentes.BaseSQLiteCasinoUtalca;

public class Postre extends ActionBarActivity {
	private ArrayList<String> postres = new ArrayList<String>();
	private ArrayList<String> values = new ArrayList<String>();
	private ArrayAdapter<String> adapter1;
	private ArrayAdapter<String> adapter;
	private Bundle bd;
	private ProgressDialog pDialog;
	private HashMap<String,String> postresId;
	private BaseSQLiteCasinoUtalca bbdd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// ESTABLECIENDO VALORES INICIALES (INTERFAZ)
		setContentView(R.layout.crear_postres);
		bd = getIntent().getExtras();
		setTitle("Postre");
		cambiarInfo(bd.getString("menu"));
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		// ESTABLECIENDO VALORES INICIALES (BBDD)
		postresId = new HashMap<>();
		cargarPlatosDeFondo();
		
		// ADAPTADORES
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, postres);
		adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// AUTOCOMPLETE
		final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompletePostre);
		textView.setHint(R.string.hint_postre);
		textView.setThreshold(1);
		textView.setTextColor(Color.BLACK);
		textView.setAdapter(adapter);	
	    
		String dbInterna = getString(R.string.db_interna);
	    //CARGAR BASE DE DATOS LOCAL
  		bbdd = new BaseSQLiteCasinoUtalca(this, dbInterna, null, 1);
  		SQLiteDatabase db = bbdd.getWritableDatabase();
  		if(db != null){
  			Cursor c = db.rawQuery("SELECT * FROM postre", null);
  			if (c.moveToFirst()) {
  			     //Recorremos el cursor hasta que no haya más registros
  			     do {
  			          String nombre = c.getString(0);
  			          values.add(nombre);
  			     } while(c.moveToNext());
  			}
  			db.close();
  			adapter1.notifyDataSetChanged();
  		}
  		
  		
  		// LISTA DE PLATOS DE POSTRES
  		ListView listView = (ListView) findViewById(R.id.listViewPostre);	 
	    listView.setAdapter(adapter1);		    
	    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	    		  @Override
	    		  public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
	    			  AlertDialog.Builder alertDialog = new AlertDialog.Builder(Postre.this); 
	    			  alertDialog.setMessage("Este postre se eliminará"); 
	    			  alertDialog.setTitle("Eliminar"); 
	    			  alertDialog.setIcon(android.R.drawable.ic_dialog_alert); 
	    			  alertDialog.setCancelable(false); 
	    			  alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() 
	    			  { 
	    			     public void onClick(DialogInterface dialog, int which) { 
	    			    	 SQLiteDatabase db = bbdd.getWritableDatabase();
	      			    	 db.execSQL("DELETE FROM postre WHERE nombre='"+values.get(position)+"';");
	      			    	 db.close();   
		    			    values.remove(position);
					    	adapter1.notifyDataSetChanged();
	    			     } 
	    			   }); 
	    			   alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
	    			   { 
	    			      public void onClick(DialogInterface dialog, int which) 
	    			     { 

	    			      } 
	    			    }); 
	    			    alertDialog.show();
		    		   return true;
	    		  }
	    		 
	    		 });	    

	    // BOTON AGREGAR
	    ImageButton addButton = (ImageButton) findViewById(R.id.addPostre);
	    addButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		       final String auxPlato = textView.getText().toString().toUpperCase(); //conversion a mayúscula
		       if(!auxPlato.equals("")){
			       if(!values.contains(auxPlato)){
			    	   if(!isPlatoInBaseDatos(auxPlato)){
			    			  AlertDialog.Builder alertDialog = new AlertDialog.Builder(Postre.this); 
			    			  alertDialog.setMessage("Este postre no se encuentra en la base de datos, ¿Desea agregarlo?"); 
			    			  alertDialog.setTitle(auxPlato); 
			    			  alertDialog.setIcon(android.R.drawable.ic_dialog_alert); 
			    			  alertDialog.setCancelable(false); 
			    			  alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() 
			    			  { 
			    			     public void onClick(DialogInterface dialog, int which) 
			    			    { 
							    	 Cargar cg = new Cargar();
							    	 cg.execute(auxPlato);

			    			     } 
			    			   }); 
			    			   alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
			    			   { 
			    			      public void onClick(DialogInterface dialog, int which) 
			    			     { 
							    	   generarToast(auxPlato + " no fue agregado");
			    			      } 
			    			    }); 
			    			    alertDialog.show();
			    	   }
			    	   else{
			    		   values.add(auxPlato);
				    	   adapter1.notifyDataSetChanged();
			    	   }
			    	   SQLiteDatabase db = bbdd.getWritableDatabase();
			    	   db.execSQL("INSERT INTO postre (nombre) " + "VALUES ('"+auxPlato+"')");
			    	   db.close();			    	   
			       }
			       else{
			    	   generarToast(auxPlato + " ya esta agregado");
			       }		    	   
		       }
		       else{
		    	   generarToast("Debe ingresar un postre");
		       }

		       textView.getText().clear();			       
		    }
	    });	  
	}
	
	@Override
	public Intent getSupportParentActivityIntent() {
		this.finish();
		this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		return super.getSupportParentActivityIntent();
	}
	
	/**
	* Método que crea una instancia para descargar la información de la BD. 
	*/	
	public void cargarPlatosDeFondo(){
		pDialog = new ProgressDialog(Postre.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Descargando Postres...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
		Descargar dc = new Descargar();
		dc.execute();
	}

	/**
	* Método que permite verificar si el plato se encuentra o no en la BD.
	* @return verdadero o falso según el caso que sea.
	*/
	public boolean isPlatoInBaseDatos(String plato){
		if(postres.contains(plato)){
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_postre, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_borrar_lista) {
			if(values.size() == 0){
				mensajeInformacion("No existen elementos para eliminar");
			}
			else{
				eliminarConjunto();
			}
			
			return true;
		}
		if(id == R.id.action_volver){
			confirmacionVolverMenu();
			return true;
		}
		if(id == R.id.action_guardar_menu){
			if(values.size()!=0){
				CargarMenu cm = new CargarMenu();
				String detalle="[{\"tipo\":\"fecha_menu\",\"valor\":\"" + bd.getString("fecha") + "\"},"+
								"{\"tipo\":\"tipo_menu\",\"valor\":\"" + bd.getString("menu") + "\"},";
				if(!bd.getStringArrayList("entrada").isEmpty()){
					detalle = detalle + "{\"tipo\":\"entrada\",\"valor\":\"" + bd.getStringArrayList("entrada").toString().replace("[", "").replace("]", "").replace(" ", "") + "\"},";
				}
				detalle = detalle + "{\"tipo\":\"plato_fondo\",\"valor\":\"" + bd.getStringArrayList("platosFondo").toString().replace("[", "").replace("]", "").replace(" ", "") + "\"},";
				if(!bd.getStringArrayList("acompanamiento").isEmpty()){
					detalle = detalle + "{\"tipo\":\"acompanamiento\",\"valor\":\"" + bd.getStringArrayList("acompanamiento").toString().replace("[", "").replace("]", "").replace(" ", "") + "\"},";
				}
				detalle = detalle + "{\"tipo\":\"ensalada\",\"valor\":\"" + bd.getStringArrayList("ensalada").toString().replace("[", "").replace("]", "").replace(" ", "") + "\"}," +
									"{\"tipo\":\"postre\",\"valor\":\"" + obtenerIds(values).toString().replace("[", "").replace("]", "").replace(" ", "") + "\"}]";
				
				
				cm.execute(detalle);				
			}
			else{
				generarToast("Debe agregar al menos un postre");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * metodo que solicita la confirmacion para volver al menu
	 */
	public void confirmacionVolverMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Postre.this);
        builder.setCancelable(false);
        builder.setMessage("¿Desea salir?")
               .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Postre.this, HomeAdmin.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_FORWARD_RESULT);
						startActivity(intent);
						overridePendingTransition(R.anim.left_in, R.anim.left_out);	
                   }
               })
               .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                   }
               });
        
        builder.show();
    }
	
	/**
	 * metodo que obtiene los id de los postres
	 * @param values lista con los nombres de los postres
	 * @return array con id de postres
	 */
	private ArrayList<String> obtenerIds(ArrayList<String> values){
		ArrayList<String> ids = new ArrayList<>();
		for(String valor : values){
			ids.add(postresId.get(valor));
		}
		return ids;
	}
	
	/**
	 * metodo que muestra un mensaje de informacion para una determinada accion
	 * @param mensaje es lo que se desea mostrar al usuario
	 */
	public void mensajeInformacion(String mensaje){
		AlertDialog.Builder builder =
                new AlertDialog.Builder(Postre.this);
 
        builder.setMessage(mensaje)
               .setTitle("Información")
               .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });	
        builder.show();
	}
	
	/**
	 * metodo que permite eliminar un conjunto de elementos seleccionados
	 */
	public void eliminarConjunto(){
		final ArrayList<Integer> mSelectedItems = new ArrayList<>(); //indices de val[]
		final String[] val = new String[values.size()]; //arreglo donde guardamos los valores de values
		for(int i=0; i<values.size(); i++){
			val[i] = values.get(i); //inicializamos el arreglo
		}
	    AlertDialog.Builder builder = new AlertDialog.Builder(Postre.this);
        builder.setCancelable(false);
	    builder.setTitle("Eliminar")
	           .setMultiChoiceItems(val, null,
	                      new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	    // Set the action buttons
	           .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	            	   SQLiteDatabase db = bbdd.getWritableDatabase();
	                   for(int i=0; i<mSelectedItems.size(); i++){
	                	   values.remove(val[mSelectedItems.get(i)]);
	                	   
	                	   db.execSQL("DELETE FROM postre WHERE nombre='"+val[mSelectedItems.get(i)]+"';");

	                   }
	                   db.close();  
	                   adapter1.notifyDataSetChanged();   
	                   
	               }
	           })
	           .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });
	           builder.show();		
	}

	/**
	 * Método que permite cambiar el título del menú según donde se encuentre
	 * posicionado, ya sea en un menú básico, hipocalórico, vegetariano o ejecutivo.
	 * @param Menu lleva la información que determina que tipo de menú se debe mostrar.
	 */
	private void cambiarInfo(String menu) {
		// TODO Auto-generated method stub
		TextView tv;
		if(menu.equals("1")){
			tv = (TextView)findViewById(R.id.tipo_menu_postre);
			tv.setText("MENÚ BÁSICO");
		}
		else if(menu.equals("2")){
			tv = (TextView)findViewById(R.id.tipo_menu_postre);
			tv.setText("MENÚ HIPOCALÓRICO");
		}
		else if(menu.equals("3")){
			tv = (TextView)findViewById(R.id.tipo_menu_postre);
			tv.setText("MENÚ VEGETARIANO");
		}
		else if(menu.equals("4")){
			tv = (TextView)findViewById(R.id.tipo_menu_postre);
			tv.setText("MENÚ EJECUTIVO");
		}
	}
	
	/**
	 * Método que permite mostrar eventualmente todo los componentes de postre que se 
	 * encuentren actualmente asociados y almacenados a la fecha seleccionada.
	 * Para ello se comunica con el servicio correspondiente (listar_postre.php),
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
			get =new HttpGet("http://"+ip+"/casino_utalca/modelo/listar_postre.php");
		    get.setHeader("content-type", "application/json");
		    try
		    {
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++)
		        {
		        	JSONObject obj = respJSON.getJSONObject(i);
		            String id = obj.getString("id_postre");
		            String nombre = obj.getString("nombre_postre");
		            postresId.put(nombre.toUpperCase(),id);
		            postres.add(nombre.toUpperCase());
		            publishProgress(i*4);
		        }
		        publishProgress(100);
		        return true;
		    }
		    catch(Exception ex){}
		    return false;
        }
		
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	 
	        pDialog.setProgress(progreso);
		}
		
		
		@Override
        protected void onPreExecute() {
            pDialog.setProgress(0);
            pDialog.show();
        }
		
	}
	
	/**
	 * Método que permite cargar eventualmente todo los componentes de un postre que se 
	 * encuentren actualmente asociados a la fecha seleccionada previamente.
	 * Para ello se comunica con el servicio correspondiente (crear_postre.php),
	 * para finalmente cargarlos a la BD.
	 * @return verdadero o falso dependiendo si la acción fue ejecutada con éxito. 
	 */	
	private class Cargar extends AsyncTask<String, Integer, Boolean>{
		String ip = getString(R.string.ip);
		public Cargar(){
			
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://"+ip+"/casino_utalca/modelo/crear_postre.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			 try {
	             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	             nameValuePairs.add(new BasicNameValuePair("nombre_postre", arg0[0]));
	             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	             httpClient.execute(post);
	             values.add(arg0[0]);
	             adapter.add(arg0[0]); //actualizamos la lista del autocompletar
	             return true;
	         } catch (ClientProtocolException e) {
	             e.printStackTrace();
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
			return false;
		}

		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	 
	        pDialog.setProgress(progreso);
		}
			
		@Override
        protected void onPreExecute() {
			pDialog.setMessage("Almacenando Postre...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		@Override
        protected void onPostExecute(Boolean result) {
			if(result)
            {
	           pDialog.dismiss();
	    	   postres.clear();
	    	   postresId.clear();
	    	   Descargar dc = new Descargar();
	    	   dc.execute(); 
	    	   adapter.notifyDataSetChanged();
	           adapter1.notifyDataSetChanged();
	           generarToast("Datos cargados exitosamente");
            }
            else{
            	pDialog.dismiss();
            	generarToast("Los datos no fueron cargados");
            }
        }
	}

	private class CargarMenu extends AsyncTask<String, Integer, Boolean>{
		String ip = getString(R.string.ip);
		public CargarMenu(){
			
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post;
			
			post= new HttpPost("http://"+ip+"/casino_utalca/modelo/crear_menu.php");
			post.setHeader("content-type", "application/x-www-form-urlencoded");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				JSONArray respJSON = new JSONArray(arg0[0]);
		        for(int i=0; i<respJSON.length(); i++)
		        {
		        	JSONObject obj = respJSON.getJSONObject(i);
		            String tipo = obj.getString("tipo");
		            String valores = obj.getString("valor");
		            nameValuePairs.add(new BasicNameValuePair(tipo, valores));
		        }				
	            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            httpClient.execute(post);
	            return true;
			} catch (IOException er) {
				return false;
			} catch (JSONException e) {
				return false;
			}
		}

		protected void onProgressUpdate(Integer... values) {
			int progreso = values[0].intValue();
	 
	        pDialog.setProgress(progreso);
		}
			
		@Override
        protected void onPreExecute() {
			pDialog.setMessage("Almacenando Menú...");
			pDialog.setProgress(0);
            pDialog.show();
        }
		
		@Override
        protected void onPostExecute(Boolean result) {
            if(result){
                pDialog.dismiss();
				postres.clear();
				postresId.clear();
				Descargar dc = new Descargar();
				dc.execute();
				adapter.notifyDataSetChanged();
				adapter1.notifyDataSetChanged();	//actualizamos el listView
                generarToast("Datos cargados exitosamente");
                Intent intent = new Intent(Postre.this, HomeAdmin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
            else{
            	pDialog.dismiss();
            	generarToast("Los datos no fueron cargados");
            }
        }
	}

}

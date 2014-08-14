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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.AdaptadorListaComentariosEstadisticas;
import com.example.casinoutal.componentes.ObjetoComentarioEstadistica;

/**
 * Actividad que permite ver las alternativas de un determinado menu y fecha seleccionada previamente 
 */
public class VerListaEstadisticaEncuesta extends ActionBarActivity implements ActionBar.TabListener {
	static Bundle bundle;
	private ProgressDialog pDialog;
	
	
	private ArrayList <ObjetoComentarioEstadistica> comentarios = new ArrayList<>(); // array comentarios

	private static AdaptadorListaComentariosEstadisticas adaptadorComentarios; // adaptador comentarios
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	/**
	 * Carga y prepara los componentes de la vista.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vista_pagina_ver_menu);
		bundle = this.getIntent().getExtras();
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	    
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// Actualizo el ActionBar (botones y titulo)
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setTitle("Resumen mes encuesta");
		
	    adaptadorComentarios = new AdaptadorListaComentariosEstadisticas(this, comentarios);
		
	    Descargar d = new Descargar();
	    d.execute();

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Para cada tabs se agrega el icono correspondiente
		actionBar.addTab(actionBar.newTab().setText("ENCUESTA").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("COMENTARIOS").setTabListener(this));
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
	 * función que se debía sobreescribir (encaso de que hubieran menus) para 
	 * implementar la lógica de cada elemento del menú
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * para cancelar la función del boton atras
	 */
	@Override
	public void onBackPressed() {
		getSupportParentActivityIntent();
	}
	
	/**
	 * Clase que se encarga de manejar los distintos fragmentos (pestañas de la vista)
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		/**
		 * constructor de la clase.
		 * @param fm el administrador de los fragmentos de la vista.
		 */
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * getItem is called to instantiate the fragment for the given page.
		 * Return a PlaceholderFragment (defined as a static inner classbelow).
		 * @param position
		 * @return
		 */
		@Override
		public Fragment getItem(int position) { 
			return PlaceholderFragment.newInstance(position + 1);
		}

		/**
		 * retorna el número de pestañas que se mostrarán
		 * @return
		 */
		@Override
		public int getCount() {
			return 2;
		}
	}

	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {	
			
			View customView = null;
		    final int number = getArguments().getInt(ARG_SECTION_NUMBER);

		    /**
		     * si estamos en la vista de las entradas, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
		    if(number == 1) { // resultado de las encuestas de un mes en especifico
		    	
		    	customView = inflater.inflate(R.layout.resultados_encuesta,container, false);
      
			}

		    else if(number == 2) { // comentarios de las encuestas de un mes en especifico
		
				customView = inflater.inflate(R.layout.ver_comentarios,container, false);
				ListView lista_comentarios = (ListView) customView.findViewById(R.id.listview_comentarios);
				System.out.println("ERROR 1");
				lista_comentarios.setAdapter(adaptadorComentarios);
				System.out.println("ERROR 2");
				adaptadorComentarios.notifyDataSetChanged();
				
				// Cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_comentarios.setOnItemClickListener(new OnItemClickListener() { 
				    @Override
				    
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				    }
				});
			}
		    		
			return customView;
		}
	}

	/**
	 * Clase que descarga las puntuaciones de cada una de las escuestas realizadas.
	 */
	private class Descargar extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);
		double promedio1, promedio2, promedio3;
		int puno1 = 0, puno2 = 0, puno3 = 0, puno4 = 0, puno5 = 0;
		int pdos1 = 0, pdos2 = 0, pdos3 = 0, pdos4 = 0, pdos5 = 0;
		int ptres1 = 0, ptres2 = 0, ptres3 = 0, ptres4 = 0, ptres5 = 0;		
		
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
			String asd = "http://"+ip+"/casino_utalca/modelo/listar_encuesta_mes.php?" +
								"&mes="+bundle.getString("mes");
			System.out.println("Direccion JSON: " + asd);
			get = new HttpGet(asd);
		    get.setHeader("content-type", "application/json");
		    try{	
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++){
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
		            
		            // PREGUNTA 2 
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
			pDialog = new ProgressDialog(VerListaEstadisticaEncuesta.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Descargando datos de encuesta...");
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
			// PREGUNTA 1
			// creo el webview que mostrará el grafico de resultados
	        WebView webview1 = (WebView) findViewById(R.id.char_view1);
	        // determino la máx. cantidad de votos que ha reviso cada estrella de la pregunta.
	        int max1 = Math.max(puno1, Math.max(puno2, Math.max(puno3,Math.max(puno4,puno5))));
	        // formateo el promedio de nota a solo un decimal
	        // cargo el promedio en el ratingbar y en el textView
	        ((RatingBar) findViewById(R.id.prom_rat1)).setRating((float)(promedio1));
	        ((TextView) findViewById(R.id.tvProm1)).setText(Double.toString(promedio1).substring(0, 3));
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
	        WebView webview2 = (WebView) findViewById(R.id.char_view2);
	        
	        int max2 = Math.max(pdos1, Math.max(pdos2, Math.max(pdos3,Math.max(pdos4,pdos5))));
	        
	        ((RatingBar) findViewById(R.id.prom_rat2)).setRating((float)(promedio2));
	        ((TextView) findViewById(R.id.tvProm2)).setText(Double.toString(promedio2).substring(0, 3));
	        
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
	        WebView webview3 = (WebView) findViewById(R.id.char_view3);
	        
	        int max3 = Math.max(ptres1, Math.max(ptres2, Math.max(ptres3,Math.max(ptres4,ptres5))));
	        
	        ((RatingBar) findViewById(R.id.prom_rat3)).setRating((float)(promedio3));
	        ((TextView) findViewById(R.id.tvProm3)).setText(Double.toString(promedio3).substring(0, 3));
	        
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
			pDialog.dismiss();
	        Descargar1 c1 = new Descargar1();
	        c1.execute();
		}
	}
	
	/**
	 * Clase que descarga los comentarios de cada una de las escuestas realizadas.
	 */
	private class Descargar1 extends AsyncTask<Void, Integer, Boolean>{
		String ip = getString(R.string.ip);
		
		public Descargar1(){	
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
			String asd = "http://"+ip+"/casino_utalca/modelo/listar_comentario_encuesta_mes.php?" +
								"&mes="+bundle.getString("mes");
			System.out.println("Direccion JSON: " + asd);
			get = new HttpGet(asd);
		    get.setHeader("content-type", "application/json");
		    try {	
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++){
		        	JSONObject obj = respJSON.getJSONObject(i);
		        	
		        	String fechaEncuesta = obj.getString("fecha_encuesta");
		        	System.out.println("Fecha: " + fechaEncuesta + "");
		        	String nombreUsuario = obj.getString("nombre_usuario");
		        	System.out.println("Usuario: " + nombreUsuario + "");
		        	String comentarioEncuesta = obj.getString("comentario_encuesta");
		        	System.out.println("Comentario: " + comentarioEncuesta + "");

		        	comentarios.add(new ObjetoComentarioEstadistica(nombreUsuario,fechaEncuesta,comentarioEncuesta));
		        	System.out.println("");
		            publishProgress(i*4);
		        }
		        // si las listas no tienen elementos se crea un elemento que notifique que no hay alternativas disponibles.
		        if(comentarios.isEmpty()){
		        	comentarios.add(new ObjetoComentarioEstadistica("No hay comentarios registrados", "0", "0"));
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
			pDialog = new ProgressDialog(VerListaEstadisticaEncuesta.this);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setMessage("Descargando datos de comentarios...");
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
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}

	 @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
}

package com.example.casinoutal.ver_menu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.casinoutal.R;
import com.example.casinoutal.componentes.AdaptadorListaElementos;
import com.example.casinoutal.componentes.ObjetoElemento;

/**
 * Actividad que permite ver las alternativas de un determinado menu y fecha seleccionada previamente 
 */
public class VerMenu extends ActionBarActivity implements ActionBar.TabListener {
	static Bundle bundle;
	private ProgressDialog pDialog;
	private ArrayList <ObjetoElemento> ets = new ArrayList<>();
	private ArrayList <ObjetoElemento> pfs = new ArrayList<>();
	private ArrayList <ObjetoElemento> acs = new ArrayList<>();
	private ArrayList <ObjetoElemento> ens = new ArrayList<>();
	private ArrayList <ObjetoElemento> pos = new ArrayList<>();
	private static AdaptadorListaElementos adaptadorEntrada; //entradas
	private static AdaptadorListaElementos adaptadorPlatoFondo; //platos de fondo
	private static AdaptadorListaElementos adaptadorAcompanamiento; //acompañamientos
	private static AdaptadorListaElementos adaptadorEnsalada; //ensaladas
	private static AdaptadorListaElementos adaptadorPostre; //postres
	private static String fecha;

    
	
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
		bundle = getIntent().getExtras();
		fecha = bundle.getString("fecha");
		if(bundle.getString("menu").equals("1")){
			this.setTitle("Menú Básico");
		}
		else if(bundle.getString("menu").equals("2")){
			this.setTitle("Menú Hipocalórico");
		} 
		else if(bundle.getString("menu").equals("3")){
			this.setTitle("Menú Vegetariano");
		} 
		else if(bundle.getString("menu").equals("4")){
			this.setTitle("Menú Ejecutivo");
		} 
		
		// Adaptadores para las listas
		adaptadorEntrada = new AdaptadorListaElementos(this, ets);
		adaptadorPlatoFondo = new AdaptadorListaElementos(this, pfs);
		adaptadorAcompanamiento = new AdaptadorListaElementos(this, acs);
		adaptadorEnsalada = new AdaptadorListaElementos(this, ens);
		adaptadorPostre = new AdaptadorListaElementos(this, pos);	
		
		cargarDatos();

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setIcon(R.drawable.et).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.pf).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ac).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.en).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setIcon(R.drawable.po).setTabListener(this));
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
	 * función que se debía sobreescribir (encaso de que hubieran menus)
	 * @param menu
	 * @return
	 */
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ver_menu, menu);
		return true;
	}*/

	/**
	 * función que se debía sobreescribir (encaso de que hubieran menus) para 
	 * implementar la lógica de cada elemento del menú
	 * @param item
	 * @return
	 */
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	/**
	 * para la función del boton atras
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
			return 5;
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
		
		/**
		 * metodo que retorna la fecha actual en formato string
		 * @return la fecha actual
		 */
	    public String getFechaActual() {
	        Date ahora = new Date();
	        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
	        return formateador.format(ahora);
	    }	
		
	    
	    /**
	     * metodo que comprar dos fechas
	     * @param fecha1 es la fecha ingresada en el datepicker
	     * @param fechaActual es la fecha actual (del dia)
	     * @return true si la fecha ingresada es menor a la actual, sino false
	     * @throws java.text.ParseException
	     */
	    public boolean compararFechasConDate(String fecha1, String fechaActual) throws java.text.ParseException {  

	    	   /**Obtenemos las fechas enviadas en el formato a comparar*/
	    	  SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
	    	  Date fechaDate1 = formateador.parse(fecha1);
	    	  Date fechaDate2 = formateador.parse(fechaActual);
	    	    
	    	  if ( fechaDate1.before(fechaDate2) ){
	    		  return true; // si es menor
	    	  }
	    	  else{
		    	     if ( fechaDate2.before(fechaDate1) ){
		    	         return false; // si es mayor
		    	     }else{
		    	         return true; // si son iguales
		    	     } 
	    	    }
	    	 }
	    	    
	    
	    
	    
		/**
		 * formatea la fecha al siguente estilo AAAA-MM-DD 
		 * @param fecha Fecha a formatear
		 * @return la fecha con el formato deseado
		 */
		public String parsearFecha(String fecha){
			String [] values = fecha.split("/");
			String anio = values[0];
			int auxMes = Integer.parseInt(values[1]) + 1;
			values[1] = auxMes+"";
			String mes = (values[1].length() == 1) ? ("0"+values[1]): (values[1]);
			String dia = (values[2].length() == 1) ? ("0"+values[2]): (values[2]);
			return anio+"-"+mes+"-"+dia;
		}		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {	
			
			View rootView = null;
			
			String menuEscogido = bundle.getString("menu");
			
		    final int number = getArguments().getInt(ARG_SECTION_NUMBER);
	    	final String fechaSel = parsearFecha(fecha);
	    	final String fechaActual = getFechaActual();

		    /**
		     * si estamos en la vista de las entradas, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
		    if(number == 1) {
				rootView = inflater.inflate(R.layout.ver_entradas,container, false);
				ListView lista_et = (ListView) rootView.findViewById(R.id.listview_ver_entrada);
				lista_et.setAdapter(adaptadorEntrada);
				adaptadorEntrada.notifyDataSetChanged();

				
				// cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_et.setOnItemClickListener(new OnItemClickListener() {
				    @Override
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				    	String opcionSeleccionada = ((ObjetoElemento)a.getAdapter().getItem(position)).getTitulo();
				    	int idSeleccionado = ((ObjetoElemento)a.getAdapter().getItem(position)).getId();

				    	// validamos que sea un componente válido
				    	try {
							if(!opcionSeleccionada.equals("No hay alternativas disponibles") && compararFechasConDate(fechaSel,fechaActual)){
							    Context ActivityVerMenu = getActivity();
								Intent intent = new Intent(ActivityVerMenu , Detalle.class);
								bundle.putInt("TIPO", (number-1));
								bundle.putString("ELEMENTO", opcionSeleccionada);
								bundle.putInt("ID", idSeleccionado);
								bundle.putString("fecha", fecha);
								intent.putExtras(bundle);
								startActivity(intent);	
								getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});
			}
		    /**
		     * si estamos en la vista de los Platos de fondo, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
		    else if(number == 2) {
				rootView = inflater.inflate(R.layout.ver_platos_fondo,container, false);
				ListView lista_pf = (ListView) rootView.findViewById(R.id.listview_ver_platofondo);
				lista_pf.setAdapter(adaptadorPlatoFondo);
				adaptadorPlatoFondo.notifyDataSetChanged();
				
				// cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_pf.setOnItemClickListener(new OnItemClickListener() {
				    @Override
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				    	String opcionSeleccionada = ((ObjetoElemento)a.getAdapter().getItem(position)).getTitulo();
				    	int idSeleccionado = ((ObjetoElemento)a.getAdapter().getItem(position)).getId();
				    	try {
							if(!opcionSeleccionada.equals("No hay alternativas disponibles") && compararFechasConDate(fechaSel,fechaActual)){
							    Context ActivityVerMenu = getActivity();
								Intent intent = new Intent(ActivityVerMenu , Detalle.class);
								bundle.putInt("TIPO", (number-1));
								bundle.putString("ELEMENTO", opcionSeleccionada);
								bundle.putInt("ID", idSeleccionado);
								bundle.putString("fecha", fecha);
								intent.putExtras(bundle);
								startActivity(intent);	
								getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});				
			}
		    /**
		     * si estamos en la vista de los acompañamientos, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
			else if(number == 3) {
				rootView = inflater.inflate(R.layout.ver_acompanamientos,container, false);
				ListView lista_ac = (ListView) rootView.findViewById(R.id.listview_ver_acompanamiento);
				lista_ac.setAdapter(adaptadorAcompanamiento);
				adaptadorAcompanamiento.notifyDataSetChanged();
				
				// cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_ac.setOnItemClickListener(new OnItemClickListener() {
				    @Override
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				    	String opcionSeleccionada = ((ObjetoElemento)a.getAdapter().getItem(position)).getTitulo();
				    	int idSeleccionado = ((ObjetoElemento)a.getAdapter().getItem(position)).getId();
				    	try {
							if(!opcionSeleccionada.equals("No hay alternativas disponibles") && compararFechasConDate(fechaSel,fechaActual)){
							    Context ActivityVerMenu = getActivity();
								Intent intent = new Intent(ActivityVerMenu , Detalle.class);
								bundle.putInt("TIPO", (number-1));
								bundle.putString("ELEMENTO", opcionSeleccionada);
								bundle.putInt("ID", idSeleccionado);
								bundle.putString("fecha", fecha);
								intent.putExtras(bundle);
								startActivity(intent);
								getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});				
			}
		    /**
		     * si estamos en la vista de los ensaladas, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
			else if(number == 4) {
				rootView = inflater.inflate(R.layout.ver_ensaladas,container, false);
				ListView lista_en = (ListView) rootView.findViewById(R.id.listview_ver_ensalada);
				lista_en.setAdapter(adaptadorEnsalada);
				adaptadorEnsalada.notifyDataSetChanged();
				
				// cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_en.setOnItemClickListener(new OnItemClickListener() {
				    @Override
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				    	String opcionSeleccionada = ((ObjetoElemento)a.getAdapter().getItem(position)).getTitulo();
				    	int idSeleccionado = ((ObjetoElemento)a.getAdapter().getItem(position)).getId();
				    	try {
							if(!opcionSeleccionada.equals("No hay alternativas disponibles") && compararFechasConDate(fechaSel,fechaActual)){
							    Context ActivityVerMenu = getActivity();
								Intent intent = new Intent(ActivityVerMenu , Detalle.class);
								bundle.putInt("TIPO", (number-1));
								bundle.putString("ELEMENTO", opcionSeleccionada);
								bundle.putInt("ID", idSeleccionado);
								bundle.putString("fecha", fecha);
								intent.putExtras(bundle);
								startActivity(intent);	
								getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});				
			}
		    /**
		     * si estamos en la vista de los postres, cargamos el fragment correspondiente
		     * y cargamos la lista con los datos descargados del servidor.
		     */
			else if(number == 5) {
				rootView = inflater.inflate(R.layout.ver_postres,container, false);
				ListView lista_po = (ListView) rootView.findViewById(R.id.listview_ver_postre);
				lista_po.setAdapter(adaptadorPostre);
				adaptadorPostre.notifyDataSetChanged();
				
				// cuando selecciona un elemento pasamos a la activity que muestra el detalle del componente del menu
				lista_po.setOnItemClickListener(new OnItemClickListener() {
				    @Override
				    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				    	String opcionSeleccionada = ((ObjetoElemento)a.getAdapter().getItem(position)).getTitulo();
				    	int idSeleccionado = ((ObjetoElemento)a.getAdapter().getItem(position)).getId();
				    	try {
							if(!opcionSeleccionada.equals("No hay alternativas disponibles") && compararFechasConDate(fechaSel,fechaActual)){
							    Context ActivityVerMenu = getActivity();
								Intent intent = new Intent(ActivityVerMenu , Detalle.class);
								bundle.putInt("TIPO", (number-1));
								bundle.putString("ELEMENTO", opcionSeleccionada);
								bundle.putInt("ID", idSeleccionado);
								intent.putExtras(bundle);
								bundle.putString("fecha", fecha);
								startActivity(intent);		
								getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});				
			}		
			return rootView;
		}
	}

	
	/**
	 * Se encarga de hacer el llamado a la clase que descargará los componentes en segundo plano
	 */
	public void cargarDatos(){
		pDialog = new ProgressDialog(VerMenu.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Descargando datos...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        
        Descargar c = new Descargar();
        c.execute();
	}
	
	/**
	 * Clase que descarga los elementos de un menu y los carga en las listas correspondientes
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
			String asd = "http://"+ip+"/casino_utalca/modelo/listar_menu_fecha_tipo.php?fecha_menu=" + 
					bundle.getString("fecha").replace("/", "-") + "&tipo_menu=" + bundle.getString("menu");
			System.out.println("ruta: "+asd);
			get = new HttpGet(asd);
		    get.setHeader("content-type", "application/json");
		    try
		    {	
		        HttpResponse resp = httpClient.execute(get);
		        String respStr = EntityUtils.toString(resp.getEntity());
		        JSONArray respJSON = new JSONArray(respStr);
		        for(int i=0; i<respJSON.length(); i++)
		        {
		        	JSONObject obj = respJSON.getJSONObject(i);
		            String tipo = obj.getString("tipo_componente");
		            int id = Integer.parseInt(obj.getString("id"));
		            String nombre = obj.getString("nombre");
		            System.out.println("tipo: "+tipo+" nombre: "+nombre);

		            // dependiendo del tipo de componente (entrada, pf, acomp...) se crea un objecto soportado por el adaptador
		            // de la lista de elementos del menu (icono + texto) y se carga en el adaptador que corresponda
		            if(tipo.equals("0")) {
		            	ets.add(new ObjetoElemento(id, nombre, getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(0, -1)));
		            }
		            else if(tipo.equals("1")) {
		            	pfs.add(new ObjetoElemento(id, nombre, getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(1, -1)));
		            }
		            else if(tipo.equals("2")) {
		            	acs.add(new ObjetoElemento(id, nombre, getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(2, -1)));
		            }
		            else if(tipo.equals("3")) {
		            	ens.add(new ObjetoElemento(id, nombre, getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(3, -1)));
		            }
		            else if(tipo.equals("4")) {
		            	pos.add(new ObjetoElemento(id, nombre, getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(4, -1)));
		            }
		            
		            publishProgress(i*4);
		        }
		        
		        // si las listas no tienen elementos se crea un elemento que notifique que no hay alternativas disponibles.
		        if(ets.isEmpty()){
		        	ets.add(new ObjetoElemento("No hay alternativas disponibles", getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(0, -1)));
		        }
		        if(acs.isEmpty()){
		        	acs.add(new ObjetoElemento("No hay alternativas disponibles", getResources().obtainTypedArray(R.array.componentes_iconos).getResourceId(2, -1)));
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
            pDialog.setProgress(0);
            pDialog.show();
        }

		/**
		 * Una vez finalizada la descarga cierra el JDialog y notifica a los adaptadores que se actualizaron sus datos
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			adaptadorEntrada.notifyDataSetChanged();
		    adaptadorPlatoFondo.notifyDataSetChanged();
		    adaptadorAcompanamiento.notifyDataSetChanged();
		    adaptadorEnsalada.notifyDataSetChanged();
		    adaptadorPostre.notifyDataSetChanged();

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

package com.example.casinoutal.componentes;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casinoutal.R;


/**
 * Esta clase se utiliza como adaptador de la listas para mostrar los ObjectElemento
 * (agregados a esta clase). Es decir, permite a un ListView reconocer los objectos que hemos creado.
 * En este caso para que se muestre un ícono y un texto en un mismo elemento.
 */
public class AdaptadorListaElementosEstadisticas extends BaseAdapter{ 
    protected Activity activity;
    protected ArrayList<ObjetoElementoEstadisticas> items;
 
    /**
     * Constructor de la clase. 
     * @param activity activity en la que se mostrarán los elementos
     * @param items lista de elementos que se mostrarán en el listview
     */
    public AdaptadorListaElementosEstadisticas(Activity activity, ArrayList<ObjetoElementoEstadisticas> items) {
        this.activity = activity;
        this.items = items;
    }
 
    /**
     * Retorna el número de elementos que contiene el adaptador.
     */
    @Override
    public int getCount() {
        return items.size();
    }
    
    /**
     * Retorna el elemento que se encuentra en una determinada posición de la lista.
     */
    @Override
    public Object getItem(int pos) {
        return items.get(pos);
    }
 
    /**
     * Retorna la posición de un elemento.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    /**
     * Contiene los componentes que se mostrarán en una fila del listView
     */
    public static class Fila{  
    	TextView nombre;
    	TextView promedio;
    } 
    
    /**
     * Genera la vista para un componente determinado que se mostrará en el ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = activity.getLayoutInflater();  
        	
        //obtengo de la lista del adaptado el objeto al que le generaremos la vista
        ObjetoElementoEstadisticas itm=items.get(position);
        convertView = inflator.inflate(R.layout.elemento_estadistica, null);
        //cargamos en la vista el promedio del objecto
        TextView promedio = (TextView) convertView.findViewById(R.id.promedio);
        if(itm.getPromedio().equals("0.0")){
        	promedio.setText("  -  ");
        }
        else{
            promedio.setTextColor(Color.BLACK);
            promedio.setText(itm.getPromedio());
        }
        //cargamos en la vista el nombre
        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        nombre.setTextColor(Color.BLACK);
        nombre.setText(itm.getNombre());
       
        return convertView;  
     }
 }
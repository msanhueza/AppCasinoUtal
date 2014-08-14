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
public class AdaptadorListaElementos extends BaseAdapter{ 
    protected Activity activity;
    protected ArrayList<ObjetoElemento> items;
 
    /**
     * Constructor de la clase. 
     * @param activity activity en la que se mostrarán los elementos
     * @param items lista de elementos que se mostrarán en el listview
     */
    public AdaptadorListaElementos(Activity activity, ArrayList<ObjetoElemento> items) {
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
    	TextView titulo_itm;
    	ImageView icono;
    } 
    
    /**
     * Genera la vista para un componente determinado que se mostrará en el ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        LayoutInflater inflator = activity.getLayoutInflater();  
        	
        //obtengo de la lista del adaptado el objeto al que le generaremos la vista
        ObjetoElemento itm = items.get(position);
        convertView = inflator.inflate(R.layout.elemento, null);
        //cargamos en la vista el texto del objecto
        TextView tv = (TextView) convertView.findViewById(R.id.title_item);
        tv.setTextColor(Color.BLACK);
        tv.setText(itm.getTitulo());           
        //cargamos en la vista el icono del objecto
        ImageView img = (ImageView) convertView.findViewById(R.id.icon);
        img.setImageResource(itm.getIcono());
        
        return convertView;  
     }
 }
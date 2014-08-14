package com.example.casinoutal.componentes;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.casinoutal.R;
 
/**
 * Adaptador utilizado para mostrar los comentarios realizados para un componente del menu.
 */
public class AdaptadorListaComentarios extends BaseAdapter{
    protected Activity activity;
    protected ArrayList<ObjetoComentario> items;
 
    /**
     * Constructor de la clase.
     * @param activity Acivity en la que se mostrará el listView asociado a este adaptador
     * @param items los elementos que se cargarán en la lista.
     */
    public AdaptadorListaComentarios(Activity activity, ArrayList<ObjetoComentario> items) {
        this.activity = activity;
        this.items = items;
    }
 
    /**
     * Retorna el tamaño de la lista de elementos que se mostrarán
     */
    @Override
    public int getCount() {
        return items.size();
    }
 
    /**
     * retorna el elemento que se encuentra en una determinada posición 
     */
    @Override
    public Object getItem(int pos) {
        return items.get(pos);
    }
 
    /**
     * retorna el id del elemento que se encuentra en una determinada posición
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    /**
     * Crea los Componentes de la vista que corresponden a un elemento de la vista.
     * En este caso un texto con el nombre del usuario, un texto con el comentario y 
     * un RatingBar que muestra la valoración dada por el usuario a un determinado 
     * componente del menu.
     */
    public static class Fila{  
		TextView usuario;
		RatingBar valoracion;
		TextView comentario;
    } 
    
    /**
     * Genera la vista para un componente determinado que se mostrará en el ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	 
        LayoutInflater inflator = activity.getLayoutInflater();  
           
        ObjetoComentario itm=items.get(position);
        convertView = inflator.inflate(R.layout.comentario, null);
        
        TextView usuario = (TextView) convertView.findViewById(R.id.usuario);
        if(itm.getValoracion()!=0){
        	usuario.setText(itm.getUsuario());
        }
        else{
        	usuario.setVisibility(View.GONE);
        }

        RatingBar valoracion = (RatingBar) convertView.findViewById(R.id.valoracion);
        if(itm.getValoracion()!=0){
        	valoracion.setRating(itm.getValoracion());
        }
        else{
        	valoracion.setVisibility(View.GONE);
        } 
        
        TextView comentario =  (TextView) convertView.findViewById(R.id.comentario);
        
        comentario.setText(itm.getComentario());
           
        return convertView;  
     }
 }
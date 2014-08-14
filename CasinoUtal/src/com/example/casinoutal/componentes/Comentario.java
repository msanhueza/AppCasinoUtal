package com.example.casinoutal.componentes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Esta clase extiende a EditText y se modifica para mostras el numero de caracteres restantes a medida el usuario escribe su comentario.
 */
public class Comentario extends EditText {
	private Paint p1;
	private Paint p2;
	private float escala;

	/**
	 * Constructor de la clase. 
	 * @param context contexto actual de la aplicación.
	 * @param attrs 
	 * @param defStyle
	 */
	public Comentario(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		inicializacion();
	}

	/**
	 * Constructor de la clase. Esta clase extiende a EditText y se modifica para mostras el numero
	 * de caracteres restantes a medida el usuario escribe su comentario.
	 * @param context Contexto actual de la aplicación.
	 * @param attrs
	 */
	public Comentario(Context context, AttributeSet attrs) {
		super(context, attrs);
		inicializacion();
	}

	/**
	 * Constructor de la clase. Esta clase extiende a EditText y se modifica para mostras el numero
	 * de caracteres restantes a medida el usuario escribe su comentario.
	 * @param context Contexto actual de la aplicación
	 */
	public Comentario(Context context) {
		super(context);
		inicializacion();
	}

	/**
	 * Se encarga de la inicialización de los valores de estilo a utilizar en el editText que se creará.
	 */
	private void inicializacion(){
		p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p1.setColor(Color.BLACK);
		p1.setStyle(Style.FILL);

		p2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		p2.setColor(Color.WHITE);
		p2.setTextSize(18);

		escala = getResources().getDisplayMetrics().density;
	}

	/**
	 * Se encarga de repintar los componentes que se mostrarán en el editText. En este caso se pinta el recuadro
	 * que mostrará los caracteres restantes que puede tipear el usuario.
	 */
	@Override
	public void onDraw(Canvas canvas) {
		//Llamamos al método de la clase base (EditText)
		super.onDraw(canvas);

		//Dibujamos el fondo negro del contador
		canvas.drawRect(this.getWidth()-30*escala, 5*escala, 
				this.getWidth()-5*escala, 20*escala, p1) ;

		//Dibujamos el número de caracteres sobre el contador
		canvas.drawText("" + (140-this.getText().toString().length()), 
				this.getWidth()-28*escala, 17*escala, p2);
	}
}

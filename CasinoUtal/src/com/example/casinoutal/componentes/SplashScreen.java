package com.example.casinoutal.componentes;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;

import com.example.casinoutal.R;
import com.example.casinoutal.autenticacion.Autenticacion;
import com.example.casinoutal.autenticacion.HomeAdmin;
import com.example.casinoutal.autenticacion.HomeUser;
 
/**
 * Actividad que se muestra al iniciar la aplicación. Muestra la imagen que identifica
 * a nuestro producto.
 */
public class SplashScreen extends Activity {
    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private int isAdmin;
 
    /**
     * Carga la vista de nuestra aplicación y la muestra por un tiempo determinado.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bloqueamos el giro de la pantalla.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Escondemos la barra del titulo de la aplicación
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        // la tarea que se realizará una vez finalice el tiempo de presentación de la aplicación
        isAdmin = 0;
    	BaseSQLiteCasinoUtalca bbdd; //base de datos interna
    	String dbInterna = getString(R.string.db_interna);
		bbdd = new BaseSQLiteCasinoUtalca(getApplicationContext(), dbInterna, null, 1);
		SQLiteDatabase db = bbdd.getWritableDatabase();
		if(db!=null){
			Cursor c = db.rawQuery("SELECT * FROM user", null);
			if(c.moveToFirst()){
				String is_admin = c.getString(3);
				System.out.println("es admin? "+is_admin);
				if(is_admin.equals("true")){
					isAdmin = 1;
                }
                else{
                	isAdmin = 2;
                }						
			}
		}
		db.close();        
        
        TimerTask task = new TimerTask() {
        	/**
        	 * Cerrará la actual activity y pasara a la de autenticación de usuario.
        	 */
            @Override
            public void run() {
				Intent intent;
				if(isAdmin==1){
                	intent = new Intent(SplashScreen.this, HomeAdmin.class);
                	startActivity(intent);
                }
                else if(isAdmin==2){
                	intent = new Intent(SplashScreen.this, HomeUser.class);
                	startActivity(intent);
                }
                else{
                	intent = new Intent(SplashScreen.this, Autenticacion.class);
                	startActivity(intent);
                }
                finish();
            }
        };

        // el cronometro que contará el tiempo que se muestra la presentación
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
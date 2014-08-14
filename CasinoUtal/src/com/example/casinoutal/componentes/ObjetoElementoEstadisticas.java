package com.example.casinoutal.componentes;

/**
 * Este elemento se utiliza en las listas de los componentes del menú. Posee un id, el texto a mostrar y el ícono que lo acompañara.
 */
public class ObjetoElementoEstadisticas implements Comparable<ObjetoElementoEstadisticas> {
	private int id;
	private String nombre;
	private String promedio;
	
	/**
	 * Constructor de la clase. 
	 * @param id identificador del elemento
	 * @param titulo texto que se mostrará en la lista.
	 * @param icono imagen que acompaña al texto
	 */
	public ObjetoElementoEstadisticas(int id, String nombre, String promedio) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.promedio = promedio;
	}
	
	/**
	 * Constructor de la casa. Este elemento se utiliza en las listas de los componentes del menú.
	 * @param titulo texto que se mostrará en la lista.
	 * @param icono imagen que acompaña al texto
	 */
	public ObjetoElementoEstadisticas(String nombre, String promedio) {
		super();
		this.nombre = nombre;
		this.promedio = promedio;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPromedio() {
		return promedio;
	}

	public void setPromedio(String promedio) {
		this.promedio = promedio;
	}

	@Override
	public int compareTo(ObjetoElementoEstadisticas obj) {
       double classProm = Double.parseDouble(this.promedio);
       double objProm = Double.parseDouble(obj.getPromedio());
       if(classProm < objProm){
    	   return 1;
       }
       if(classProm == objProm){
    	   return 1;
       }  
       else{
    	   return -1;
       }
	}


	
	
}

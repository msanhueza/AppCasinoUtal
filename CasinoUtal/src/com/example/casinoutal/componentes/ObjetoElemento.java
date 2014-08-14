package com.example.casinoutal.componentes;

/**
 * Este elemento se utiliza en las listas de los componentes del menú. Posee un id, el texto a mostrar y el ícono que lo acompañara.
 */
public class ObjetoElemento {
	private int id;
	private String titulo;
	private int icono;
	
	/**
	 * Constructor de la clase. 
	 * @param id identificador del elemento
	 * @param titulo texto que se mostrará en la lista.
	 * @param icono imagen que acompaña al texto
	 */
	public ObjetoElemento(int id, String nombre, int icono) {
		super();
		this.id = id;
		this.titulo = nombre;
		this.icono = icono;
	}
	
	/**
	 * Constructor de la casa. Este elemento se utiliza en las listas de los componentes del menú.
	 * @param titulo texto que se mostrará en la lista.
	 * @param icono imagen que acompaña al texto
	 */
	public ObjetoElemento(String nombre, int icono) {
		super();
		this.titulo = nombre;
		this.icono = icono;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

}

package com.example.casinoutal.componentes;


/**
 * Este objeto se utiliza para mostrar cada comentario en la lista de comentarios de un elemento del menú. 
 */
public class ObjetoComentario {
	private int id;
	private String usuario;
	private int valoracion;
	private String comentario;
	
	/**
	 * Constructor de la clase. 
	 * @param id identificador del componente
	 * @param usuario Persona que realizó el comentario.
	 * @param valoracion Valoración otorgada por el usuario al componente del menú
	 * @param comentario Opinion emitida por el usuario sobre el componente del menú.
	 */
	public ObjetoComentario(int id, String usuario, int valoracion, String comentario) {
		super();
		this.id = id;
		this.usuario = usuario;
		this.valoracion = valoracion;
		this.comentario = comentario;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public int getValoracion() {
		return valoracion;
	}

	public void setValoracion(int valoracion) {
		this.valoracion = valoracion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
}

package com.example.casinoutal.componentes;


/**
 * Este objeto se utiliza para mostrar cada comentario en la lista de comentarios de un elemento del menú. 
 */
public class ObjetoComentarioEstadistica {
	private String usuario;
	private String fecha;
	private String comentario;
	
	/**
	 * Constructor de la clase. 
	 * @param id identificador del componente
	 * @param usuario Persona que realizó el comentario.
	 * @param valoracion Valoración otorgada por el usuario al componente del menú
	 * @param comentario Opinion emitida por el usuario sobre el componente del menú.
	 */
	public ObjetoComentarioEstadistica(String usuario, String fecha, String comentario) {
		super();
		this.usuario = usuario;
		this.fecha = fecha;
		this.comentario = comentario;
	}
	

	public String getFecha() {
		return fecha;
	}


	public void setFecha(String fecha) {
		this.fecha = fecha;
	}


	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
}

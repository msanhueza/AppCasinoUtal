package com.example.casinoutal.componentes;


/**
 * Este objeto se utiliza para mostrar cada comentario en la lista de comentarios de un elemento del men�. 
 */
public class ObjetoComentarioEstadistica {
	private String usuario;
	private String fecha;
	private String comentario;
	
	/**
	 * Constructor de la clase. 
	 * @param id identificador del componente
	 * @param usuario Persona que realiz� el comentario.
	 * @param valoracion Valoraci�n otorgada por el usuario al componente del men�
	 * @param comentario Opinion emitida por el usuario sobre el componente del men�.
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

package miquilini.felipe.servidor.exception;

public class NomeJaEmUsoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2365501294095367670L;
	
	public NomeJaEmUsoException() {
		super("Este nome já está em uso");
	}
}

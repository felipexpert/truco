package miquilini.felipe.servidor.subsystem.mesa.exception;

public class JaTrucouException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6877087084206066806L;

	
	public JaTrucouException() {
		super("Seu time foi o último a trucar");
	}
}

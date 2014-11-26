package miquilini.felipe.servidor.subsystem.mesa.exception;

public class NaoPossuiAVezException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7019274205150385099L;
	
	public NaoPossuiAVezException() {
		super("Você não possui a vêz");
	}

}

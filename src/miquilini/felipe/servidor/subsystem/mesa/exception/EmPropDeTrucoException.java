package miquilini.felipe.servidor.subsystem.mesa.exception;

public class EmPropDeTrucoException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4382074171828945693L;

	public EmPropDeTrucoException() {
		super("Em proposta de truco, espere a confirmacao");
	}
}

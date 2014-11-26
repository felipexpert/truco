package miquilini.felipe.servidor.exception;

public class ComandoInexistenteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7334248257108337299L;

	public ComandoInexistenteException() {
		super("Comando inexistente");
	}
}

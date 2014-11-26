package miquilini.felipe.servidor.subsystem.mesa.exception;

public class NaoRecebeuPropException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 933585667404319667L;
	
	public NaoRecebeuPropException() {
		super("Você não recebeu proposta de truco");
	}
}

package miquilini.felipe.servidor.subsystem.mesa.exception;

public class NaoPodeTrucarNaPrimeiraRodadaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3144089902406067071L;
	
	public NaoPodeTrucarNaPrimeiraRodadaException() {
		super("Não pode trucar na primeira rodada (Rodada Inicial)");
	}

}

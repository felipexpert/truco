package miquilini.felipe.servidor.subsystem.rodada;

public class IncobertoRodadaInicialException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7466269178011726073L;

	public IncobertoRodadaInicialException() {
		super("Não jogue incoberto na rodada inicial");
	}
}

/**
 * Classe permettant de créer une exception particulière qui sera levée lors de la fin d'une connection
 */

package client;

public class EndConnectionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public EndConnectionException() {
		
	}

	public EndConnectionException(String message){
		super(message);
	}

}

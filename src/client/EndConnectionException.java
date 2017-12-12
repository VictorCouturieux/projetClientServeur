package client;

public class EndConnectionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public EndConnectionException() {
		
	}

	public EndConnectionException(String message){
		super(message);
	}

}

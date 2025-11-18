package service.exceptions;
public class RegistroNoEncontradoException extends RuntimeException{

    public RegistroNoEncontradoException(String message) {
        super(message);
    }

    public RegistroNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
    
}

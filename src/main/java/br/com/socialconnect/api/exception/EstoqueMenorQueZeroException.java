package br.com.socialconnect.api.exception;

public class EstoqueMenorQueZeroException extends RuntimeException {
    public EstoqueMenorQueZeroException() {
        super("O estoque não pode ser negativo");
    }

    public EstoqueMenorQueZeroException(String message) {
        super(message);
    }
}

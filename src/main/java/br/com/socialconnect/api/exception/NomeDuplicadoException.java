package br.com.socialconnect.api.exception;

public class NomeDuplicadoException extends RuntimeException {

    private final String nome;

    public NomeDuplicadoException(String nome) {
        super("Nome já cadastrado: " + nome);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}

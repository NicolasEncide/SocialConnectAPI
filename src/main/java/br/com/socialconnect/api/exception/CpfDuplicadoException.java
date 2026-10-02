package br.com.socialconnect.api.exception;

public class CpfDuplicadoException extends RuntimeException {

    private final String cpf;

    public CpfDuplicadoException(String cpf) {
        super("CPF já cadastrado: " + cpf);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }
}

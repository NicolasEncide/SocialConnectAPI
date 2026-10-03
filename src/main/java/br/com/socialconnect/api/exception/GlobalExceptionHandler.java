package br.com.socialconnect.api.exception;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ProblemDetail.FieldError> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(
                        e.getField(),
                        e.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/validacao",
                "Erro de validação",
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos são inválidos.",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleCpfDuplicado(
            CpfDuplicadoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/cpf-duplicado",
                "CPF já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(NomeDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleNomeDuplicado(
            NomeDuplicadoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nome-duplicado",
                "Nome já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(EstoqueMenorQueZeroException.class)
    public ResponseEntity<ProblemDetail> handleEstoqueMenorQueZero(
            EstoqueMenorQueZeroException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/estoque-invalido",
                "Estoque não pode ser negativo",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            InvalidDataAccessApiUsageException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ProblemDetail> handleParametroInvalido(
            Exception ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/parametro-invalido",
                "Parâmetro de consulta inválido",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenerico(
            Exception ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/erro-interno",
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado. Tente novamente.",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}

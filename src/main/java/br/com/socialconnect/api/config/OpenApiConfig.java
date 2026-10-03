package br.com.socialconnect.api.config;

import br.com.socialconnect.api.beneficiarios.controller.BeneficiarioController;
import br.com.socialconnect.api.doacoes.controller.DoacaoController;
import br.com.socialconnect.api.doadores.controller.DoadorController;
import br.com.socialconnect.api.produtos.controller.ProdutoController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SocialConnect API")
                        .description("API RESTful de gestão para instituições sociais")
                        .version("v1"));
    }

    @Bean
    public OperationCustomizer customizePageableSort() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() != null) {
                String sortExample = "id,asc";
                if (handlerMethod.getBeanType().equals(DoacaoController.class)) {
                    sortExample = "dataDoacao,desc";
                } else if (handlerMethod.getBeanType().equals(BeneficiarioController.class) ||
                           handlerMethod.getBeanType().equals(DoadorController.class) ||
                           handlerMethod.getBeanType().equals(ProdutoController.class)) {
                    sortExample = "nome,asc";
                }
                final String example = sortExample;

                operation.getParameters().stream()
                        .filter(parameter -> "sort".equals(parameter.getName()))
                        .forEach(parameter -> {
                            parameter.setSchema(new StringSchema()
                                    .description("Critério de ordenação no formato: propriedade(,asc|desc). Exemplo: " + example)
                                    .example(example));
                        });
            }
            return operation;
        };
    }
}

package br.com.socialconnect.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * Filtro para sanitizar o parâmetro de query 'sort' enviado pelo Swagger UI ou clientes REST.
 * Remove colchetes (ex: ["nome,asc"] ou [ "string" ]) e aspas que costumam causar PropertyReferenceException e HTTP 500/400.
 * Além disso, previne erro caso o exemplo genérico 'nome,asc' seja submetido para endpoints de doações.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SortParameterSanitizerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String[] sortValues = request.getParameterValues("sort");
        if (sortValues != null && sortValues.length > 0) {
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String[] getParameterValues(String name) {
                    if ("sort".equals(name)) {
                        return sanitizeSortValues(request, super.getParameterValues(name));
                    }
                    return super.getParameterValues(name);
                }

                @Override
                public String getParameter(String name) {
                    if ("sort".equals(name)) {
                        String[] sanitized = sanitizeSortValues(request, super.getParameterValues(name));
                        return (sanitized != null && sanitized.length > 0) ? sanitized[0] : null;
                    }
                    return super.getParameter(name);
                }

                @Override
                public Map<String, String[]> getParameterMap() {
                    Map<String, String[]> map = new LinkedHashMap<>(super.getParameterMap());
                    if (map.containsKey("sort")) {
                        String[] sanitized = sanitizeSortValues(request, map.get("sort"));
                        if (sanitized != null && sanitized.length > 0) {
                            map.put("sort", sanitized);
                        } else {
                            map.remove("sort");
                        }
                    }
                    return Collections.unmodifiableMap(map);
                }
            };
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String[] sanitizeSortValues(HttpServletRequest request, String[] rawValues) {
        if (rawValues == null) {
            return null;
        }
        boolean isDoacao = request.getRequestURI() != null && request.getRequestURI().contains("/doacoes");
        List<String> cleaned = new ArrayList<>();
        for (String raw : rawValues) {
            if (raw == null) continue;
            // Remove colchetes, aspas e espaços extras
            String value = raw.replace("[", "")
                              .replace("]", "")
                              .replace("\"", "")
                              .replace("'", "")
                              .trim();

            // Se for vazio ou o placeholder genérico padrão do Swagger ("string"), ignora para usar a ordenação padrão
            if (value.isEmpty() || "string".equalsIgnoreCase(value)) {
                continue;
            }

            // Se for endpoint de doações e vier 'nome' (exemplo herdado de outros endpoints no Swagger), ajusta para dataDoacao
            if (isDoacao && value.toLowerCase().startsWith("nome")) {
                value = value.toLowerCase().contains("desc") ? "dataDoacao,desc" : "dataDoacao,asc";
            }

            cleaned.add(value);
        }
        return cleaned.isEmpty() ? null : cleaned.toArray(new String[0]);
    }
}

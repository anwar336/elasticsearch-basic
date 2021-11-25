package com.es.api.esdb.config;

import com.es.api.esdb.common.Utils;
import com.es.api.esdb.model.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LogManager.getLogger(AuthEntryPointJwt.class);
    private final Marker warning = MarkerManager.getMarker("WARNING");
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException{
        logger.error(warning,"Unauthorized error: {} PATH:{}", authException.getMessage(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ObjectMapper mapper = new ObjectMapper();

        if(Utils.isOk(authException.getMessage()) && (authException.getMessage().contains("User Not Found with username") || authException.getMessage().contains("Bad credentials")) ) {
            try {
                ServiceResponse body = new ServiceResponse(false, "Invalid login information provided");
                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getOutputStream(), body);
            } catch (Throwable t) {
                final Map<String, Object> body = new HashMap<>();
                body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                body.put("error", "Unauthorized");
                body.put("message", authException.getMessage());
                body.put("path", request.getServletPath());
                mapper.writeValue(response.getOutputStream(), body);
            }
        } else {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", authException.getMessage());
            body.put("path", request.getServletPath());
            mapper.writeValue(response.getOutputStream(), body);
        }
    }

}

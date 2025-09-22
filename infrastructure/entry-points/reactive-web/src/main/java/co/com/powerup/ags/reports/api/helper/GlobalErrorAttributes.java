package co.com.powerup.ags.reports.api.helper;

import co.com.powerup.ags.reports.api.exception.AccessDeniedException;
import co.com.powerup.ags.reports.api.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorAttributes.class);
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest serverRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(serverRequest, options);
        Throwable error = getError(serverRequest);

        errorAttributes.remove("trace");
        errorAttributes.remove("exception");
        
        String path = getPath(serverRequest);
        
        switch (error) {
            case UnauthorizedException unauthorizedException -> {
                log.warn("Authorization failed", unauthorizedException);
                setErrorAttributes(errorAttributes, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                        "Unauthorized", "Authorization is invalid", path);
            }
            case AccessDeniedException accessDeniedException -> {
                log.warn("Access denied", accessDeniedException);
                setErrorAttributes(errorAttributes, HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                        "Forbidden", "Access denied. You don't have sufficient permissions to access this resource.", path);
            }
            case null, default -> {
                log.error("Unexpected error", error);
                
                setErrorAttributes(errorAttributes, HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR",
                        INTERNAL_SERVER_ERROR, "An unexpected error occurred, please contact administrators.",
                        getPath(serverRequest));
            }
        }
        
        errorAttributes.put("timestamp", LocalDateTime.now());
        errorAttributes.put("requestId", serverRequest.exchange().getRequest().getId());
        return errorAttributes;
    }
    
    private static String getPath(ServerRequest serverRequest) {
        return serverRequest.path() + (serverRequest.uri().getQuery() != null ? "?" + serverRequest.uri().getQuery() : "");
    }
    
    private void setErrorAttributes(Map<String, Object> errorAttributes, HttpStatus status,
                                    String code, String error, String message, String path) {
        errorAttributes.put("status", status.value());
        errorAttributes.put("code", code);
        errorAttributes.put("error", error);
        errorAttributes.put("message", message);
        errorAttributes.put("path", path);
    }
}

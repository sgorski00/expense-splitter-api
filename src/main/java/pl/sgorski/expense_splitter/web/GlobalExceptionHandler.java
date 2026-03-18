package pl.sgorski.expense_splitter.web;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ApiResponse(
            responseCode = "500",
            description = "An unexpected server error occurred. The error details are provided in the response body in RFC 7807 Problem Details format.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ProblemDetail.class,
                            description = "Standard RFC 7807 Problem Details response containing error information (type, title, status, detail, instance)."
                    )
            )
    )
    public ProblemDetail handleException(Exception ex) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle("Unexpected Exception");
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return problemDetail;
    }
}

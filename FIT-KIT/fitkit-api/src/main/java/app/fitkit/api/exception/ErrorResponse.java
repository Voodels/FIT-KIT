package app.fitkit.api.exception;

import java.time.LocalDate;

import lombok.Getter;


@Getter
public  class ErrorResponse{

    private LocalDate timestamp;
    private int status;
    private  String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDate.now();
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
    }

}
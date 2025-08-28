package com.nestenote.notenest.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String path;
    private List<String> details;

    public ApiError(int status,String error, String path, List<String> details)
    {
        this.status = status;
        this.error = error;
        this.path = path;
        this.details = details;
    }
    public LocalDateTime getTimeStamp() {return timestamp; }
    public int getStatus() {return status; }
    public String getError(){return error; }
    public String getPath(){return path;}
    public List<String> getDetails() {return details; }
}

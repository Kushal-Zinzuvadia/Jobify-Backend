package org.example.jobify.utilities;

public class ApiResponse {
    private int statusCode;
    private Object data;
    private String message;

    public ApiResponse(int statusCode, Object data, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }
    public ApiResponse() {
        super();
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

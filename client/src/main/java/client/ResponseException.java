package client;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;


public class ResponseException extends Exception{

    public enum Code {
        BadRequest,
        Unauthorized,
        AlreadyTaken,
        ServerError
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }

    public static ResponseException fromJson(String json, int statusCode) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = fromHttpStatusCode(statusCode);
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 400 -> Code.BadRequest;
            case 401 -> Code.Unauthorized;
            case 403 -> Code.AlreadyTaken;
            case 500 -> Code.ServerError;
            default -> Code.ServerError;
        };
    }
}


package client;
import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class HttpExcept extends RuntimeException{
    final private int statusCode;
    public HttpExcept(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public static HttpExcept fromStream(InputStream stream, int status) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        String message = map.get("message").toString();
        return new HttpExcept(status, message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}

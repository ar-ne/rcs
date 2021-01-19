package ar.ne.rcs.client.network.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPRequest {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static MyOkHttpRetryInterceptor interceptor =
            new MyOkHttpRetryInterceptor.Builder()
                    .retryInterval(5000)
                    .executionCount(100)
                    .build();
    public static OkHttpClient client =
            new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .addInterceptor(interceptor)
                    .connectionPool(new ConnectionPool())
                    .connectTimeout(3000, TimeUnit.MILLISECONDS)
                    .readTimeout(10000, TimeUnit.MILLISECONDS)
                    .build();

    static {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
    }

    /**
     * @param data data that you want send
     * @return -1 if occur any exception
     */
    public static <T> int PostObject(String url, T data) throws Exception {
        String json = new Gson().toJson(data, data.getClass());
        RequestBody requestBody = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        response.close();
        return response.code();
    }


    /**
     * @param classOfT Type
     * @return that object
     * @throws Exception if anything wrong
     */
    public static <T> T GetObject(String url, List<RequestParam> params, Class<T> classOfT) throws Exception {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(GetString(url, params), classOfT);
    }

    public static String GetString(String url, @Nullable List<RequestParam> params) throws Exception {
        Request request = new Request.Builder()
                .url(getUrlWithParams(url, params))
                .get()
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }

    private static String getUrlWithParams(String url, @Nullable List<RequestParam> params) {
        StringBuilder builder = new StringBuilder();

        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                builder.append(params.get(i));
                if (i + 1 < params.size()) {
                    builder.append("&");
                }
            }
        }
        return params == null
                ? url
                : String.format("%s?%s", url, builder);
    }

    @AllArgsConstructor
    public static class RequestParam {
        public String key;
        public String value;

        @Override
        public String toString() {
            return String.format("%s=%s", key, value);
        }
    }
}

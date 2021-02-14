package ar.ne.rcs.client.communication.http;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InterruptedIOException;

public class MyOkHttpRetryInterceptor implements Interceptor {
    public final long retryInterval;//重试的间隔
    public final int executionCount;//最大重试次数

    MyOkHttpRetryInterceptor(Builder builder) {
        this.executionCount = builder.executionCount;
        this.retryInterval = builder.retryInterval;
    }


    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = doRequest(chain, request);
        int retryNum = 0;
        while (!response.isSuccessful() && retryNum <= executionCount) {
            final long nextInterval = getRetryInterval();
            System.err.printf("NetworkError,retrying in %sms......%s%n", nextInterval, retryNum);
            try {
                Thread.sleep(nextInterval);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            retryNum++;
            // retry the request
            response = doRequest(chain, request);
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }

    /**
     * retry间隔时间
     */
    public long getRetryInterval() {
        return this.retryInterval;
    }

    public static final class Builder {
        public int executionCount;
        public long retryInterval;

        public Builder() {
            executionCount = 3;
            retryInterval = 1000;
        }

        public MyOkHttpRetryInterceptor.Builder executionCount(int executionCount) {
            this.executionCount = executionCount;
            return this;
        }

        public MyOkHttpRetryInterceptor.Builder retryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        public MyOkHttpRetryInterceptor build() {
            return new MyOkHttpRetryInterceptor(this);
        }
    }

}
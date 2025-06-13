package com.amvera.cli.client;

import com.amvera.cli.utils.TokenUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

@Service
public class AmveraHttpClient extends BaseHttpClient {

    public AmveraHttpClient(TokenUtils tokenUtils) {
        super(tokenUtils.readToken().accessToken());
    }

    public <T> T get(URI uri, Class<T> clazz, String err) {
        return super.client(err)
                .get()
                .uri(uri)
                .retrieve()
                .body(clazz);
    }

    public <T> List<T> get(URI uri, ParameterizedTypeReference<List<T>> clazz, String err) {
        return super.client(err)
                .get()
                .uri(uri)
                .retrieve()
                .body(clazz);
    }

    public <T, K> T post(URI uri, Class<T> clazz, String err, K body) {
        return super.client(err)
                .post()
                .uri(uri)
                .body(body)
                .retrieve()
                .body(clazz);
    }

    public <T, K> T post(URI uri, Class<T> clazz, String err, K body, Consumer<HttpHeaders> headers) {
        return super.client(headers, err)
                .post()
                .uri(uri)
                .body(body)
                .retrieve()
                .body(clazz);
    }

    public <K> void post(URI uri, String err, K body, Consumer<HttpHeaders> headers) {
        super.client(headers, err)
                .post()
                .uri(uri)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }


    public <K> void post(URI uri, String err, K body) {
        super.client(err)
                .post()
                .uri(uri)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public <K> void post(URI uri, String err) {
        super.client(err)
                .post()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }

    public <T, K> T put(URI uri, Class<T> clazz, String err, K body) {
        return super.client(err)
                .put()
                .uri(uri)
                .body(body)
                .retrieve()
                .body(clazz);
    }

    public <K> void put(URI uri, String err, K body) {
        super.client(err)
                .put()
                .uri(uri)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public void put(URI uri, String err) {
        super.client(err)
                .put()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(URI uri, String err) {
        super.client(err)
                .delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }
}

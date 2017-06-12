package com.example.firebase;

import java.io.IOException;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * A Http Messaging Service that sends messages over via HTTP
 * 
 * @author aanal
 *
 */
public abstract class HttpMessagingService implements ResponseErrorHandler, MessagingService {

    /**
     * Used to access the http rest service
     */
    private RestTemplate template;

    /**
     * The target URL of this messaging service
     */
    private String url;

    /**
     * The method used while sending message
     */
    private HttpMethod method;

    /**
     * A constructor that initializes the messaging service with the defined
     * rest template
     * 
     * @param restTemplate
     * @param url
     * @param method
     */
    public HttpMessagingService(RestTemplate restTemplate, String url, HttpMethod method) {
        this.template = restTemplate;
        template.setErrorHandler(this);
        this.url = url;
        this.method = method;
    }

    /**
     * Sends the message to the previously defined url using the predefined
     * method
     * 
     * @param headers
     *            headers to be included in the field
     * @param message
     *            that will be sent to the server
     * @return the response what the server will be returning
     */
    protected ResponseEntity<String> sendHttpMessage(Map<String, String> headers, Message message)
            throws JSONException {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String headElement : headers.keySet()) {
            httpHeaders.add(headElement, headers.get(headElement));
        }
        String request = message.convertToRequestBody();
        if (request != null) {
            HttpEntity<String> requestEntity = new HttpEntity<String>(request, httpHeaders);
            ResponseEntity<String> response = template.exchange(url, method, requestEntity, String.class);
            return response;
        }
        return null;
    }

    public abstract void sendMessage(Message message) throws JSONException;

    public abstract void sendMessage(String messageType, String customerId, String token, JSONObject message,
            JSONArray buildInstruction) throws JSONException;

    /**
     * Handle the error generated by the server
     */
    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getRawStatusCode();
        if (statusCode >= 400 && statusCode < 500) {
            throw new IOException("Invalid Request Exception");
        } else if (statusCode > 500) {
            throw new IOException("Internal Server Exception");
        }
    }

    /**
     * Check whether there was an error.
     */
    public boolean hasError(ClientHttpResponse response) throws IOException {
        if (response.getRawStatusCode() >= 400) {
            return true;
        }
        return false;
    }
}

package io.jenkins.plugins.util;

import io.jenkins.plugins.dto.MainBodyDto;
import io.jenkins.plugins.exception.AppException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class WebhookCaller {
    private final String webhookUrl;
    private final MainBodyDto body;

    public WebhookCaller(String _webhookUrl, MainBodyDto _body) {
        this.webhookUrl = _webhookUrl;
        this.body = _body;
    }

    public void send() throws AppException {

        try {
            String json = StringHelper.serializableObject(this.body);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(this.webhookUrl);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);

            if(response.getStatusLine().getStatusCode()!=200)
                throw new AppException("Can not send notification with status: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) { e.printStackTrace(); }
    }
}

package wikilocation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import play.Logger;
import wikilocation.pojos.Article;

import java.io.IOException;
import java.util.List;

/**
 * Note: not used anymore.
 */
public class WikiLocationApi {

    private static final String WIKILOCATION_BASE_URL = "http://api.wikilocation.org/articles";
    private static final Integer FALLBACK_LIMIT = 5;
    private CloseableHttpClient client;
    private Double lat;
    private Double lng;
    private Integer limit;

    public WikiLocationApi(Double lat, Double lng, Integer limit) {
        client = HttpClients.createDefault();
        this.lat = lat;
        this.lng = lng;
        this.limit = limit;
    }

    public WikiLocationApi(Double lat, Double lng) {
        this(lat, lng, FALLBACK_LIMIT);
    }

    /**
     * Access WikiLocation and retrieve wikipedia-articles related to an area (latitude, longitude)
     */
    public List<Article> doQuery() throws ClientProtocolException {
        final String formattedUrl = formatLatLngUrl();
        HttpGet httpGet = new HttpGet(formattedUrl);
        try {
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();

                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;

                    } else {
                        throw new ClientProtocolException(
                                String.format("GET to '%s' failed with status code: %d", formattedUrl, status)
                        );
                    }
                }
            };

            String response = client.execute(httpGet, responseHandler);
            // JsonNode parsedResponse = Json.toJson(response);
            return convertToArticles(response);

        } catch (IOException e) {
            Logger.trace("Failed to query WikiLocation", e);
            return null;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.trace("Failed to close HttpClient", e);
            }
        }
    }

    /**
     * @param response
     * Json formatted String with composition {"articles": [list of articles]}
     * @return Parsed articles.
     */
    private List<Article> convertToArticles(String response) {
        if (response == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Article> articles;
        try {
            JsonNode node = mapper.readTree(response);
            node = node.get("articles");
            TypeReference<List<Article>> typeRef = new TypeReference<List<Article>>() {};
            articles = mapper.readValue(node.traverse(), typeRef);
        } catch (IOException e) {
            Logger.trace("Failed to convert response to Articles", e);
            return null;
        }
        return articles;
    }

    private String formatLatLngUrl() {
        return String.format(
                "%s?lat=%f&lng=%f&limit=%d",
                WIKILOCATION_BASE_URL, lat, lng, limit
        );
    }

}
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
import org.joda.time.DateTime;
import play.Logger;
import play.libs.Json;
import wikilocation.managers.LocationManager;
import wikilocation.models.WikiArticle;
import wikilocation.pojos.Article;
import wikilocation.utils.HtmlParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by swami on 10/04/14.
 */

public class WikiMediaApi {

    public static WikiArticle query(Long articleId, String locale) throws ClientProtocolException {
        List<WikiArticle> articles = query(new Long[] {articleId}, locale);
        return articles.get(0);
    }

    public static List<WikiArticle> query(Long[] articleIds, String locale) throws ClientProtocolException {
        CloseableHttpClient client = HttpClients.createDefault();
        final String formattedUrl = formatUrl(articleIds, locale);
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
            return parseArticles(response, locale);

        } catch (IOException e) {
            Logger.trace("Failed to query WikiMedia", e);
            throw new RuntimeException(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.trace("Failed to close HttpClient", e);
            }
        }
    }

    public static List<WikiArticle> parseArticles(String response, String locale) throws IOException {
        List<WikiArticle> wikiArticles = new ArrayList<WikiArticle>();
        String baseUri = getBaseUri(locale);
        Logger.debug("WikiMedia.resp", response);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(response);
        JsonNode outerNode = tree.findValue("pages");
        List<String> pageIds = outerNode.findValuesAsText("pageid");

        for (String pageId : pageIds) {
            JsonNode page = outerNode.findValue(pageId);
            String content = HtmlParser.parseWikiHtml(page.findValue("*").asText(), baseUri);  // only one but
            DateTime timestamp = DateTime.parse(page.findValue("timestamp").asText());

            WikiArticle wikiArticle = new WikiArticle(Long.parseLong(pageId), timestamp, content, locale);
            wikiArticles.add(wikiArticle);
        }

        return wikiArticles;
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
            throw new RuntimeException(e);
        }
        return articles;
    }

    private static String formatUrl(Long[] articleIds, String locale) {
        String articles = "";

        // separate article ids with a pipe "|"
        for (Long articleId : articleIds) {
            articles += articleId.toString() + "|";
        }
        articles = articles.substring(0, articles.length() - 1);  // remove the last one
        String rvProps = "content|timestamp";

        try {
            // we have to encode articles since HttpGet doesn't like pipes
            articles = URLEncoder.encode(articles, "UTF-8");
            rvProps = URLEncoder.encode(rvProps, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return String.format(
                "%s/w/api.php?action=query&prop=revisions&rvparse=1&rvprop=%s&rvsection=0&format=json&pageids=%s",
                getBaseUri(locale), rvProps, articles);
    }

    private static String getBaseUri(String locale) {
        return String.format("http://%s.wikipedia.org", locale);
    }

}

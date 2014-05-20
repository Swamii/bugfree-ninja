package wikilocation.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import play.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by swami on 23/04/14.
 */
public class HtmlParser {

    /**
     * Try to build a internal map link first. When clicked move map to that location.
     * If the link doesnt match a
     * @param html
     * @param baseUri
     * @return
     */
    public static String parseWikiHtml(String html, String baseUri) {
        // TODO: finish mapping of href's to internal lat-lng links

        baseUri = makeProtocolRelative(baseUri);
        Document doc = Jsoup.parse(html, baseUri);
        Elements elements = doc.getElementsByTag("a");
//        Pattern internalTitleLinkPattern = Pattern.compile("/wiki/([a-zA-Z\\-_()]+)");  // like /wiki/ArticleTitle
//        Map<String, String> hrefArticleTitleMap = new HashMap<String, String>();

        for (Element element : elements) {
            element.attr("target", "_blank");

            String href = element.attr("href");
            URI uri;
            try {
                uri = new URI(href);
            } catch (URISyntaxException e) {
                Logger.trace("Caught exception while parsing element with href: {}", href);
                continue;
            }

            // check if the url isn't totally relative (protocol relative is fine)
            if (uri.isAbsolute() || href.startsWith("//")) {
                continue;
            }

            // it totally relative, add the base uri
            String updatedHref = baseUri + href;
            element.attr("href", updatedHref);

//            Matcher matcher = internalTitleLinkPattern.matcher(href);
//            if (matcher.find()) {
//                String articleTitle = matcher.group();
//                hrefArticleTitleMap.put(updatedHref, articleTitle);
//            }

        }

        doc.getElementsByAttribute("style").removeAttr("style");
        doc.getElementsByTag("table").addClass("table");

        return doc.toString();
    }

    private static String makeProtocolRelative(String baseUri) {
        try {
            return new URI(baseUri).getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static Whitelist getWhitelist() {
        return new Whitelist()
                .addTags(
                    "a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                    "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                    "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
                    "sub", "sup", "tfoot", "u", "ul")

                .addAttributes("a", "href", "title", "class")
                .addAttributes("blockquote", "cite", "class")
                .addAttributes("col", "span", "width", "class")
                .addAttributes("colgroup", "span", "width", "class")
                .addAttributes("img", "align", "alt", "height", "src", "title", "width", "class")
                .addAttributes("ol", "start", "type", "class")
                .addAttributes("q", "cite", "class")
//                .addAttributes("table", "summary", "width")
//                .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
//                .addAttributes(
//                        "th", "abbr", "axis", "colspan", "rowspan", "scope",
//                        "width")
                .addAttributes("ul", "type", "class")

                .addProtocols("a", "href", "ftp", "http", "https", "mailto")
                .addProtocols("blockquote", "cite", "http", "https")
                .addProtocols("cite", "cite", "http", "https")
                .addProtocols("img", "src", "http", "https")
                .addProtocols("q", "cite", "http", "https")
//                .addProtocols("a", "href", "//")
                .preserveRelativeLinks(true)
        ;
    }

}

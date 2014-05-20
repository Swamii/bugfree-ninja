package wikilocation.models;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by swami on 23/04/14.
 */
@Entity
@Table(name = "wiki_article")
public class WikiArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "wiki_article_id_seq")
    @SequenceGenerator(name = "wiki_article_id_seq", sequenceName = "wiki_article_id_seq")
    public Long id;

    @Column(name = "article_id")
    public Long articleId;

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime timestamp;

    @Column(columnDefinition = "TEXT")
    public String content;

    public String locale;

    public WikiArticle() {}

    public WikiArticle(Long articleId, DateTime timestamp, String content, String locale) {
        this.articleId = articleId;
        this.timestamp = timestamp;
        this.content = content;
        this.locale = locale;
    }

}

package ad.models;

import ad.dto.AdSubmission;
import ad.dto.ColorDto;
import ad.enums.AdType;
import ad.enums.Color;
import ad.enums.Condition;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by swami on 14/05/14.
 */
@Entity
@Table(name = "ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ad_id_seq")
    @SequenceGenerator(name = "ad_id_seq", sequenceName = "ad_id_seq")
    public Long id;

    public String name;
    @Column(length = 2047)
    public String description;
    public String location;
    public String size;
    public Double price;
    public String brand;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    public Collection<AdPicture> adPictures;

    @ElementCollection(targetClass = Color.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ad_color")
    @Column(name = "color") // Column name in ad_color
    public Collection<Color> colors;

    @Enumerated(EnumType.STRING)
    @Column(name = "ad_type")
    public AdType adType;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition")
    public Condition condition;

    @ManyToOne
    @JoinColumn(name = "ad_category")
    public AdCategory adCategory;

    public static Ad fromDto(AdSubmission submission) {
        Ad ad = new Ad();

        ad.name = submission.name;
        ad.description = submission.description;
        ad.location = submission.location;
        ad.size = submission.size;
        ad.adType = AdType.fromDto(submission.type);
        ad.price = ad.adType == AdType.SALE_TRADE || ad.adType == AdType.SALE ? Double.valueOf(submission.price) : null;
        ad.condition = Condition.fromDisplayName(submission.condition);
        ad.brand = submission.brand;
        ad.colors = new ArrayList<Color>();

        if (submission.colors != null) {
            for (ColorDto c : submission.colors) {
                ad.colors.add(Color.valueOf(c.label.toUpperCase()));
            }
        }

        return ad;

    }
}

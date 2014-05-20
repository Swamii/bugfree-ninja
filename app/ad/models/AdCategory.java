package ad.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by swami on 14/05/14.
 */
@Entity
@Table(name = "ad_category")
public class AdCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ad_category_id_seq")
    @SequenceGenerator(name = "ad_category_id_seq", sequenceName = "ad_category_id_seq")
    public Long id;

    public String name;
    public Integer level;

    @ManyToMany(fetch = FetchType.EAGER)
    public List<AdCategory> subCategories;

}

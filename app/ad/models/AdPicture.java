package ad.models;

import ad.PictureUtils;
import ad.UploadedItem;
import ad.dto.PictureDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by swami on 14/05/14.
 */
@Entity
@Table(name="ad_picture")
public class AdPicture implements UploadedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ad_picture_id_seq")
    @SequenceGenerator(name = "ad_picture_id_seq", sequenceName = "ad_picture_id_seq")
    public Long id;
    public Integer index;  // the order in which they appear
    public String path;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    @JsonIgnore
    public Ad ad;

    public static AdPicture fromDto(PictureDto dto, Ad ad) {
        AdPicture pic = new AdPicture();
        pic.ad = ad;
        pic.index = dto.index;
        pic.path = new PictureUtils(dto.src, getSubFolder()).savePicture().getRelativePath();
        return pic;
    }

    public static String getSubFolder() {
        return "ad";
    }

    @Override
    public String getPath() {
         return controllers.routes.MainController.asset(path).url();
    }
}

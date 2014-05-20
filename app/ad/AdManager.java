package ad;

import ad.dto.AdSubmission;
import ad.dto.CategoryDto;
import ad.dto.PictureDto;
import ad.dto.SubcategoryDto;
import ad.models.Ad;
import ad.models.AdCategory;
import ad.models.AdPicture;
import play.db.jpa.JPA;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swami on 14/05/14.
 */
public class AdManager {

    public static AdCategory byName(String name) {
        String query = "select ac from AdCategory ac where ac.name = :name";
        try {
            return JPA.em().createQuery(query, AdCategory.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public static Ad adByName(String name) {
        String query = "select ad from Ad ad where ad.name = :name";
        try {
            return JPA.em().createQuery(query, Ad.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }



    public static List<AdPicture> createPictures(List<PictureDto> pictures, Ad ad) {
        List<AdPicture> adPictures = new ArrayList<AdPicture>();
        for (PictureDto pic : pictures) {
            AdPicture adPic = AdPicture.fromDto(pic, ad);
//            JPA.em().persist(adPic);
            adPictures.add(adPic);
        }
        return adPictures;
    }

    public static AdCategory getOrCreate(CategoryDto dto) {
        AdCategory cat = byName(dto.label);

        if (cat == null) {
            cat = new AdCategory();
            cat.name = dto.label;
            cat.level = 1;
            cat.subCategories = new ArrayList<AdCategory>();
        }

        for (SubcategoryDto subDto : dto.subcategories) {
            boolean doContinue = false;
            for (AdCategory subCat : cat.subCategories) {
                if (subCat.name.equalsIgnoreCase(subDto.label)) {
                    doContinue = true;
                    break;
                }
            }
            if (doContinue) {
                continue;
            }

            AdCategory subCat = new AdCategory();
            subCat.name = subDto.label;
            subCat.level = 2;
            JPA.em().persist(subCat);
            cat.subCategories.add(subCat);
        }

        JPA.em().persist(cat);
        return cat;
    }

    public static Ad create(AdSubmission submission) {
        Ad ad = Ad.fromDto(submission);
        ad.adCategory = getOrCreate(submission.category);
        JPA.em().persist(ad);
        ad.adPictures = createPictures(submission.pictures, ad);
        ad = JPA.em().find(Ad.class, ad.id);  // grab a fresh one
        return ad;
    }

}

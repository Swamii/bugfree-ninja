import ad.AdManager;
import ad.PictureUtils;
import ad.dto.*;
import ad.enums.AdType;
import ad.enums.Color;
import ad.models.Ad;
import ad.models.AdCategory;
import ad.models.AdPicture;
import org.junit.Test;
import play.data.validation.ValidationError;
import play.db.jpa.JPA;
import play.libs.F;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.*;

public class AdTests extends TestBase {

    @Test
    public void testConvertBase64StringToImageFile() throws Exception {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                String imgData = readFile("base64img.txt");
                PictureUtils utils = new PictureUtils(imgData, "ad-tests");

                String path = utils.savePicture().getFullPath();
                File f = new File(path);

                assertThat(f.exists());

                // clean up
                String subFolder = PictureUtils.formatPath("ad-tests");
                f.delete();

                new File(subFolder).delete();
            }
        });
    }

    @Test
    public void testSubmitAd() throws Exception {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                AdSubmission submission = createValidSubmission();

                assertThat(submission.validate()).isNull();

                Ad ad = AdManager.create(submission);

                assertThat(ad.adCategory.level).isEqualTo(1);
                assertThat(ad.adCategory.name).isEqualTo("Clothes");
                for (AdCategory subCat : ad.adCategory.subCategories) {
                    assertThat(subCat.name).startsWith("SubCat");
                    assertThat(subCat.level).isEqualTo(2);
                }

                assertThat(ad.adPictures.size()).isEqualTo(2);
                for (AdPicture adPic : ad.adPictures) {
                    assertThat(adPic.getPath()).isNotNull();
                    assertThat(new URI(adPic.getPath()).isAbsolute()).isEqualTo(false);
                    assertThat(adPic.index).isNotNull();
                    assertThat(adPic.path).isNotNull();
                    assertThat(adPic.path).isNotEqualTo(adPic.getPath());
                    assertThat(adPic.ad == ad);
                }

                assertThat(ad.condition.getDisplayName()).isEqualTo("Medium nice");
                assertThat(ad.location).isEqualTo("Stockholm");
                assertThat(ad.name).isEqualTo("Mah submission");
                assertThat(ad.description).isEqualTo("A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description");
                assertThat(ad.brand).isEqualTo("H&M");
                assertThat(ad.price).isEqualTo(10000);
                assertThat(ad.size).isEqualTo("48");

                assertThat(ad.colors.size()).isEqualTo(3);
                assertThat(ad.colors).contains(Color.RED);
                assertThat(ad.colors).contains(Color.GREEN);
                assertThat(ad.colors).contains(Color.BLUE);

                assertThat(ad.adType).isEqualTo(AdType.SALE_TRADE);
            }
        });
    }

    @Test
    public void testAdValidation() throws Exception {
        AdSubmission ad = createInvalidSubmission();

        List<ValidationError> errors = ad.validate();
        assertThat(errors).isNotNull();

        assertThat(errors).contains(AdErrors.CONDITION_INVALID);
        assertThat(errors).contains(AdErrors.PRICE_ZERO);
    }

    @Test
    public void testGetAd() throws Exception {
        JPA.withTransaction(new F.Callback0() {
            @Override
            public void invoke() throws Throwable {
                // Mostly to test that foreign keys are persisted correctly
                Ad ad = AdManager.adByName("Woo");
                Long id = ad.id;

                PictureDto dto = new PictureDto();
                dto.index = 0;
                dto.src = readFile("base64img.txt");

                AdPicture adPic = AdPicture.fromDto(dto, ad);
                JPA.em().persist(adPic);
                Ad otherAd = JPA.em().find(Ad.class, id);

                assertThat(otherAd.adPictures.size()).isEqualTo(1);
            }
        });
    }

    /*  HELPERS */

    public AdSubmission createInvalidSubmission() throws Exception {
        String imgData = readFile("base64img.txt");
        AdSubmission submission = new AdSubmission();

        CategoryDto cat = new CategoryDto();
        cat.label = "FakeClothes";
        cat.subcategories = new ArrayList<SubcategoryDto>();
        for (int i = 0; i < 2; i++) {
            SubcategoryDto subCat = new SubcategoryDto();
            subCat.label = String.format("SubCat %d", i);
            cat.subcategories.add(subCat);
        }
        submission.category = cat;

        List<ColorDto> colors = new ArrayList<ColorDto>();
        ColorDto color1 = new ColorDto();
        color1.label = "red";
        colors.add(color1);
        ColorDto color2 = new ColorDto();
        color2.label = "green";
        colors.add(color2);
        ColorDto color3 = new ColorDto();
        color3.label = "blue";
        colors.add(color3);
        submission.colors = colors;

        List<PictureDto> pics = new ArrayList<PictureDto>();
        PictureDto pic1 = new PictureDto();
        pic1.index = 0;
        pic1.src = imgData;
        pics.add(pic1);
        PictureDto pic2 = new PictureDto();
        pic2.index = 1;
        pic2.src = imgData;
        pics.add(pic2);
        submission.pictures = pics;

        AdTypeDto adType = new AdTypeDto();
        adType.sale = true;
        adType.trade = true;
        adType.gift = true;
        submission.type = adType;

        submission.condition = "Medium super nice";
        submission.location = "Stockholm";
        submission.name = "Mah submission";
        submission.description = "A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description";
        submission.brand = "H&M";
        submission.price = -1;
        submission.size = "48";

        return submission;
    }

    public AdSubmission createValidSubmission() throws Exception {
        String imgData = readFile("base64img.txt");
        AdSubmission submission = new AdSubmission();

        CategoryDto cat = new CategoryDto();
        cat.label = "Clothes";
        cat.subcategories = new ArrayList<SubcategoryDto>();
        for (int i = 0; i < 2; i++) {
            SubcategoryDto subCat = new SubcategoryDto();
            subCat.label = String.format("SubCat %d", i);
            cat.subcategories.add(subCat);
        }
        submission.category = cat;

        List<ColorDto> colors = new ArrayList<ColorDto>();
        ColorDto color1 = new ColorDto();
        color1.label = "red";
        colors.add(color1);
        ColorDto color2 = new ColorDto();
        color2.label = "green";
        colors.add(color2);
        ColorDto color3 = new ColorDto();
        color3.label = "blue";
        colors.add(color3);
        submission.colors = colors;

        List<PictureDto> pics = new ArrayList<PictureDto>();
        PictureDto pic1 = new PictureDto();
        pic1.index = 0;
        pic1.src = imgData;
        pics.add(pic1);
        PictureDto pic2 = new PictureDto();
        pic2.index = 1;
        pic2.src = imgData;
        pics.add(pic2);
        submission.pictures = pics;

        AdTypeDto adType = new AdTypeDto();
        adType.sale = true;
        adType.trade = true;
        submission.type = adType;

        submission.condition = "Medium nice";
        submission.location = "Stockholm";
        submission.name = "Mah submission";
        submission.description = "A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description A long description";
        submission.brand = "H&M";
        submission.price = 10000;
        submission.size = "48";

        return submission;
    }

}

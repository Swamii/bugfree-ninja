package ad.dto;

import ad.enums.Color;
import ad.enums.Condition;
import play.Logger;
import play.api.libs.json.Json;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swami on 13/05/14.
 */
public class AdSubmission {

    @Constraints.Required
    public String name;
    public String description;
    @Constraints.Required
    public String location;
    @Constraints.Required
    public CategoryDto category;
    public List<PictureDto> pictures = new ArrayList<>();
    @Constraints.Required
    public List<ColorDto> colors = new ArrayList<>();
    @Constraints.Required
    public String size;
    @Constraints.Required
    public AdTypeDto type = new AdTypeDto();
    public Integer price;
    @Constraints.Required
    public String condition;
    public String brand;

    public List<ValidationError> validate() {
        // TODO: more validation.
        List<ValidationError> errors = new ArrayList<>();

        if (!type.gift && !type.sale && !type.trade) {
            errors.add(AdErrors.TYPE_MISSING);
        } else if (type.gift && type.sale && type.trade || type.gift && type.sale || type.gift && type.trade) {
            errors.add(AdErrors.INCOMPATIBLE_TYPES);
        } else if (type.sale && (price == null || price <= 0)) {
            errors.add(AdErrors.PRICE_ZERO);
        }

        for (ColorDto cDto : colors) {
            try {
                Color.valueOf(cDto.label.toUpperCase());
            } catch (Throwable t) {
                ValidationError colorError = new ValidationError("colors", String.format("The color '%s' is not valid", cDto.label));
                errors.add(colorError);
            }
        }

        if (Condition.fromDisplayName(condition) == null) {
            errors.add(AdErrors.CONDITION_INVALID);
        }

        if (category == null || category.label == null) {
            errors.add(AdErrors.CATEGORY_MISSING);
        }

        Logger.debug("Errors were: {}", errors);
        return errors.size() == 0 ? null : errors;
    }

}

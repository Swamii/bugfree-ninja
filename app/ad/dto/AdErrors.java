package ad.dto;

import ad.enums.Condition;
import play.data.validation.ValidationError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swami on 15/05/14.
 */
public class AdErrors {

    public static final ValidationError PRICE_ZERO = new ValidationError("price", "The price must be positive number");
    public static final ValidationError CONDITION_INVALID = new ValidationError("condition", "That condition is not valid");
    public static final ValidationError TYPE_MISSING = new ValidationError("type", "A type is required.");
    public static final ValidationError NAME_MISSING = new ValidationError("name", "The title is required.");
    public static final ValidationError CATEGORY_MISSING = new ValidationError("category", "Category is required.");
    public static final ValidationError INCOMPATIBLE_TYPES = new ValidationError("type", "The types selected are incompatible.");
}

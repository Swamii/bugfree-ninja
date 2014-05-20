package ad.enums;

import ad.dto.AdTypeDto;

/**
 * Created by swami on 14/05/14.
 */
public enum AdType {
    SALE, TRADE, SALE_TRADE, GIFT;

    public static AdType fromDto(AdTypeDto dto) {
        if (dto.sale && dto.trade) {
            return AdType.SALE_TRADE;
        } else if (dto.sale) {
            return AdType.SALE;
        } else if (dto.trade) {
            return AdType.TRADE;
        }
        return AdType.GIFT;
    }

    @Override
    public String toString() {
        String n = super.toString().replace('_', ' ');
        return n.substring(0, 1).toUpperCase() +
               n.substring(1).toLowerCase();
    }


}

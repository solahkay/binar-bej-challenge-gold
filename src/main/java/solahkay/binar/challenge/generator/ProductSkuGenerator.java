package solahkay.binar.challenge.generator;

import lombok.experimental.UtilityClass;
import solahkay.binar.challenge.entity.Merchant;

import java.time.LocalTime;

@UtilityClass
public class ProductSkuGenerator {

    public String generate(Merchant merchant, String productName) {
        LocalTime time = LocalTime.now();
        String merchantName = getString(merchant);

        String[] productNameSplit = productName.split(" ");

        StringBuilder productNameInitialBuilder = new StringBuilder();
        for (String split : productNameSplit) {
            productNameInitialBuilder.append(split.charAt(0));
        }

        String productNameInitial = productNameInitialBuilder.toString().toUpperCase();

        String nanoTime = String.valueOf(time.getNano());

        return String.join(
                "-",
                merchantName,
                productNameInitial,
                nanoTime.substring(nanoTime.length() - 5)
        );
    }

    private String getString(Merchant merchant) {
        String merchantName = merchant.getName().toUpperCase();
        String[] merchantNameSplit = merchantName.split(" ");

        StringBuilder merchantNameInitialBuilder = new StringBuilder();
        if (merchantNameSplit.length > 1) {
            for (String merchantNameValue : merchantNameSplit) {
                merchantNameInitialBuilder.append(merchantNameValue.charAt(0));
            }
            merchantName = merchantNameInitialBuilder.toString();
        } else if (merchantNameSplit.length == 1) {
            if (merchantNameSplit[0].length() < 12) {
                merchantName = merchantNameSplit[0];
            } else {
                merchantName = merchantName.substring(0, 12);
            }
        }
        return merchantName;
    }

}

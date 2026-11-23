
package com.rupix.util;

import com.rupix.model.MerchantPayment;
import java.util.Comparator;

public class TransactionComparator
        implements Comparator<MerchantPayment> {

    @Override
    public int compare(MerchantPayment p1, MerchantPayment p2) {

        if (p1.getAmountPaise() > p2.getAmountPaise()) {
            return 1;
        }

        if (p1.getAmountPaise() < p2.getAmountPaise()) {
            return -1;
        }

        return 0;
    }
}

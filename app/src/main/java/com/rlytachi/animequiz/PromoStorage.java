package com.rlytachi.animequiz;

public class PromoStorage {

    public static final String[] APP_PREFERENCES_PROMO_CODES = {"promoCode1", "promoCode2", "promoCode3", "promoCode4"};
    private static String[] promoStorageArr = new String[]{"one", "two", "three", "four"};
    private static final String[] promoStorageArrBackup = new String[]{"one", "two", "three", "four"};

    public static String[] getPromoStorage() {
        return promoStorageArr;
    }

    public static void setPromoStorage(String[] promoStorageArr) {
        PromoStorage.promoStorageArr = promoStorageArr;
    }

    public static boolean promoStorage(String promoCode) {
        for (int i = 0; i < promoStorageArr.length; i++) {
            if (promoStorageArr[i] == null) continue;
            if (promoCode.equals(promoStorageArr[i].toLowerCase())) {
                promoStorageArr[i] = null;
                return true;
            }
        }
        return false;
    }

    public static void reset(){
        setPromoStorage(PromoStorage.promoStorageArrBackup);
    }
}

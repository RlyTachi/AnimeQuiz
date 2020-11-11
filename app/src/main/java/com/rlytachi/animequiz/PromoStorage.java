package com.rlytachi.animequiz;

public class PromoStorage {

    public static final String[] APP_PREFERENCES_PROMO_CODES = {"promoCode0", "promoCode1", "promoCode2", "promoCode3", "promoCode4", "promoCode5", "promoCode6", "promoCode7", "promoCode8", "promoCode9"};
    private static String[] promoStorageArr = new String[]{"NEWYEAR_2021", "LAUNCH", "ILOVEANIME", "A9K4G8M6N9", "K8G5F9L7V0", "X0D0Y7K3O7", "A6H8O4P8N2", "B0V9C5H9F1", "T7R9P0N7V3", "C0J4B1G8L4"};
    private static final String[] promoStorageArrBackup = new String[]{"NEWYEAR_2021", "LAUNCH", "ILOVEANIME", "A9K4G8M6N9", "K8G5F9L7V0", "X0D0Y7K3O7", "A6H8O4P8N2", "B0V9C5H9F1", "T7R9P0N7V3", "C0J4B1G8L4"};

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

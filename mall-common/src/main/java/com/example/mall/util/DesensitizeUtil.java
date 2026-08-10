package com.example.mall.util;

public class DesensitizeUtil {
    /**
     * 姓名脱敏：保留首位，其余*
     * 例：张三 → 张* 、张三丰 → 张**
     */
    public static String name(String name) {
        if (isEmpty(name)) {
            return name;
        }
        char[] chars = name.toCharArray();
        if (chars.length == 1) {
            return name;
        }
        for (int i = 1; i < chars.length; i++) {
            chars[i] = '*';
        }
        return new String(chars);
    }

    /**
     * 手机号脱敏：中间4位*
     * 例：13800138000 → 138****8000
     */
    public static String phone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 身份证脱敏：保留前6后4，中间*
     * 支持15/18位身份证
     */
    public static String idCard(String idCard) {
        if (isEmpty(idCard)) {
            return idCard;
        }
        int len = idCard.length();
        if (len == 15) {
            return idCard.substring(0, 6) + "*******" + idCard.substring(12);
        } else if (len == 18) {
            return idCard.substring(0, 6) + "********" + idCard.substring(14);
        }
        return idCard;
    }

    /**
     * 邮箱脱敏：@前保留首位，中间*
     * 例：test@163.com → t***@163.com
     */
    public static String email(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 1) {
            return email;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(prefix.charAt(0));
        for (int i = 1; i < prefix.length(); i++) {
            sb.append("*");
        }
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * 银行卡脱敏：保留前6后4
     */
    public static String bankCard(String card) {
        if (isEmpty(card) || card.length() < 10) {
            return card;
        }
        return card.substring(0, 6) + "******" + card.substring(card.length() - 4);
    }

    /**
     * 地址脱敏：保留前半段，后半*
     */
    public static String address(String address) {
        if (isEmpty(address) || address.length() <= 6) {
            return address;
        }
        int mid = address.length() / 2;
        return address.substring(0, mid) + "****";
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

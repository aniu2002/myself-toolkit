package com.sparrow.collect.cjf.utils;

public class QBChangeFilter {

    public static void main(String[] args) throws Exception {
        try {
            String QJstr = "HELLO";
            String QJstr1 = "ＨＥＬＬＯ";

            String result = BQchange(QJstr);
            String result1 = QBchange(QJstr1);

            System.out.println("半角：" + QJstr + "转换成全角：" + result);
            System.out.println("全角" + QJstr1 + "转换成半角：" + result1);
        } catch (Exception ex) {
            throw new Exception("ERROR:" + ex.getMessage());
        }
    }

    public static String BQchange(String QJstr) {
        char[] c = QJstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '　';
            } else if (c[i] < '')
                c[i] = ((char) (c[i] + 65248));
        }
        return new String(c);
    }

    public static String QBchange(String QJstr) {
        char[] c = QJstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '　') {
                c[i] = ' ';
            } else if ((c[i] > 65280) && (c[i] < 65375))
                c[i] = ((char) (c[i] - 65248));
        }
        return new String(c);
    }
}

package com.forte.demo.api;

import com.forte.demo.util.HttpclientUtil;

public class TranslateApi {
    public static String toEnglish(String param){
        String tkParam = param;
        param = HttpclientUtil.getURLEncoderString(param);
        String url = "https://translate.google.cn/translate_a/single?client=webapp&sl=zh-CN&tl" +
                "=en&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&otf=1&ssel=0&tsel=0&kc=2&tk="+tk(tkParam,"422392.71207223")+"&q=" + param;

        String data = HttpclientUtil.doGet(url);
        String result = data.split(",")[0];
        return result.substring(4,result.length()-1);
    }

    public static String toChinese(String param){
        tkParam = HttpclientUtil.getURLEncoderString(param);
        String url = "https://translate.google.cn/translate_a/single?client=webapp&sl=en&tl=" +
                "zh-CN&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&otf=2&ssel=3&tsel=6&kc=2&tk="+tk(tkParam,"422392.71207223")+"&q="+param;

        String data = HttpclientUtil.doGet(url);
        String result = data.split(",")[0];
        return result.substring(4,result.length()-1);
    }
    public static String toJapainese(String param){
        String tkParam = param;
        param = HttpclientUtil.getURLEncoderString(param);
        String url = "https://translate.google.cn/translate_a/single?client=webapp&sl=zh-CN&tl=" +
                "ja&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ssel=3&tsel=3&kc=0&tk="+tk(tkParam,"422392.71207223")+"&q="+param;

        String data = HttpclientUtil.doGet(url);
        String result = data.split(",")[0];
        return result.substring(4,result.length()-1);
    }

    //计算tk值
    public static String b(long a, String b) {
        for (int d = 0; d < b.length() - 2; d += 3) {
            char c = b.charAt(d + 2);
            int c0 = 'a' <= c ? charCodeAt(c, 0) - 87 : Number(c);
            long c1 = '+' == b.charAt(d + 1) ? a >> c0 : a << c0;
            a = '+' == b.charAt(d) ? a + c1 & 4294967295L : a ^ c1;
        }
        a = Number(a);
        return a + "";
    }

    public static String tk(String a, String TKK) {
        String[] e = TKK.split("\\.");
        int d = 0;
        int h = 0;
        int[] g = new int[a.length() * 3];
        h = Number(e[0]);
        for (int f = 0; f < a.length(); f++) {
            int c = charCodeAt(a, f);
            if (128 > c) {
                g[d++] = c;
            } else {
                if (2048 > c) {
                    g[d++] = c >> 6 | 192;
                } else {
                    if (55296 == (c & 64512) && f + 1 < a.length() && 56320 == (charCodeAt(a, f + 1) & 64512)) {
                        c = 65536 + ((c & 1023) << 10) + charCodeAt(a, ++f) & 1023;
                        g[d++] = c >> 18 | 240;
                        g[d++] = c >> 12 & 63 | 128;
                    } else {
                        g[d++] = c >> 12 | 224;
                        g[d++] = c >> 6 & 63 | 128;
                        g[d++] = c & 63 | 128;
                    }
                }
            }
        }
        int gl = 0;
        for (int b : g) {
            if (b != 0) {
                gl++;
            }
        }
        int[] g0 = new int[gl];
        gl = 0;
        for (int c : g) {
            if (c != 0) {
                g0[gl] = c;
                gl++;
            }
        }
        long aa = h;
        for (d = 0; d < g0.length; d++) {
            aa += g0[d];
            aa = Number(b(aa, "+-a^+6"));
        }
        aa = Number(b(aa, "+-3^+b+-f"));
        long bb = aa ^ Number(e[1]);
        aa = bb;
        aa = aa + bb;
        bb = aa - bb;
        aa = aa - bb;
        if (0 > aa) {
            aa = (aa & 2147483647) + 2147483648L;
        }
        aa %= (long) 1e6;
        return aa + "" + "." + (aa ^ h);
    }
    // 实现js的charAt方法
    public static char charAt(Object obj, int index) {
        char[] chars = obj.toString().toCharArray();
        return chars[index];
    }

    // 实现js的charCodeAt方法
    public static int charCodeAt(Object obj, int index) {
        char[] chars = obj.toString().toCharArray();
        return (int) chars[index];
    }

    // 实现js的Number方法
    public static int Number(Object cc) {
        try {
            long a = Long.parseLong(cc.toString());
            int b = a > 2147483647 ? (int) (a - 4294967296L) : a < -2147483647 ? (int) (a + 4294967296L) : (int) a;
            return b;
        } catch (Exception ex) {
            return 0;
        }
    }
}


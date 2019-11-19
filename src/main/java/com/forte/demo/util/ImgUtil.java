package com.forte.demo.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImgUtil {
    public static void createImage(String imgurl, String filePath) throws Exception {

        URL url = new URL(imgurl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = conn.getInputStream(); // 通过输入流获得图片数据
        byte[] getData = readInputStream(inputStream); // 获得图片的二进制数据

        File imageFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(getData);
        fos.close();
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}

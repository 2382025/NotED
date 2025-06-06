package com.example.noted;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * Metode Untuk mengirim HttpPostRequest
 * Metode ini mengambil 2 argumen
 * Metode pertama adalah URL dari Skrip yang digunakan untuk mengirimkan permintaan
 * Yang lainnya adalah HashMap dengan nilai pasangan nama yang berisi data yang akan
 * dikirim dengan permintaan
 */

public class RequestHandler {
    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {
        // Membuat URL
        URL url;

        // Objek StringBuilder untuk menyimpan pesan diambil dari server
        StringBuilder sb = new StringBuilder();
        try {
            // Inisialisasi URL
            url = new URL(requestURL);

            // Membuat Koneksi HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Konfigurasi koneksi
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Membuat Keluaran Stream
            OutputStream os = conn.getOutputStream();

            // Menulis Parameter Untuk Permintaan
            // Kita menggunakan metode getPostDataString yang didefinisikan di bawah ini
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;

                // Reading server response
                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String sendGetRequest(String requestURL, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder(requestURL);

        if (params != null && !params.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    sb.append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            sb.setLength(sb.length() - 1); // Hapus "&" terakhir
        }

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(sb.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.toString();
    }



    public String sendGetRequestParam(String requestURL, String id) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(requestURL + id);
            Log.d("RequestHandler", "Request URL: " + url.toString());

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            Log.e("RequestHandler", "Error in sendGetRequestParam: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d("RequestHandler", "Response: " + sb.toString());
        return sb.toString();
    }

    // Overload method agar bisa dipakai tanpa parameter (seperti sebelumnya)
    public String sendGetRequest(String requestURL) {
        return sendGetRequest(requestURL, null);
    }


    private String getPostDataString(HashMap<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


}

package edu.buffalo.cse.blue.maybe.metadata;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xcv58 on 9/16/15.
 */
public class Post {
    public void post(String urlString, JSONObject jsonObject) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);

            OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream()));
            writer.write(jsonObject.toString());
            writer.close();

            JSONObject responseJSONObject = this.getResponseJSONObject(connection);
            System.out.println(responseJSONObject.get(Constants.RESPONSE_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private JSONObject getResponseJSONObject(HttpURLConnection connection) {
        JSONObject jsonObject = new JSONObject();

        int responseCode = -1;
        InputStream inputStream = null;
        String responseMessage = null;

        try {
            responseCode = connection.getResponseCode();
            inputStream = connection.getInputStream();
            responseMessage = connection.getResponseMessage();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }
        int length = connection.getContentLength();

        jsonObject.put(Constants.RESPONSE_CODE, responseCode);
        jsonObject.put(Constants.RESPONSE_MESSAGE, responseMessage);
        jsonObject.put(Constants.RESPONSE_LENGTH, length);

//        InputStream errorStream = connection.getErrorStream();

        String content = this.readFromInputStream(inputStream, length, connection.getContentEncoding());
        jsonObject.put(Constants.RESPONSE_CONTENT, content);
//        String error = Utils.readFromInputStream(errorStream, length, connection.getContentEncoding());
//        jsonObject.put(Constants.RESPONSE_ERROR, error);

        return jsonObject;
    }

    private String readFromInputStream(InputStream inputStream, int length, String encoding) {
        if (inputStream == null) {
            return Constants.EMPTY;
        }
        if (encoding == null) {
            encoding = Constants.DEFAULT_ENCODING;
        }
        if (length == -1) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    stringBuilder.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        } else {
            byte[] bytes = new byte[length];
            try {
                inputStream.read(bytes, 0, length);
                return new String(bytes, encoding);
            } catch (IOException e) {
                e.printStackTrace();
                return Constants.EMPTY;
            }
        }
    }
}

package com.example.imgur_app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImgurClient {

    @Value("${imgur.client.id}")
    private String clientId;

    @Value("${imgur.base.url}")
    private String baseUrl;

    private final OkHttpClient httpClient = new OkHttpClient();

    // Upload Image
    public ImageMetadata uploadImage(byte[] imageBytes) throws Exception {
        String uploadUrl = baseUrl + "/image";

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", null, RequestBody.create(imageBytes))
                .build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(body)
                .addHeader("Authorization", "Client-ID " + clientId)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to upload image: " + response.message());
            }

            // Parse the response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(response.body().string());
            JsonNode data = responseJson.get("data");

            return new ImageMetadata(
                    data.get("id").asText(),
                    data.get("deletehash").asText(),
                    data.get("link").asText()
            );
        }
    }

    public static class ImageMetadata {
        private final String deleteHash;
        private final String link;

        public ImageMetadata(String imageId, String deleteHash, String link) {
            this.deleteHash = deleteHash;
            this.link = link;
        }

        public String getDeleteHash() {
            return deleteHash;
        }

        public String getLink() {
            return link;
        }
    }

    // View Image
    public String viewImage(String imageId) throws Exception {
        String viewUrl = baseUrl + "/image/" + imageId;

        Request request = new Request.Builder()
                .url(viewUrl)
                .get()
                .addHeader("Authorization", "Client-ID " + clientId) // Use Client-ID header
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch image: " + response.message());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }

    // Delete Image
    public String deleteImage(String deleteHash) throws Exception {
        String deleteUrl = baseUrl + "/image/" + deleteHash;

        Request request = new Request.Builder()
                .url(deleteUrl)
                .delete()
                .addHeader("Authorization", "Client-ID " + clientId) // Use Client-ID header
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to delete image: " + response.message());
            }
            assert response.body() != null;
            return response.body().string();
        }
    }
}

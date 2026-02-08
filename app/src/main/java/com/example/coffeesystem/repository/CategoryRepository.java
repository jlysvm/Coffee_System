package com.example.coffeesystem.repository;

import android.util.Log;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.callbacks.FetchCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryRepository {
    private static final String supabaseUrl = BuildConfig.SUPABASE_URL;
    private static final String supabaseKey = BuildConfig.SUPABASE_KEY;

    public void getCategories(FetchCallback<List<String>> callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl+"/rest/v1/categories?select=name";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);

                if (!response.isSuccessful()) {
                    callback.onError(response.code());
                    return;
                }
                if (responseBody.equals("[]")) {
                    callback.onNotFound();
                    return;
                }

                JSONArray jsonArray = new JSONArray(responseBody);
                List<String> categories = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    categories.add(obj.getString("name"));
                }

                callback.onSuccess(categories);

            } catch (Exception e) {
                Log.e("Login", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }
}

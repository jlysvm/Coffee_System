package com.example.coffeesystem.repository;

import android.util.Log;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.Drink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrinkRepository {
    private static final String supabaseUrl = BuildConfig.SUPABASE_URL;
    private static final String supabaseKey = BuildConfig.SUPABASE_KEY;

    public void getAllDrinks(FetchCallback<List<Drink>> callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl+"/rest/v1/rpc/get_all_drinks";
        RequestBody body = RequestBody.create("", MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
                List<Drink> drinks = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    drinks.add(new Drink(
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("image"),
                        obj.getString("category"),
                        obj.getString("ingredients")
                    ));
                }

                callback.onSuccess(drinks);

            } catch (Exception e) {
                Log.e("Login", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

}

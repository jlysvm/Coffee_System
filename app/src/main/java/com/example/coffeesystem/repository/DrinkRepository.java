package com.example.coffeesystem.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.activities.auth.LoginActivity;
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

        String json = "{\"p_userid\":"+LoginActivity.getAuthenticatedUser().getId()+"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(supabaseUrl+"/rest/v1/rpc/get_all_drinks")
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
                        obj.getLong("id"),
                        obj.getString("name"),
                        obj.getString("description"),
                        obj.getString("image"),
                        obj.getString("category"),
                        obj.getString("ingredients"),
                        obj.getBoolean("is_favorited")
                    ));
                }

                callback.onSuccess(drinks);

            } catch (Exception e) {
                Log.e("Supabase", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void getDrinkImage(String fileName, FetchCallback<Bitmap> callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = supabaseUrl + "/storage/v1/object/drink_images/" + fileName;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful()) {
                    byte[] imageBytes = response.body().bytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    callback.onSuccess(bitmap);
                }
                else if (response.code() == 404) callback.onNotFound();
                else callback.onError(response.code());

            } catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }
}

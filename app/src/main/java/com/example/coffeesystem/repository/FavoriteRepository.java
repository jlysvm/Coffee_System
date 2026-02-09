package com.example.coffeesystem.repository;

import android.util.Log;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.Drink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteRepository {
    private static final String supabaseUrl = BuildConfig.SUPABASE_URL;
    private static final String supabaseKey = BuildConfig.SUPABASE_KEY;

    public void getFavoriteDrinks(long userId, FetchCallback<List<Drink>> callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String json = "{\"p_userid\":"+userId+"}";
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(supabaseUrl + "/rest/v1/rpc/get_favorite_drinks")
                    .post(body)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);

                if (response.isSuccessful() && response.body() != null) {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (jsonArray.length() == 0) {
                        callback.onNotFound();
                        return;
                    }

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
                }
                else if (response.code() == 404) {
                    callback.onNotFound();
                }
                else {
                    callback.onError(response.code());
                }
            }
            catch (Exception e) {
                Log.e("Supabase", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void addFavoriteDrink(long userId, long drinkId, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String json = "{\"user_id\":" + userId + ",\"drink_id\":" + drinkId + "}";
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            Request request = new Request.Builder()
                .url(supabaseUrl+"/rest/v1/favorites")
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                Log.e("Supabase", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void removeFavoriteDrink(long userId, long drinkId, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            HttpUrl url = HttpUrl.parse(supabaseUrl + "/rest/v1/favorites")
                    .newBuilder()
                    .addQueryParameter("user_id", "eq." + userId)
                    .addQueryParameter("drink_id", "eq." + drinkId)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                Log.e("Supabase", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

}

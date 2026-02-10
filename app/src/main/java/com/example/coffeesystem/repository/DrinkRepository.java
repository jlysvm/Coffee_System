package com.example.coffeesystem.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.Drink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
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

        String json = "{\"p_userid\":"+UserManager.getInstance().getUser().getId()+"}";
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

    public void createDrink(Drink drink, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String json = "{"
                            + "\"p_name\": \"" + drink.getName() + "\","
                            + "\"p_description\": \"" + drink.getDescription() + "\","
                            + "\"p_image\": \"" + drink.getImage() + "\","
                            + "\"p_category\": \"" + drink.getCategory() + "\","
                            + "\"p_ingredients\": \"" + drink.getIngredients() + "\""
                        + "}";

            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(supabaseUrl + "/rest/v1/rpc/add_new_drink")
                    .post(body)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void updateDrink(Drink drink, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            String json = "{"
                            + "\"p_id\": " + drink.getId() + ","
                            + "\"p_name\": \"" + drink.getName() + "\","
                            + "\"p_description\": \"" + drink.getDescription() + "\","
                            + "\"p_category\": \"" + drink.getCategory() + "\","
                            + "\"p_ingredients\": \"" + drink.getIngredients() + "\""
                        + "}";

            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

            Request request = new Request.Builder()
                    .url(supabaseUrl + "/rest/v1/rpc/update_drink")
                    .post(body)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response: " + responseBody);

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                Log.e("Supabase", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }


    public void deleteDrink(long id, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            HttpUrl url = HttpUrl.parse(supabaseUrl + "/rest/v1/drinks")
                    .newBuilder()
                    .addQueryParameter("id", "eq." + id)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void uploadDrinkImage(Context context, Uri imageUri, String fileName, RequestCallback callback) {
        new Thread(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    callback.onNetworkError(new IOException("Unable to open image stream"));
                    return;
                }

                byte[] imageBytes = new byte[inputStream.available()];
                inputStream.read(imageBytes);
                inputStream.close();

                OkHttpClient client = new OkHttpClient();

                String bucketUrl = supabaseUrl+"/storage/v1/object/drink_images/"+fileName;

                RequestBody body = RequestBody.create(imageBytes, MediaType.get("image/*"));

                Request request = new Request.Builder()
                        .url(bucketUrl)
                        .put(body)
                        .addHeader("apikey", supabaseKey)
                        .addHeader("Authorization", "Bearer " + supabaseKey)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());
            }
            catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }
}

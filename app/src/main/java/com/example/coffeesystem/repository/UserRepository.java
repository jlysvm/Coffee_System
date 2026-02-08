package com.example.coffeesystem.repository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.callbacks.InsertCallback;
import com.example.coffeesystem.callbacks.UserFetchCallback;
import com.example.coffeesystem.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserRepository {
    private static final String supabaseUrl = BuildConfig.SUPABASE_URL;
    private static final String supabaseKey = BuildConfig.SUPABASE_KEY;

    public void insertUser(User user, InsertCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl + "/rest/v1/users";
        String jsonBody =
                "{"
                    + "\"username\":\"" + user.getUsername() + "\","
                    + "\"email\":\"" + user.getEmail() + "\","
                    + "\"password\":\"" + user.getPassword() + "\","
                    + "\"role_id\":\"" + Integer.toString(user.getRoleID()) + "\""
                + "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", "YOUR_SUPABASE_ANON_KEY")
                .addHeader("Authorization", "Bearer YOUR_SUPABASE_ANON_KEY")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());

            } catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void getUserByEmail(String email, UserFetchCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl
                    + "/rest/v1/users"
                    + "?email=eq." + email
                    + "&select=username,email,password,role_id"
                    + "&limit=1";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    callback.onError(response.code());
                    return;
                }

                String body = response.body().string();

                if (body.equals("[]")) {
                    callback.onNotFound();
                    return;
                }

                JSONObject json = new JSONArray(body).getJSONObject(0);

                User user = new User(
                    json.getString("username"),
                    json.getString("email"),
                    json.getString("password"),
                    Integer.parseInt(json.getString("role_id"))
                );

                callback.onSuccess(user);

            } catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }

    public static String getSupabaseUrl() {
        return supabaseUrl;
    }

    public static String getSupabaseKey() {
        return supabaseKey;
    }
}
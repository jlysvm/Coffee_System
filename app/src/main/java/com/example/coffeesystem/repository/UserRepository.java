package com.example.coffeesystem.repository;

import android.util.Log;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserRepository {
    private static final String supabaseUrl = BuildConfig.SUPABASE_URL;
    private static final String supabaseKey = BuildConfig.SUPABASE_KEY;

    public void insertUser(User user, RequestCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl + "/rest/v1/rpc/insert_user";
        String jsonBody =
                "{"
                    + "\"p_username\":\"" + user.getUsername() + "\","
                    + "\"p_email\":\"" + user.getEmail() + "\","
                    + "\"p_password\":\"" + user.getPassword() + "\","
                    + "\"p_role_name\":\"" + user.getRole() + "\""
                + "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", supabaseKey)
                .addHeader("Authorization", "Bearer " + supabaseKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError(response.code());

            } catch (Exception e) {
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void getUserByEmail(String email, FetchCallback<User> callback) {
        OkHttpClient client = new OkHttpClient();

        String url = supabaseUrl+"/rest/v1/rpc/get_user_by_email";
        String jsonBody = "{"+"\"p_email\":\""+email+"\"}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

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

                JSONObject json = new JSONArray(responseBody).getJSONObject(0);

                User user = new User(
                    json.getLong("id"),
                    json.getString("username"),
                    json.getString("email"),
                    json.getString("password"),
                    json.getString("role_name")
                );

                callback.onSuccess(user);

            } catch (Exception e) {
                Log.e("Login", "Error: ", e);
                callback.onNetworkError(e);
            }
        }).start();
    }

    public void updateUserField(long userId, String columnName, String newValue, RequestCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();


            HttpUrl url = HttpUrl.parse(supabaseUrl + "/rest/v1/accounts")
                    .newBuilder()
                    .addQueryParameter("id", "eq." + userId) // Filter: WHERE id = userId
                    .build();

            String json = "{\"" + columnName + "\":\"" + newValue + "\"}";

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(url)
                    .patch(body)
                    .addHeader("apikey", supabaseKey)
                    .addHeader("Authorization", "Bearer " + supabaseKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("Supabase", "Response body: " + responseBody);
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    Log.e("SupabaseUpdate", "Failed: " + response.code() + " " + response.message());
                    callback.onError(response.code());
                }
            } catch (Exception e) {
                Log.e("SupabaseUpdate", "Error", e);
                callback.onNetworkError(e);
            }
        }).start();
    }
}
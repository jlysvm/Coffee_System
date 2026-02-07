package com.example.coffeesystem.repository;

import com.example.coffeesystem.BuildConfig;
import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.SupabaseClientBuilderKt;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.storage.Storage;
import kotlin.Unit;

public class SupabaseProvider {
    private static SupabaseClient client;

    public static SupabaseClient getClient() {
        if (client == null) {
            client = SupabaseClientBuilderKt.createSupabaseClient(
                    BuildConfig.SUPABASE_URL,
                    BuildConfig.SUPABASE_KEY,
                    builder -> {
                        builder.install(Postgrest.Companion, config -> Unit.INSTANCE);
                        builder.install(Storage.Companion, config -> Unit.INSTANCE);
                        return null;
                    }
            );
        }
        return client;
    }
}

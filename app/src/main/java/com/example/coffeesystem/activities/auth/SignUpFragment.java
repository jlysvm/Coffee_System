package com.example.coffeesystem.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coffeesystem.callbacks.InsertCallback;
import com.example.coffeesystem.databinding.FragmentSignupBinding;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class SignUpFragment extends Fragment {

    private FragmentSignupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.signupButton.setOnClickListener(v -> handleSignUp());

        binding.loginLink.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void handleSignUp() {
        String username = binding.usernameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.setError("Please enter a valid email address");
            binding.emailInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInput.setError("Passwords do not match!");
            binding.confirmPasswordInput.requestFocus();
            return;
        }

        if (password.length() < 8) {
            binding.passwordInput.setError("Password must be at least 8 characters");
            binding.passwordInput.requestFocus();
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(username, email, hashedPassword, 2);
        UserRepository repository = new UserRepository();

        repository.insertUser(newUser, new InsertCallback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(SignUpFragment.this).navigateUp();
                });
            }

            @Override
            public void onError(int code) {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Sign up failed: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

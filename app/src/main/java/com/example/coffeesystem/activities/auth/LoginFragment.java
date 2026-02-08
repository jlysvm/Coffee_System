package com.example.coffeesystem.activities.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.UserFetchCallback;
import com.example.coffeesystem.databinding.FragmentLoginBinding;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        binding.loginButton.setOnClickListener(v -> handleLogin());

        binding.signUpLink.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_signup)
        );

        binding.guestLink.setOnClickListener(v ->
            Toast.makeText(requireContext(), "Continuing as Guest", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        String email = binding.usernameInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRepository repository = new UserRepository();

        repository.getUserByEmail(email, new UserFetchCallback() {
            @Override
            public void onSuccess(User user) {
                requireActivity().runOnUiThread(() -> {
                    if (BCrypt.checkpw(password, user.getPassword())) {
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to dashboard (Intent)
                    } else {
                        Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNotFound() {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(int code) {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Server error: " + code, Toast.LENGTH_SHORT).show()
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

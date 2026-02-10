package com.example.coffeesystem.activities.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserManager;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class EditProfile extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPassword;
    private final UserRepository userRepository = new UserRepository();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_profile);

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);

        refreshUI();

        if ("GUEST".equalsIgnoreCase(UserManager.getInstance().getUser().getRole())) {

            findViewById(R.id.btnEditUsername).setOnClickListener(v ->
                Toast.makeText(this, "Guests cannot edit their username", Toast.LENGTH_SHORT).show()
            );

            findViewById(R.id.btnEditEmail).setOnClickListener(v ->
                Toast.makeText(this, "Guests cannot edit their email", Toast.LENGTH_SHORT).show()
            );

            findViewById(R.id.btnChangePassword).setOnClickListener(v ->
                Toast.makeText(this, "Guests cannot edit their password", Toast.LENGTH_SHORT).show()
            );
        }
        else {
            // EDIT USERNAME
            findViewById(R.id.btnEditUsername).setOnClickListener(v ->
                    showEditDialog("Edit Username", "username", currentUser.getUsername())
            );

            // EDIT EMAIL
            findViewById(R.id.btnEditEmail).setOnClickListener(v ->
                    showEditDialog("Edit Email", "email", currentUser.getEmail())
            );

            // CHANGE PASSWORD
            findViewById(R.id.btnChangePassword).setOnClickListener(v ->
                    showChangePasswordDialog()
            );
        }
    }

    private void refreshUI() {
        currentUser = UserManager.getInstance().getUser();
        if (currentUser != null) {
            tvUsername.setText(currentUser.getUsername());
            tvEmail.setText(currentUser.getEmail());
            // Always mask the password
            tvPassword.setText("********");
        }
    }

    // Generic Dialog for Username and Email
    private void showEditDialog(String title, String dbColumn, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentValue);

        // Add some padding to the input box
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty() && !newValue.equals(currentValue)) {
                performUpdate(dbColumn, newValue);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Special Dialog for Password
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // We don't set the title here because it's already in the custom XML layout

        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        // Find the EditTexts from the custom layout
        final EditText etOldPass = dialogView.findViewById(R.id.etOldPass);
        final EditText etNewPass = dialogView.findViewById(R.id.etNewPass);
        final EditText etConfirmPass = dialogView.findViewById(R.id.etConfirmPass);

        builder.setPositiveButton("Change", (dialog, which) -> {
            // ... (Rest of the logic remains exactly the same) ...
            String oldPass = etOldPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            if (!oldPass.equals(currentUser.getPassword())) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPass.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hash the NEW password before sending to DB
            String hashedNewPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());

            // Send the HASH to the DB, but keep the PLAIN TEXT for the local session
            updatePassword(newPass, hashedNewPassword);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void performUpdate(String column, String newValue) {
        userRepository.updateUserField(currentUser.getId(), column, newValue, new RequestCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfile.this, "Update successful!", Toast.LENGTH_SHORT).show();

                    // Update Local User Session
                    User updatedUser = new User(
                            currentUser.getId(),
                            column.equals("username") ? newValue : currentUser.getUsername(),
                            column.equals("email") ? newValue : currentUser.getEmail(),
                            currentUser.getPassword(),
                            currentUser.getRole()
                    );
                    UserManager.getInstance().setUser(updatedUser);

                    refreshUI();
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() -> Toast.makeText(EditProfile.this, "Update failed: " + code, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() -> Toast.makeText(EditProfile.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updatePassword(String plainTextNewPassword, String hashedNewPassword) {
        // Send HASH to DB
        userRepository.updateUserField(currentUser.getId(), "password", hashedNewPassword, new RequestCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfile.this, "Password changed!", Toast.LENGTH_SHORT).show();

                    // Store PLAIN TEXT in Session (so they can view it/change it again)
                    User updatedUser = new User(
                            currentUser.getId(),
                            currentUser.getUsername(),
                            currentUser.getEmail(),
                            plainTextNewPassword,
                            currentUser.getRole()
                    );
                    UserManager.getInstance().setUser(updatedUser);
                    refreshUI();
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() -> Toast.makeText(EditProfile.this, "Failed to change password", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() -> Toast.makeText(EditProfile.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
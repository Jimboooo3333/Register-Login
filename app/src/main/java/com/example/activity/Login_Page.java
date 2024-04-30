package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login_Page extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    EditText LoginEmail;
    EditText LoginPassword;
    TextView toRegisterPage;
    Button LoginButton;
    String stringEmail;
    String stringPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        LoginEmail = findViewById(R.id.LoginEmail);
        LoginPassword = findViewById(R.id.LoginPassword);
        toRegisterPage = findViewById(R.id.toRegisterPage);
        LoginButton = findViewById(R.id.LoginButton);

        toRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Page.this, Register_Page.class));
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringEmail = LoginEmail.getText().toString();
                stringPassword = LoginPassword.getText().toString();
                if(!isValidEmail(stringEmail)) {
                    LoginEmail.setError("Invalid Email Address");
                    return;
                }
                if(stringPassword.isEmpty()) {
                    LoginPassword.setError("Password is Empty");
                    return;
                }
                if(stringPassword.length() < 8) {
                    LoginPassword.setError("Password is too short");
                    return;
                }
                database.collection("Account").whereEqualTo("email", stringEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if(querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String storedEmail = document.getString("email");
                                String storedPassword = document.getString("password");
                                String storedUserType = document.getString("userType");
                                if(storedPassword.equals(stringPassword)) {
                                    if(storedUserType.equals("user")) {
                                        startActivity(new Intent(Login_Page.this, UserDashboard.class));
                                    } else if(storedUserType.equals("admin")) {
                                        startActivity(new Intent(Login_Page.this, AdminDashboard.class));
                                    }
                                } else {
                                    Toast.makeText(Login_Page.this, "No account found with the email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Firestore", "Error getting documents:", task.getException());
                            }
                        }
                    }
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
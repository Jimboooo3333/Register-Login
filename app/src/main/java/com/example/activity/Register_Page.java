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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.DataOutputStream;

public class Register_Page extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    EditText RegisterEmail;
    EditText RegisterPassword;
    EditText RegisterRetypePassword;
    Button RegisterButton;
    TextView toLoginPage;
    EditText UserType;
    String stringEmail;
    String stringPassword;
    String stringRetypePassword;
    String stringUserType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        RegisterEmail = findViewById(R.id.RegisterEmail);
        RegisterPassword = findViewById(R.id.RegisterPassword);
        RegisterRetypePassword = findViewById(R.id.RegisterRetypePassword);
        RegisterButton = findViewById(R.id.RegisterButton);
        toLoginPage = findViewById(R.id.toLoginPage);
        UserType = findViewById(R.id.UserType);

        toLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register_Page.this, Login_Page.class));
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringEmail = RegisterEmail.getText().toString();
                stringPassword = RegisterPassword.getText().toString();
                stringRetypePassword = RegisterRetypePassword.getText().toString();
                stringUserType = UserType.getText().toString();

                try {
                    if(stringEmail.isEmpty()) {
                        RegisterEmail.setError("Email Address is Empty");
                        return;
                    }
                    if(!isValidEmail(stringEmail)) {
                        RegisterEmail.setError("Invalid Email Address");
                        return;
                    }
                    if(stringPassword.isEmpty()) {
                        RegisterPassword.setError("Password is empty");
                        return;
                    }
                    if(stringPassword.length() < 8) {
                        RegisterPassword.setError("Password is too short");
                        return;
                    }
                    if(!stringPassword.equals(stringRetypePassword)) {
                        RegisterPassword.setError("Password does not match");
                        RegisterRetypePassword.setError("Password does not match");
                        return;
                    }
                    if(stringUserType.isEmpty()) {
                        UserType.setError("User Type is empty");
                        return;
                    }

                    Register register = new Register(stringEmail, stringPassword, stringUserType);
                    saveToDatabase(register);
                } catch (Exception e) {
                    Log.e("RegisterException", e.getMessage());
                }

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
    private void saveToDatabase(Register register) {
        database.collection("Account").whereEqualTo("email", stringEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(!task.getResult().isEmpty()) {
                        Toast.makeText(Register_Page.this, "Email Address Already Exist", Toast.LENGTH_SHORT).show();
                    } else {
                        database.collection("Account").add(register).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(Register_Page.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register_Page.this, Login_Page.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register_Page.this, "Registered Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.d("Registration", "Error getting documents", task.getException());
                    Toast.makeText(Register_Page.this, "Error checking email existence", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
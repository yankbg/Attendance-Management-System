package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Register extends AppCompatActivity {
    EditText etvname, etvId;
    ImageView img;
    Button btnselect, btnregister;
    String base64Image;

    // ActivityResultLauncher for image selection
    private ActivityResultLauncher<Intent> selectImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etvname = findViewById(R.id.etvname);
        etvId = findViewById(R.id.etvId);
        img = findViewById(R.id.imageView);
        btnselect = findViewById(R.id.btnselect);
        btnregister = findViewById(R.id.btnregister);

        // Initialize the ActivityResultLauncher
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null) {
                                img.setImageURI(selectedImageUri);
                                try {
                                    Bitmap bitmap = getBitmapFromUri(this, selectedImageUri);
                                     base64Image = bitmapToBase64(bitmap);

                                }catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Failed to process selected image", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                }
        );

        btnselect.setOnClickListener(v -> selectPicture());

        // You can add btnregister click listener here
        btnregister.setOnClickListener(v -> {
            if (base64Image == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_LONG).show();
                return;
            }
            AddStudent(base64Image);
        });
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        selectImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // Convert Uri to Bitmap
    private Bitmap getBitmapFromUri(Context context, Uri selectedImageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);
    }

    // Then convert Bitmap to Base64 string
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();

        // Optional: Reject images > 2MB
        if (imageBytes.length > 2 * 1024 * 1024) {
            Toast.makeText(this, "Image too large (max 2MB)", Toast.LENGTH_LONG).show();
            return null;
        }
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void registerStudent(String base64Image){

        // 2. Prepare student data
        String studentId = etvId.getText().toString().trim();
        String studentName = etvname.getText().toString().trim();

        // 3. Call uploadStudentData method
        ApiUtil.uploadStudentData(this, studentId, studentName, base64Image,
                response -> {
                    Toast.makeText(this, "Add student loading...", Toast.LENGTH_SHORT).show();
                    // Success listener: handle JSON response from server
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");
                        Toast.makeText(this, "Upload success: " + message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Error listener: handle error case
                    Toast.makeText(this, "Registration failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );
    }
    public void AddStudent(String base64Image){
        String studentId = etvId.getText().toString().trim();
        String studentName = etvname.getText().toString().trim();
        if(studentName.isEmpty() || studentId.isEmpty()){
            Toast.makeText(this,"Please fill all required fields",Toast.LENGTH_LONG).show();
        }else{
            registerStudent(base64Image);
        }
    }


}

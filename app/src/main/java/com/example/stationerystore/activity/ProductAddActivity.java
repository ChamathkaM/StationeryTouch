package com.example.stationerystore.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stationerystore.R;
import com.example.stationerystore.Model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class ProductAddActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 10;

    EditText productName, price, qty, description, supplierName;
    Button button;
    private ImageView imageView;
    private Uri filePath;
    private String productImagePath;
    private DatabaseReference databaseProductNode;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        //set the activity title
        setTitle("Add Product");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initialize the firebase reference
        databaseProductNode = FirebaseDatabase.getInstance().getReference().child("Product");
        storageReference = FirebaseStorage.getInstance().getReference();

        //initialize the UI components
        productName = findViewById(R.id.product);
        price = findViewById(R.id.price);
        qty = findViewById(R.id.qty);

        description = findViewById(R.id.description);
        supplierName = findViewById(R.id.supplierName);
        imageView = findViewById(R.id.imageView11);

        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (TextUtils.isEmpty(productName.getText().toString()))
                        Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(price.getText().toString()))
                        Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(qty.getText().toString()))
                        Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(description.getText().toString()))
                        Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                    else if (description.length()>30)
                        Toast.makeText(getApplicationContext(), "Character length is too long", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(supplierName.getText().toString()))
                        Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();

                    else {
                        String id = databaseProductNode.push().getKey();

                        //get all the values
                        Product product = new Product();
                        product.setId(id);
                        product.setProduct(productName.getText().toString().trim());
                        product.setPrice(price.getText().toString().trim());
                        product.setQty(qty.getText().toString().trim());
                        product.setDescription(description.getText().toString().trim());
                        product.setSupplierName(supplierName.getText().toString().trim());

                        if (productImagePath != null)
                            product.setImage(productImagePath);

                        databaseProductNode.child(id).setValue(product);
                        Toast.makeText(getApplicationContext(), "Successfully inserted", Toast.LENGTH_SHORT).show();
                        clearControls();

                    }
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();

                }
            }

        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }


    /**
     * Select image from gallery
     */
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Upload selected image to the firebase storage
     */
    private void uploadImage() {

        if (filePath != null) {
            //display progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("Product/" + UUID.randomUUID().toString());


            UploadTask uploadTask = ref.putFile(filePath);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            productImagePath = uri.toString();
                            Picasso.with(ProductAddActivity.this).load(uri.toString()).into(imageView);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(progress);
                }
            });
        }
    }


    public static float getTotalPrice(float price, float qty){
        return (price*qty);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private void clearControls() {
        imageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
        productName.setText("");
        price.setText("");
        qty.setText("");
        description.setText("");
        supplierName.setText("");
    }
}
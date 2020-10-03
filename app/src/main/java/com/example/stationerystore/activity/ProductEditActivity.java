package com.example.stationerystore.activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stationerystore.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductEditActivity extends AppCompatActivity {

    private static final String TAG = ProductEditActivity.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST = 10;

    //our database reference object
    DatabaseReference databaseProductChildNode;
    StorageReference storageReference;
    private EditText edt_name;
    private EditText edt_description;
    private EditText edt_price;
    private EditText edt_qty;
    private CircularImageView img_product;
    private Uri filePath;
    private String productImagePath;
    private EditText edt_supplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String itemId = getIntent().getStringExtra("productId");

        setTitle("Edit Product");

        databaseProductChildNode = FirebaseDatabase.getInstance().getReference().child("Product").child(itemId);
        storageReference = FirebaseStorage.getInstance().getReference();

        edt_name = findViewById(R.id.edt_name);
        edt_description = findViewById(R.id.edt_description);
        edt_price = findViewById(R.id.edt_price);
        edt_qty = findViewById(R.id.edt_qty);
        edt_supplier = findViewById(R.id.edt_supplier);
        Button btn_update = findViewById(R.id.btn_update);
        Button btn_cancel = findViewById(R.id.btn_cancel);

        img_product = findViewById(R.id.img_product);
        CircularImageView img_plus = findViewById(R.id.img_plus);

        databaseProductChildNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("product").getValue(String.class) != null) {
                    Log.i(TAG, dataSnapshot.child("product").getValue(String.class));

                    edt_name.setText(dataSnapshot.child("product").getValue(String.class));
                    edt_description.setText(dataSnapshot.child("description").getValue(String.class));
                    edt_price.setText(dataSnapshot.child("price").getValue(String.class));
                    edt_qty.setText(dataSnapshot.child("qty").getValue(String.class));
                    edt_supplier.setText(dataSnapshot.child("supplierName").getValue(String.class));

                    if (dataSnapshot.child("image").getValue(String.class) != null) {
                        Picasso.with(ProductEditActivity.this).load(dataSnapshot.child("image").getValue(String.class)).into(img_product);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name = edt_name.getText().toString().trim();
                String description = edt_description.getText().toString().trim();
                String price = edt_price.getText().toString().trim();
                String qty = edt_qty.getText().toString().trim();
                String supplier = edt_supplier.getText().toString().trim();


                if (!TextUtils.isEmpty(name)) {
                    Map<String, Object> itemMap = new HashMap<String, Object>();
                    itemMap.put("product", name);
                    itemMap.put("description", description);
                    itemMap.put("price", price);
                    itemMap.put("image", productImagePath);
                    itemMap.put("qty", qty);
                    itemMap.put("supplierName", supplier);
                    if (productImagePath != null)
                        itemMap.put("image", productImagePath);

                    saveProduct(itemMap);
                } else {
                    //if the value is not given displaying a toast
                    Toast.makeText(ProductEditActivity.this, "Please enter a name", Toast.LENGTH_LONG).show();
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    /***
     * Choose image when use tap the image view
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
                img_product.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Upload image to the firebase storage
     */
    private void uploadImage() {

        if (filePath != null) {
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
                            Picasso.with(ProductEditActivity.this).load(uri.toString()).into(img_product);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteProduct();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Delete single product from the server
     */
    private void deleteProduct() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductEditActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseProductChildNode.removeValue();
                dialog.dismiss();
                finish();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }


    /**
     * Save updated product details in firebase
     * @param s product
     */
    private void saveProduct(Map<String, Object> s) {
        databaseProductChildNode.updateChildren(s);

        //displaying a success toast
        Toast.makeText(this, "Product Updated", Toast.LENGTH_LONG).show();
    }
}

package com.example.stationerystore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stationerystore.R;
import com.example.stationerystore.adapter.ProductAdapter;
import com.example.stationerystore.Model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private List<Product> products = new ArrayList<>();
    private ProductAdapter mAdapter;
    private RecyclerView recyclerView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        //set the activity title
        setTitle("Products");

        //initialize firebase product reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");

        FloatingActionButton fab = findViewById(R.id.fabProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProductAddActivity.class));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        initAdapter();
        fetchProducts();

    }


    /**
     * initialize the adapter to display product details
     */
    private void initAdapter() {
        mAdapter = new ProductAdapter(ProductListActivity.this, products, new ProductAdapter.ProductItemListener() {
            @Override
            public void onProductItemClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductEditActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }


    /**
     * Retrieve the product details from firebase
     */
    private void fetchProducts() {
        //attaching value event listener
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                products.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Product item = postSnapshot.getValue(Product.class);
                    //adding artist to the list
                    products.add(item);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

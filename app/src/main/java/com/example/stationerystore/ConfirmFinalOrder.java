package com.example.stationerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stationerystore.Model.Cart;
import com.example.stationerystore.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmFinalOrder extends AppCompatActivity {
    private TextView txtTotalAmountcal;
    private RecyclerView cartlist;
    private RecyclerView.LayoutManager layoutManager;
    private Button next;
    //public int overallTotPrice = 0;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        txtTotalAmountcal = (TextView) findViewById(R.id.total_price1);
        next = (Button) findViewById(R.id.next);

        //navigate to the next activity onclick of the next button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmFinalOrder.this,FeedbackActivity.class);
                startActivity(intent);
            }
        });

        cartlist = findViewById(R.id.cart_list);
        cartlist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cartlist.setLayoutManager(layoutManager);
        //totalAmount = getIntent().getStringExtra("Total Price");
        txtTotalAmountcal.setText("Total Price = " + String.valueOf(CartActivity.overallTotPrice));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")//refernce to the cartlist node
                        .child("1234567890").child("Products"), Cart.class).build();
        //.child(Prevalent.currentOnlineUser.getPhone()).child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                holder.txtProductPrice.setText("Price :" + model.getPrice());
                holder.txtProductName.setText(model.getPname());
            }
        };
        cartlist.setAdapter(adapter);
        adapter.startListening();
    }
}
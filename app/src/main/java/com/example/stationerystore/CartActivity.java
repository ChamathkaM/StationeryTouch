package com.example.stationerystore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.example.stationerystore.Model.Cart;
import com.example.stationerystore.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount;
    public static int overallTotPrice = 0;
    private static final String TAG = "CartActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessBtn = (Button) findViewById(R.id.confirm);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtTotalAmount.setText("Total Price = Rs"+String.valueOf(overallTotPrice));
                Toast.makeText(CartActivity.this, "Total Price = Rs"+overallTotPrice, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrder.class);
                intent.putExtra("Total Price",String.valueOf(overallTotPrice));
                startActivity(intent);
                finish();
            }
        });
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
                =new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductPrice.setText("Price :"+model.getPrice());
                holder.txtProductName.setText(model.getPname());

                //calculate total price of a single item
                int onepPrice=oneProductTotPrice(((Integer.parseInt(model.getPrice()))),Integer.parseInt(model.getQuantity()));
                Log.i(TAG, "onBindViewHolder: "+model.getPrice());


                //calculate the overall total price
                overallTotPrice = overallTotPrice(onepPrice);
                //for edit and delete of items
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){//user clicks on edit button
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);//directs the user to the productsDetails page
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1){//user clicks on delete button
                                    cartListRef.child("User View")
                                            .child("1234567890")
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){//if item has been deleted successfully
                                                        Toast.makeText(CartActivity.this,"Item Removed successfully",Toast.LENGTH_SHORT);
                                                        Intent intent = new Intent(CartActivity.this, SearchProductActivity.class);//directs the user to the SearchProductActivity page
                                                        startActivity(intent);
                                                    }
                                                }
                                            });

                                }
                            }
                        });
                        builder.show();
                    }
                });
            }


            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public int oneProductTotPrice(int price,int qty){
        return price * qty;
    }

    public int overallTotPrice(int oneproductprice){
        return overallTotPrice+oneproductprice;
    }
}

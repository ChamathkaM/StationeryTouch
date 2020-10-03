package com.example.stationerystore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategory extends AppCompatActivity {

    private ImageView penholder,file,stapler;
    private ImageView highlighter,notebook,lettercover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admincategory);

        penholder = (ImageView) findViewById(R.id.penhodler);
        file = (ImageView) findViewById(R.id.file);
        stapler = (ImageView) findViewById(R.id.stapler);
        highlighter = (ImageView) findViewById(R.id.highligher);
        notebook = (ImageView) findViewById(R.id.notebook);
        lettercover = (ImageView) findViewById(R.id.lettercover);

        penholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","penholder");
                startActivity(intent);
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","file");
                startActivity(intent);
            }
        });

        stapler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","stapler");
                startActivity(intent);
            }
        });

        highlighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","highlighter");
                startActivity(intent);
            }
        });

        notebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","notebook");
                startActivity(intent);
            }
        });

        lettercover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategory.this,AdminAddNewProductActivity.class);
                intent.putExtra("category","lettercover");
                startActivity(intent);
            }
        });

    }
}
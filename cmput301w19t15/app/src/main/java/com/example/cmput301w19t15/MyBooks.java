package com.example.cmput301w19t15;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;


public class MyBooks extends AppCompatActivity {

    private static final int NEW_BOOK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        Button addBook = (Button) findViewById(R.id.add_book);

        addBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent addIntent = new Intent(MyBooks.this, AddBookInfo.class);
                startActivityForResult(addIntent, NEW_BOOK);
            }
        });

        User loggedInUser = MainActivity.getUser();
        ArrayList<Book> myBooks = loggedInUser.getMyBooks();
        TextView textView = findViewById(R.id.textView2);
        textView.setText("Number of books: " + myBooks.size());
    }
}
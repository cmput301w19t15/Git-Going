package com.example.cmput301w19t15.Functions;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.cmput301w19t15.Objects.Book;
import com.example.cmput301w19t15.Objects.BookAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// @reuse: https://github.com/google-developer-training/android-fundamentals/tree/master/WhoWroteIt
public class FetchBookWithList extends AsyncTask<String, Void, ArrayList<Book>> {

    private ArrayList<Book> bookList;
    private ArrayList<String> bookListID;
    private BookAdapter bookAdapter;


    public FetchBookWithList(ArrayList<Book> bookList, ArrayList<String> idList){
        this.bookList = bookList;
        this.bookListID = idList;
    }

    public FetchBookWithList(ArrayList<String> idList, BookAdapter bookAdapter){
        this.bookList = bookList;
        this.bookListID = idList;
        this.bookAdapter = bookAdapter;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... strings) {
        try{
            DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference().child("books");
            bookReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        try {
                            for (DataSnapshot books : dataSnapshot.getChildren()) {
                                if(bookListID.contains(books.child("bookID").getValue())) {
                                    Book book = books.getValue(Book.class);
                                    bookList.add(book);
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("testing","Error: ", databaseError.toException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> s){
        super.onPostExecute(s);
        try {
            if(this.bookAdapter != null) {
                this.bookAdapter.notifyDataSetChanged();
            }
        } catch (Exception e){
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            e.printStackTrace();
        }

    }
}

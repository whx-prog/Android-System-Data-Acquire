package com.example.infomationcollector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GetContactors extends AppCompatActivity {
    private Cursor cursor;
    private List<String> newContactors;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_contactors);

        listView = findViewById(R.id.lv1);
        newContactors = new ArrayList<>();

        cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,null);

        while (cursor.moveToNext()) {
            String get_name;
            get_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String get_number;
            get_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            newContactors.add(get_name + ":\n" + get_number);
        }
        adapter = new ArrayAdapter<>(this, R.layout.contact, R.id.array_txt, newContactors);

        listView.setAdapter(adapter);
    }
}
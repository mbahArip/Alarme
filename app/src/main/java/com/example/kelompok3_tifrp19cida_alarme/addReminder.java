package com.example.kelompok3_tifrp19cida_alarme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.sql.Time;
import java.util.Calendar;

public class addReminder extends AppCompatActivity {
    final Calendar currentTime = Calendar.getInstance();
    TimePickerDialog timePicker;
    final Calendar currentDate = Calendar.getInstance();
    DatePickerDialog datePicker;
    String add_Time;
    String add_Date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add);
        getSupportActionBar().hide();

        Button add = findViewById(R.id.add_tambah);
        Button back = findViewById(R.id.add_batal);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(addReminder.this);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToDashboard();
            }
        });
    }

    public void showTimePicker(Context c){
        int currentHours = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = currentTime.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                add_Time = hourOfDay + ":" + minute;
                showDatePicker(addReminder.this);
            }
        };
        timePicker = new TimePickerDialog(c, android.R.style.Theme_Holo_Dialog_MinWidth, timeListener, currentHours, currentMinutes,true);
        timePicker.setTitle("Tentukan jam");
        timePicker.show();
    }

    public void showDatePicker(Context c){
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDOM = currentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                add_Date = dayOfMonth + "/" + month + "/" + year;
                addToDatabase();
            }
        };
        datePicker = new DatePickerDialog(c, android.R.style.Theme_Holo_Dialog_MinWidth, dateListener, currentYear, currentMonth, currentDOM);
        datePicker.setTitle("Tentukan tanggal");
        datePicker.show();
    }

    public void addToDatabase(){
        //Add data to database
        //Tambah disini

        Toast.makeText(addReminder.this, "Data berhasil ditambah", Toast.LENGTH_LONG).show();
        //Balik ke dashboard
        backToDashboard();
    }
    public void backToDashboard(){
        Intent intent = new Intent(addReminder.this, dashboard.class);
        startActivity(intent);
        finish();
    }
}

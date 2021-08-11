package com.example.kelompok3_tifrp19cida_alarme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelompok3_tifrp19cida_alarme.util.dbHelper;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class addReminder extends AppCompatActivity {
    final Calendar currentTime = Calendar.getInstance();
    TimePickerDialog timePicker;
    final Calendar currentDate = Calendar.getInstance();
    DatePickerDialog datePicker;
    String add_Time;
    String add_Date;
    EditText inputTitle, inputDesc;
    dbHelper db = new dbHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add);
        getSupportActionBar().hide();


        Button add = findViewById(R.id.add_tambah);
        Button back = findViewById(R.id.add_batal);
        inputTitle = findViewById(R.id.add_title);
        inputDesc = findViewById(R.id.add_description);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(inputTitle.getText().toString()) || TextUtils.isEmpty(inputDesc.getText().toString())){
                    Toast.makeText(addReminder.this, "Pastikan data telah diisi!", Toast.LENGTH_LONG).show();
                } else {
                    showTimePicker(addReminder.this);
                }
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
                String inputTime = hourOfDay + ":" + minute;
                SimpleDateFormat beforeFormat = new SimpleDateFormat("kk:mm");
                SimpleDateFormat afterFormat = new SimpleDateFormat("HH:mm");
                try {
                    add_Time = afterFormat.format(beforeFormat.parse(inputTime));
                } catch(ParseException e) {
                    Log.e("AddData", "Error");
                    e.printStackTrace();
                    return;
                }
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
                int bulan = month + 1;
                String inputTime = dayOfMonth + "/" + bulan + "/" + year;
                SimpleDateFormat beforeFormat = new SimpleDateFormat("dd/M/yyyy");
                SimpleDateFormat afterFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    add_Date = afterFormat.format(beforeFormat.parse(inputTime));
                } catch(ParseException e) {
                    Log.e("AddData", "Error");
                    e.printStackTrace();
                    return;
                }
                addToDatabase();
            }
        };
        datePicker = new DatePickerDialog(c, android.R.style.Theme_Holo_Dialog_MinWidth, dateListener, currentYear, currentMonth, currentDOM);
        datePicker.setTitle("Tentukan tanggal");
        datePicker.show();
    }

    public void addToDatabase(){
        //Add data to database
        String timestamp = add_Time + " " + add_Date;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        long unix = 0;
        try {
            Date date = sdf.parse(timestamp);
            unix = date.getTime();
            timestamp = String.valueOf(unix);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("AddData", inputTitle.getText().toString());
        Log.d("AddData", inputDesc.getText().toString());
        Log.d("AddData", String.valueOf(unix));
        try {
            db.insertData(inputTitle.getText().toString(), inputDesc.getText().toString(), String.valueOf(unix));
        }catch (Exception e){
            Toast.makeText(addReminder.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        Toast.makeText(addReminder.this, "Data berhasil ditambah", Toast.LENGTH_SHORT).show();
        //Balik ke dashboard
        backToDashboard();
    }
    public void backToDashboard(){
        Intent intent = new Intent(addReminder.this, dashboard.class);
        startActivity(intent);
        finish();
    }
}

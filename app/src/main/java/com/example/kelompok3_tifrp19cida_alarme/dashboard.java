package com.example.kelompok3_tifrp19cida_alarme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class dashboard extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    String gtaskTitle[] = {"Tugas A", "Tugas B", "Tugas C", "Tugas A", "Tugas B", "Tugas C", "Tugas A", "Tugas B", "Tugas C"};
    String gtaskDescription[] = {"Tugas A description", "Tugas B description", "Tugas C description", "Tugas A description", "Tugas B description", "Tugas C description", "Tugas A description", "Tugas B description", "Tugas C description"};
    String gtaskTime[] = {"20:00", "08:00", "12:00", "20:00", "08:00", "12:00", "20:00", "08:00", "12:00"};
    String gtaskDate[] = {"21/07/2021", "22/07/2021", "23/07/2021", "21/07/2021", "22/07/2021", "23/07/2021", "21/07/2021", "22/07/2021", "23/07/2021"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        RelativeLayout emptyView = (RelativeLayout) findViewById(R.id.dash_reLayout);
        ListView taskList = (ListView) findViewById(R.id.dash_tasklist);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(gtaskTitle.length == 0){
            emptyView.setVisibility(View.VISIBLE);
            taskList.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            taskList.setVisibility(View.VISIBLE);

            populateTask populate = new populateTask(this, gtaskTitle, gtaskDescription, gtaskTime, gtaskDate);
            taskList.setAdapter(populate);

            taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String message = "Touch position : " + position;
//                    Toast.makeText(dashboard.this, message, Toast.LENGTH_SHORT).show();
//                    System.out.println(view);
                    openMenu(view, position);
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, addReminder.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class populateTask extends ArrayAdapter<String>{
        Context context;
        String taskTitle[];
        String taskDescription[];
        String taskTime[];
        String taskDate[];

        populateTask(Context c, String title[], String description[], String time[], String date[]){
            super(c, R.layout.task_item, R.id.list_title, title);
            this.context = c;
            this.taskTitle = title;
            this.taskDescription = description;
            this.taskTime = time;
            this.taskDate = date;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View item = layoutInflater.inflate(R.layout.task_item, parent, false);
            TextView itemTitle = item.findViewById(R.id.list_title);
            TextView itemDesc = item.findViewById(R.id.list_description);
            TextView itemTime = item.findViewById(R.id.list_time);
            TextView itemDate = item.findViewById(R.id.list_date);

            itemTitle.setText(taskTitle[position]);
            itemDesc.setText(taskDescription[position]);
            itemTime.setText(taskTime[position]);
            itemDate.setText(taskDate[position]);

            return item;
        }
    }

    public void openMenu(View v, final int position) {
        PopupMenu menu = new PopupMenu(this, v, Gravity.END);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.item_menu);
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
//                  case R.id.itemmenu_edit:
//                      Toast.makeText(dashboard.this, "Edit reminder", Toast.LENGTH_SHORT).show();
//                      return true;
                    case R.id.itemmenu_delete:
                        System.out.println(position);
                        AlertDialog alert = new AlertDialog.Builder(dashboard.this)
                                .setTitle("Hapus reminder")
                                .setMessage("Apa anda yakin ingin menghapus reminder ini?")
                                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(dashboard.this, "Delete reminder", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.itemmenu_edit:
//                Toast.makeText(dashboard.this, "Edit reminder", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.itemmenu_delete:
                AlertDialog alert = new AlertDialog.Builder(this)
                        .setTitle("Hapus reminder")
                        .setMessage("Apa anda yakin ingin menghapus reminder ini?")
                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(dashboard.this, "Delete reminder", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
                return true;
            default:
                return false;
        }
    }

}
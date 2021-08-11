package com.example.kelompok3_tifrp19cida_alarme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
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

import com.example.kelompok3_tifrp19cida_alarme.util.alarmHelper;
import com.example.kelompok3_tifrp19cida_alarme.util.dbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/*
DONE: Check ulang date to unix di addReminder.
DONE: Check ulang unix to date di dashboard.
TODO: Notifikasi, terus alarm.
 */
public class dashboard extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    dbHelper db = new dbHelper(this);
    alarmHelper ah = new alarmHelper();
    ArrayList<JSONObject> listItem;
    RelativeLayout emptyView;
    ListView taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        listItem = new ArrayList<>();
        try {
            viewData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        emptyView = findViewById(R.id.dash_reLayout);
        taskList = findViewById(R.id.dash_tasklist);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, addReminder.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void viewData() throws JSONException {
        Cursor cursor = db.viewData();

        if(cursor.getCount() == 0){
            this.showEmpty();
//            Toast.makeText(dashboard.this, "Tidak ada data yang dapat di load! Silahkan buat reminder!", Toast.LENGTH_LONG).show();
        } else {
            this.hideEmpty();

            while (cursor.moveToNext()){
                JSONObject json = new JSONObject();
                json.put("id", cursor.getString(0));
                json.put("title", cursor.getString(1));
                json.put("desc", cursor.getString(2));
                json.put("time", cursor.getString(3));
                listItem.add(json);
                Log.d("Timestamp", json.toString());
            }
            ListView list = findViewById(R.id.dash_tasklist);

            ArrayList<String> arrayId = new ArrayList<String>();
            ArrayList<String> arrayTitle = new ArrayList<String>();
            ArrayList<String> arrayDesc = new ArrayList<String>();
            ArrayList<String> arrayTime = new ArrayList<String>();
            ArrayList<String> arrayDate = new ArrayList<String>();
            for(int i = 0; i < listItem.size(); i++){
                JSONObject json = new JSONObject(listItem.get(i).toString());
                int id = json.getInt("id");
                String title = json.getString("title");
                String desc = json.getString("desc");
                String time = json.getString("time");

                //Format time here
                Long unix = Long.valueOf(time);
                String timestamp = new Date(unix).toString();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                Date d = null;
                try {
                    d = sdf.parse(timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Format to Hour and Day
                SimpleDateFormat timeHour = new SimpleDateFormat("HH:mm");
                SimpleDateFormat timeDay = new SimpleDateFormat("dd / MMM / yyyy");
                String strTimeHour = timeHour.format(d);
                String strTimeDay = timeDay.format(d);

                arrayId.add(String.valueOf(id));
                arrayTitle.add(title);
                arrayDesc.add(desc);
                arrayTime.add(strTimeHour);
                arrayDate.add(strTimeDay);

                ah.setAlarm(dashboard.this, unix, title, desc, id);
            }
            Log.d("ViewData", arrayId.toString());

            String[] tId = arrayId.toArray(new String[arrayTitle.size()]);
            String[] tTitle = arrayTitle.toArray(new String[arrayTitle.size()]);
            String[] tDesc = arrayDesc.toArray(new String[arrayTitle.size()]);
            String[] tTime = arrayTime.toArray(new String[arrayTitle.size()]);
            String[] tDate = arrayDate.toArray(new String[arrayTitle.size()]);
            populateTask populate = new populateTask(this, tId, tTitle, tDesc, tTime, tDate);
            list.setAdapter(populate);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int passedId = Integer.valueOf((String) list.getAdapter().getItem(position));
                    openMenu(view, position, passedId);
                }
            });

        }
    }

    class populateTask extends ArrayAdapter<String>{
        Context context;
        String[] taskId;
        String[] taskTitle;
        String[] taskDescription;
        String[] taskTime;
        String[] taskDate;

        populateTask(Context c, String[] id, String[] title, String[] description, String[] time, String[] date){
            super(c, R.layout.task_item, R.id.list_title, id);
            this.context = c;
            this.taskId = id;
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
            TextView itemId = item.findViewById(R.id.list_id);
            TextView itemTitle = item.findViewById(R.id.list_title);
            TextView itemDesc = item.findViewById(R.id.list_description);
            TextView itemTime = item.findViewById(R.id.list_time);
            TextView itemDate = item.findViewById(R.id.list_date);

            itemId.setText(taskId[position]);
            itemTitle.setText(taskTitle[position]);
            itemDesc.setText(taskDescription[position]);
            itemTime.setText(taskTime[position]);
            itemDate.setText(taskDate[position]);

            return item;
        }
    }

    public void openMenu(View v, final int position, int id) {
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
                                        try {
                                            db.deleteData(id);
                                            Toast.makeText(dashboard.this, "Reminder berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                            recreate();
                                        }catch (SQLException e){
                                            e.printStackTrace();
                                            Toast.makeText(dashboard.this, "Reminder gagal dihapus!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
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
//        switch (item.getItemId()){
////            case R.id.itemmenu_edit:
////                Toast.makeText(dashboard.this, "Edit reminder", Toast.LENGTH_SHORT).show();
////                return true;
//            case R.id.itemmenu_delete:
//                AlertDialog alert = new AlertDialog.Builder(this)
//                        .setTitle("Hapus reminder")
//                        .setMessage("Apa anda yakin ingin menghapus reminder ini?")
//                        .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try{
////                                    db.deleteData();
//                                    Toast.makeText(dashboard.this, "Reminder berhasil dihapus!", Toast.LENGTH_SHORT).show();
//                                } catch (Exception e){
//                                    e.printStackTrace();
//                                    Toast.makeText(dashboard.this, "Reminder gagal dihapus!", Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            }
//                        })
//                        .setNegativeButton("Batal", null)
//                        .show();
//                return true;
//            default:
//                return false;
//        }
        return false;
    }

    public void showEmpty(){
        emptyView = findViewById(R.id.dash_reLayout);
        taskList = findViewById(R.id.dash_tasklist);
        emptyView.setVisibility(View.VISIBLE);
        taskList.setVisibility(View.GONE);
    }
    public void hideEmpty(){
        emptyView = findViewById(R.id.dash_reLayout);
        taskList = findViewById(R.id.dash_tasklist);
        emptyView.setVisibility(View.GONE);
        taskList.setVisibility(View.VISIBLE);
    }
    public void refresh(){
        recreate();
    }
}
package com.example.xwn.calendarmanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.provider.CalendarContract.Calendars;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SimpleDateFormat df;
    private Date startDate;

    private NumberPicker weekNumber;
    private NumberPicker dayNumber;
    private EditText eventTitleEdit;
    private EditText eventLocationEdit;
    private Button saveEvent;
    private AlertDialog classDialog;
    private ActionBar actionBar;

    private List<EventRecord> mEventRecordList;
    private RecyclerView itemRecycler;
    private MyAdapter myAdapter;

    //用到的相关URL
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        mEventRecordList = new ArrayList<>();
        initInsert();
        initDialog();
        initRecycleView();

        df = new SimpleDateFormat("yyyy-MM-dd"); //默认的日期格式

        try {
            startDate = df.parse("2016-08-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initInsert() {
        Button tryInsert = (Button) findViewById(R.id.try_insert);

        tryInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "点击");
                inputAccount();

            }
        });
    }
    //读取现有日历账户
    private void readUser(){
        Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);

        System.out.println("Count: " + userCursor.getCount());
        Toast.makeText(this, "Count: " + userCursor.getCount(), Toast.LENGTH_LONG).show();

        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            System.out.println("name: " + userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME")));

            String userName1 = userCursor.getString(userCursor.getColumnIndex("name"));
            String userName0 = userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME"));
            Toast.makeText(this, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0, Toast.LENGTH_LONG).show();
        }
    }
    //
    private void inputAccount(){
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "yy");

        value.put(Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com");
        value.put(Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "mytt");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.android.exchange")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initRecycleView() {
        itemRecycler = (RecyclerView) findViewById(R.id.item_recycler);
        itemRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemRecycler.setAdapter(myAdapter = new MyAdapter(MainActivity.this,mEventRecordList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                classDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化对话框
    private void initDialog() {
        //初始化添加事件对话框
        AlertDialog.Builder classDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View classDialogView = getLayoutInflater().inflate(R.layout.class_dialog,null);
        eventTitleEdit = (EditText) classDialogView.findViewById(R.id.event_title_edit);
        eventLocationEdit = (EditText) classDialogView.findViewById(R.id.event_location_edit);
        weekNumber = (NumberPicker) classDialogView.findViewById(R.id.week_number);
        dayNumber = (NumberPicker) classDialogView.findViewById(R.id.day_number);
        saveEvent = (Button) classDialogView.findViewById(R.id.save_event);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date tempDate = null;
                try {
                    tempDate = df.parse(calculateDateByWeeks(startDate,weekNumber.getValue(),dayNumber.getValue(),df));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                EventRecord eventRecord = new EventRecord(eventTitleEdit.getText().toString(),tempDate,weekNumber.getValue(),dayNumber.getValue(),eventLocationEdit.getText().toString());
                Log.d(TAG, "onClick:\n"+eventRecord.toString());
//                eventRecord.save();
                classDialog.dismiss();
                myAdapter.add(eventRecord,0);
                Log.d(TAG, "列表现有项目："+ mEventRecordList.size());
            }
        });
        initNumberPicker();
        classDialogBuilder.setView(classDialogView);
        classDialog = classDialogBuilder.create();
    }
    //初始化数字选择器
    private void initNumberPicker() {
        weekNumber.setMinValue(1);
        weekNumber.setMaxValue(20);
        weekNumber.setValue(1);
        dayNumber.setMinValue(1);
        dayNumber.setMaxValue(7);
        dayNumber.setValue(1);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        List<EventRecord> mEventRecordList;
        Context context;
        public MyAdapter(Context context,List<EventRecord> mEventRecordList){
            this.context = context;
            this.mEventRecordList = mEventRecordList;
        }
        public void add(EventRecord item, int position) {
            mEventRecordList.add(position,item);
            notifyItemInserted(position);
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mItemTitle.setText(mEventRecordList.get(position).getEventTitle());
            holder.mItemTime.setText("暂时还没做时间信息");
            holder.mItemLocation.setText(mEventRecordList.get(position).getEventLocation());
            holder.mItemDate.setText(df.format(mEventRecordList.get(position).getEventDate()));
//            holder.mItemOvalText;
            holder.mItemStatusText.setText("课程表");
        }

        @Override
        public int getItemCount() {
            return mEventRecordList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView mItemTitle;
            TextView mItemTime;
            TextView mItemLocation;
            TextView mItemDate;
            ImageView mItemOvalText;
            TextView mItemStatusText;

            public MyViewHolder(View view){
                super(view);
                mItemTitle = (TextView) view.findViewById(R.id.item_event_title);
                mItemTime = (TextView) view.findViewById(R.id.item_event_time);
                mItemLocation = (TextView) view.findViewById(R.id.item_event_location);
                mItemDate = (TextView) view.findViewById(R.id.item_event_date);
                mItemOvalText = (ImageView) view.findViewById(R.id.item_status_oval);
                mItemStatusText = (TextView) view.findViewById(R.id.item_status_text);
            }
        }
    }

    //将周次信息转成日期信息
    //输入参数：起始日期、周次、星期几、用于转换的日期格式
    public String calculateDateByWeeks(Date startDate, int weeks, int days, SimpleDateFormat sdf){
        long intervalSeconds = ((weeks-1)*7+(days-1))*1000l*60l*60l*24l;
        return sdf.format(new Date(startDate.getTime()+intervalSeconds));
    }
    //将日期信息转成周次信息
    //输入参数：起始日期、需要转换的日期、用户转换的日期格式
    public String calculateWeeksByDate(Date startDate, Date date, SimpleDateFormat sdf){
        long intervalDays = (date.getTime()-startDate.getTime())/(1000*60*60*24);
        long intervalWeeks = intervalDays/7+1;
        return intervalWeeks+"-"+(intervalDays%7+1);
    }
}
package com.example.xwn.calendarmanager;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.xwn.calendarmanager.util.CalendarUtil;
import com.example.xwn.calendarmanager.util.DensityUtil;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SimpleDateFormat df;
    private Date startDate;

    private EditText eventTitleEdit;
    private EditText eventLocationEdit;
    private Button saveEvent;
    private AlertDialog classDialog;
    private ActionBar actionBar;

    private List<EventRecord> mEventRecordList;
    private RecyclerView itemRecycler;
    private MyAdapter myAdapter;
    private EditText eventTeacherEdit;
    private CheckBox checkBox;
    private GridLayout gridLayout;
    private List<CheckBox> checkBoxWeeksList;
    private List<CheckBox> checkBoxDaysList;
    private List<CheckBox> checkBoxTimesList;

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
                Log.d(TAG, CalendarUtil.queryAccountName(MainActivity.this,"简单日历"));
                //CalendarUtil.inputAccount(MainActivity.this,"简单日历","课程表","woshixwn@gmail.com");
                String calId = CalendarUtil.queryAccountName(MainActivity.this,"简单日历");
                if (!calId.equals("error")){
                    Log.d(TAG, calculateDateByWeeks(startDate,13,2,df).toString());
                    CalendarUtil.writeEvent(MainActivity.this,calId,"web信息框架","李卫东","东九楼 D206",new Date(1479744000000l),new Date(1479744000000l+1000l*60l*60l));
                };
            }
        });
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
        eventTeacherEdit = (EditText) classDialogView.findViewById(R.id.event_teacher_edit);
        checkBox = (CheckBox) classDialogView.findViewById(R.id.checkbox_direct);
        saveEvent = (Button) classDialogView.findViewById(R.id.save_event);
        gridLayout = (GridLayout) classDialogView.findViewById(R.id.checkbox_grid);
        addCheckBoxs();
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Date> dateCreateByWeeks = calculateDateList(startDate,checkBoxWeeksList,checkBoxDaysList,df);
                List<TimeLast> timeLastList = calculateTimeLastList(checkBoxTimesList);
                Log.d(TAG, "产生的日历数目是:"+dateCreateByWeeks.size());
                List<EventRecord> eventRecordList = new ArrayList<>();
                for (int i= 0;i<dateCreateByWeeks.size();i++){
                    for (int j=0;j<timeLastList.size();j++) {
                        eventRecordList.add(new EventRecord(eventTitleEdit.getText().toString(), dateCreateByWeeks.get(i),
                                eventLocationEdit.getText().toString(), "上课老师是：" + eventTeacherEdit.getText().toString(),timeLastList.get(j)));
                    }
                }
                if (!checkBox.isChecked()) {
                    Log.d(TAG, "这部分还没写");
                }else{
                    String calId = CalendarUtil.queryAccountName(MainActivity.this,"简单日历");

                    if (!calId.equals("error")){
                        for (int m = 0;m<eventRecordList.size();m++) {
                            Log.d(TAG, "插入了:"+eventRecordList.get(m).toString());
                            CalendarUtil.writeEvent(MainActivity.this, calId, eventRecordList.get(m));
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"无账户将为你添加一个",Toast.LENGTH_SHORT).show();
                        calId = CalendarUtil.inputAccount(MainActivity.this,"简单日历","课程表","this is account");
                        for (int m = 0;m<eventRecordList.size();m++) {
                            CalendarUtil.writeEvent(MainActivity.this, calId, eventRecordList.get(m));
                        }
                    }
                }
                classDialog.dismiss();
            }
        });
        //initNumberPicker();
        classDialogBuilder.setView(classDialogView);
        classDialog = classDialogBuilder.create();
    }

    private List<TimeLast> calculateTimeLastList(List<CheckBox> checkBoxTimesList) {
        List<TimeLast> list = new ArrayList<>();
        for (int i=0;i<checkBoxTimesList.size();i++) {
            if (checkBoxTimesList.get(i).isChecked()) {
                switch (i){
                    case 0:
                        list.add(new TimeLast(8l*60l*60l*1000l,100l*60l*1000l));
                        break;
                    case 1:
                        list.add(new TimeLast((10*60l+10l)*60l*1000l,100l*60l*1000l));
                        break;
                    case 2:
                        list.add(new TimeLast((14l*60l+0l)*60l*1000l,95l*60l*1000l));
                        break;
                    case 3:
                        list.add(new TimeLast((15l*60l+55l)*60l*1000l,95l*60l*1000l));
                        break;
                    case 4:
                        list.add(new TimeLast((18l*60l+30l)*60l*1000l,95l*60l*1000l));
                        break;
                    case 5:
                        list.add(new TimeLast((20l*60l+15l)*60l*1000l,95l*60l*1000l));
                        break;
                }
            }
        }
        Log.d(TAG, "时刻列表里面有: ");
        for (int m=0;m<list.size();m++){
            Log.d(TAG, ""+list.get(m).toString());
        }
        return list;
    }

    private List<Date> calculateDateList(Date startDate, List<CheckBox> checkBoxWeeksList, List<CheckBox> checkBoxDaysList, SimpleDateFormat df) {
        List<Date> list = new ArrayList<>();
        for (int i = 0;i<checkBoxWeeksList.size();i++){
            Log.d(TAG, i+"Weeks: "+checkBoxWeeksList.get(i).isChecked());
            if (!checkBoxWeeksList.get(i).isChecked()) continue;
            for (int j = 0;j<checkBoxDaysList.size();j++){
                Log.d(TAG, j+"Days: "+checkBoxDaysList.get(j).isChecked());
                if (checkBoxDaysList.get(j).isChecked()){
                    list.add(calculateDateByWeeks(startDate,i+1,j+1,df));
                }
            }
        }
        return list;
    }

    private void addCheckBoxs() {
        checkBoxWeeksList = new ArrayList<>();
        checkBoxDaysList = new ArrayList<>();
        checkBoxTimesList = new ArrayList<>();
        String args[] = new String[]{"1-2","3-4","5-6","7-8","9-10","11-12"};
        for (int i=1;i<=21;i++) {
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.one_check_box, null);
            GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.WRAP_CONTENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.height = DensityUtil.dip2px(this, 42.0f);
            params.width = DensityUtil.dip2px(this, 42.0f);
            checkBox.setLayoutParams(params);
            checkBox.setText(i+"");
            checkBoxWeeksList.add(checkBox);
            gridLayout.addView(checkBox);
        }
        for (int j=1;j<=7;j++){
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.one_check_box, null);
            GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.WRAP_CONTENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.height = DensityUtil.dip2px(this, 42.0f);
            params.width = DensityUtil.dip2px(this, 42.0f);
            checkBox.setBackground(getDrawable(R.drawable.check_box_days));
            checkBox.setLayoutParams(params);
            checkBox.setText(j+"");
            checkBoxDaysList.add(checkBox);
            gridLayout.addView(checkBox);
        }
        for (int k=1;k<=6;k++){
            CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.one_check_box, null);
            GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.WRAP_CONTENT, GridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.height = DensityUtil.dip2px(this, 42.0f);
            params.width = DensityUtil.dip2px(this, 42.0f);
            checkBox.setBackground(getDrawable(R.drawable.check_box_times));
            checkBox.setLayoutParams(params);
            checkBox.setText(args[k-1]);
            checkBox.setTextSize(12);
            checkBoxTimesList.add(checkBox);
            gridLayout.addView(checkBox);
        }
        Log.d(TAG, "addCheckBoxs: "+ checkBoxWeeksList.size());
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
    public Date calculateDateByWeeks(Date startDate, int weeks, int days, SimpleDateFormat sdf){
        long intervalSeconds = ((weeks-1)*7+(days-1))*1000l*60l*60l*24l;
        Date date = new Date(startDate.getTime()+intervalSeconds);
        Log.d(TAG, "calculateDateByWeeks: "+date.getTime());
//        return sdf.format(date);
        return date;
    }
    //将日期信息转成周次信息
    //输入参数：起始日期、需要转换的日期、用户转换的日期格式
    public String calculateWeeksByDate(Date startDate, Date date, SimpleDateFormat sdf){
        long intervalDays = (date.getTime()-startDate.getTime())/(1000*60*60*24);
        long intervalWeeks = intervalDays/7+1;
        return intervalWeeks+"-"+(intervalDays%7+1);
    }
}
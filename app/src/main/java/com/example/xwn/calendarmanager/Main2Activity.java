package com.example.xwn.calendarmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xwn.calendarmanager.util.CalendarUtil;
import com.example.xwn.calendarmanager.util.DensityUtil;
import com.example.xwn.calendarmanager.util.OtherUtil;
import com.example.xwn.calendarmanager.widget.FabTagLayout;
import com.example.xwn.calendarmanager.widget.FloatingActionButtonPlus;
import com.iflytek.cloud.SpeechUnderstander;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Main2Activity";
    private FloatingActionButtonPlus floatingActionButtonPlus;
    private RecyclerView itemRecycler;
    private EditText eventTitleEdit;
    private EditText eventLocationEdit;
    private EditText eventTeacherEdit;
    private CheckBox checkBox;
    private Button saveEvent;
    private GridLayout gridLayout;
    private MyAdapter myAdapter;
    private List<CheckBox> checkBoxTimesList;
    private List<CheckBox> checkBoxDaysList;
    private List<CheckBox> checkBoxWeeksList;
    private List<ClassRecord> mEventRecordList;
    private Date startDate;
    private SimpleDateFormat df;
    private AlertDialog classDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEventRecordList = new ArrayList<>();

        //默认的日期格式、
        df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = df.parse("2016-08-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initDialog();
        initRecycleView();
        //初始化高级floatActionButton
        floatingActionButtonPlus= (FloatingActionButtonPlus) findViewById(R.id.FabPlus);
        floatingActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener(){

            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                switch (position){
                    case 0:
                        Toast.makeText(Main2Activity.this,"点击了没事找事功能",Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(Main2Activity.this,"点击了添加活动功能",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(Main2Activity.this,"点击了添加课表功能",Toast.LENGTH_LONG).show();
                        classDialog.show();
                        break;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //初始化RecycleView
    private void initRecycleView() {
        itemRecycler = (RecyclerView) findViewById(R.id.item_recycler);
        itemRecycler.setLayoutManager(new LinearLayoutManager(this));
        itemRecycler.setAdapter(myAdapter = new MyAdapter(Main2Activity.this,mEventRecordList));
    }
    //为RecycleView声明的内部类
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        List<ClassRecord> mEventRecordList;
        Context context;
        public MyAdapter(Context context,List<ClassRecord> mEventRecordList){
            this.context = context;
            this.mEventRecordList = mEventRecordList;
        }
        public void add(ClassRecord item, int position) {
            mEventRecordList.add(position,item);
            notifyItemInserted(position);
        }
        public List<ClassRecord> returnList(){
            return mEventRecordList;
        }
        public void clearList(){
            int n = mEventRecordList.size();
            mEventRecordList.clear();
            for (int i = 0;i<n;i++) {
                notifyItemRemoved(i);
            }
        }
        public void remove(int position){
            mEventRecordList.remove(position);
            notifyItemRemoved(position);
        }
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyAdapter.MyViewHolder holder = new MyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            holder.mItemTitle.setText(mEventRecordList.get(position).getClassTitle());
            holder.mItemLocation.setText(mEventRecordList.get(position).getClassLocation());
            List<Integer> tempList = mEventRecordList.get(position).getWeekNumber();

            if (tempList.size()<=2) holder.mItemWeek.setText("第" + mEventRecordList.get(position).getWeekNumber() + "周");
            else holder.mItemWeek.setText("第"+OtherUtil.integerListToString(mEventRecordList.get(position).getWeekNumber())+"周");

            String temp = mEventRecordList.get(position).getDayNumber().toString();
            holder.mItemDays.setText("礼拜"+temp.substring(1,temp.length()-1).replace(',','、')+"");

            temp = mEventRecordList.get(position).getClassTime().toString();
            holder.mItemTime.setText("第"+temp.substring(1,temp.length()-1).replace(',','、')+"节");
            holder.mItemTeacher.setText(mEventRecordList.get(position).getClassTeacher());
            holder.mItemStatusText.setText("课程表");

        }

        @Override
        public int getItemCount() {
            return mEventRecordList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            TextView mItemTitle;
            TextView mItemLocation;
            TextView mItemWeek;
            TextView mItemTime;
            ImageView mItemOvalText;
            TextView mItemStatusText;
            TextView mItemTeacher;
            TextView mItemDays;

            public MyViewHolder(View view){
                super(view);
                mItemTitle = (TextView) view.findViewById(R.id.item_class_title);
                mItemLocation = (TextView) view.findViewById(R.id.item_class_location);
                mItemWeek = (TextView) view.findViewById(R.id.item_class_week);
                mItemDays = (TextView) view.findViewById(R.id.item_class_day);
                mItemTime = (TextView) view.findViewById(R.id.item_class_time);
                mItemTeacher = (TextView) view.findViewById(R.id.item_class_teacher);
                mItemOvalText = (ImageView) view.findViewById(R.id.item_status_oval);
                mItemStatusText = (TextView) view.findViewById(R.id.item_status_text);
            }
        }
    }
    //初始化对话框
    private void initDialog() {
        //初始化添加事件对话框
        AlertDialog.Builder classDialogBuilder = new AlertDialog.Builder(Main2Activity.this);
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
                    ClassRecord classRecord = new ClassRecord();
                    classRecord.setClassTitle(eventTitleEdit.getText().toString());
                    classRecord.setClassLocation(eventLocationEdit.getText().toString());
                    classRecord.setClassTeacher(eventTeacherEdit.getText().toString());
                    List<Integer> listWeek = new ArrayList<Integer>();
                    for (int i=0;i<checkBoxWeeksList.size();i++) {
                        if (checkBoxWeeksList.get(i).isChecked()){
                            listWeek.add(i+1);
                        }
                    }
                    classRecord.setWeekNumber(listWeek);
                    List<Integer> listDay = new ArrayList<Integer>();
                    for (int i=0;i<checkBoxDaysList.size();i++){
                        if (checkBoxDaysList.get(i).isChecked()){
                            listDay.add(i+1);
                        }
                    }
                    classRecord.setDayNumber(listDay);
                    List<Integer> listTime = new ArrayList<>();
                    for (int i=0;i<checkBoxTimesList.size();i++){
                        if(checkBoxTimesList.get(i).isChecked()){
                            listTime.add(i+1);
                        }
                    }
                    classRecord.setClassTime(listTime);
                    myAdapter.add(classRecord,0);
                    Log.d(TAG, "这部分还没写"+classRecord);
                }else{
                    String calId = CalendarUtil.queryAccountName(Main2Activity.this,"简单日历");

                    if (calId.equals("error")){
                        Toast.makeText(Main2Activity.this,"无账户将为你添加一个",Toast.LENGTH_SHORT).show();
                        calId = CalendarUtil.inputAccount(Main2Activity.this,"简单日历","课程表","this is account");
                    }
                    for (int m = 0;m<eventRecordList.size();m++) {
                        Log.d(TAG, "插入了:"+eventRecordList.get(m).toString());
                        CalendarUtil.writeEvent(Main2Activity.this, calId, eventRecordList.get(m));
                    }
                }
                classDialog.dismiss();
            }
        });
        //initNumberPicker();
        classDialogBuilder.setView(classDialogView);
        classDialog = classDialogBuilder.create();
    }

    //通过时间checkBox计算开始时间和持续时间
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
    //通过checkBox计算Date
    private List<Date> calculateDateList(Date startDate, List<CheckBox> checkBoxWeeksList, List<CheckBox> checkBoxDaysList, SimpleDateFormat df) {
        List<Date> list = new ArrayList<>();
        for (int i = 0;i<checkBoxWeeksList.size();i++){
            Log.d(TAG, i+"Weeks: "+checkBoxWeeksList.get(i).isChecked());
            if (!checkBoxWeeksList.get(i).isChecked()) continue;
            for (int j = 0;j<checkBoxDaysList.size();j++){
                Log.d(TAG, j+"Days: "+checkBoxDaysList.get(j).isChecked());
                if (checkBoxDaysList.get(j).isChecked()){
                    list.add(OtherUtil.calculateDateByWeeks(startDate,i+1,j+1,df));
                }
            }
        }
        return list;
    }
    //用代码添加checkboxUI控件，一个个添加太累了
    private void addCheckBoxs() {
        //决定周次的checkBox，一共21个
        checkBoxWeeksList = new ArrayList<>();
        //决定星期几的checkBox，一共7个
        checkBoxDaysList = new ArrayList<>();
        //决定第几节课的checkBox，一共6个
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

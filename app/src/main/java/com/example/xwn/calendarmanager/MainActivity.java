package com.example.xwn.calendarmanager;

import android.content.Context;
import android.os.Environment;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.xwn.calendarmanager.util.CalendarUtil;
import com.example.xwn.calendarmanager.util.DensityUtil;
import com.example.xwn.calendarmanager.util.OtherUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.UnderstanderResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SimpleDateFormat df;
    private Date startDate;

    private EditText eventTitleEdit;
    private EditText eventLocationEdit;
    private Button saveEvent;
    private AlertDialog classDialog;
    private ActionBar actionBar;

    private List<ClassRecord> mEventRecordList;
    private RecyclerView itemRecycler;
    private MyAdapter myAdapter;
    private EditText eventTeacherEdit;
    private CheckBox checkBox;
    private GridLayout gridLayout;
    private List<CheckBox> checkBoxWeeksList;
    private List<CheckBox> checkBoxDaysList;
    private List<CheckBox> checkBoxTimesList;
    private int batchFlag;
    //语音语义识别
    private SpeechUnderstander mSpeechUnderstander;
    //语音监听器初始化
    private InitListener mSpeechInitListener=new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG,"speechUnderstandListener init() code="+code);
            if(code != ErrorCode.SUCCESS){
                Log.d(TAG,"初始化失败"+code);
            }
        }
    };
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                Log.d(TAG, result.getResultString());

                // 显示,关键就在这
                String text = result.getResultString();
            } else {
                Log.d(TAG, "识别结果不正确。");
            }
            // 读取json结果中的各种字段
            try {
                JSONObject resultJson = new JSONObject(result.getResultString());
                String text = resultJson.optString("text");
                Log.d(TAG, text);
                //应答码
                int rc = resultJson.optInt("rc");
                //服务类型，这里是提醒schedule
                String service = resultJson.optString("service");
                Log.d(TAG, service);
                //操作 这里是create 建立
                String operation = resultJson.optString("operation");
                Log.d(TAG, operation);
                //解析semantic Json 语义结构化表示
                String semantic = resultJson.optString("semantic");
                Log.d(TAG, semantic);
                JSONObject semanticJSon = new JSONObject(semantic);
                //slots
                String slots = semanticJSon.optString("slots");
                Log.d(TAG, slots);
                JSONObject slotsJSon = new JSONObject(slots);
                //提醒内容content
                String content = slotsJSon.optString("content");
                Log.d(TAG, content);

                String name = slotsJSon.optString("name");
                Log.d(TAG, name);

                String datetime = slotsJSon.optString("datetime");
                Log.d(TAG, datetime);
                JSONObject datetimeJSon = new JSONObject(datetime);

                String date = datetimeJSon.optString("date");
                Log.d(TAG, date);

                String dataOrig = datetimeJSon.optString("dateOrig");
                Log.d(TAG, dataOrig);

                String time = datetimeJSon.optString("time");
                Log.d(TAG, time);

                String timeOrig = datetimeJSon.optString("timeOrig");
                Log.d(TAG, timeOrig);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG,"当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length+"");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d(TAG,"结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG,"开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            Log.d(TAG,error.getPlainDescription(true));
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };
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
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(MainActivity.this,mSpeechInitListener);
        try {
            startDate = df.parse("2016-08-29");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initInsert() {
        Button jumpCenter = (Button) findViewById(R.id.jump_center);

        jumpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            case R.id.menu_add_to_calendar:
                addListItemToCalendar();
                break;
            case R.id.menu_add_to_cloud:
                List<BmobObject> list = new ArrayList<>();
                List<ClassRecord> listCR = myAdapter.returnList();
                for (int i=0;i<listCR.size();i++){
                    Log.d(TAG, "onOptionsItemSelected: "+listCR.size());
                    list.add(listCR.get(i).setImportantKey("597939931"));
                }
                Log.d(TAG, "list的长度:"+list.size());
                BmobBatch bmobBatch = new BmobBatch();
                //我在这里立下这个用来标记有些数据在批量添加的时候失败了
                batchFlag = 0;
                bmobBatch.insertBatch(list).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if(e==null){
                            for(int i=0;i<list.size();i++){
                                BatchResult result = list.get(i);
                                BmobException ex =result.getError();
                                if(ex==null){
                                    Log.d(TAG,"第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                    myAdapter.remove(batchFlag);
                                }else{
                                    Log.d(TAG,"第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                    batchFlag = i;
                                }
                            }
                        }else{
                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                break;
            case R.id.menu_voice:
                tryInitWithVoice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tryInitWithVoice() {
        setParam();

        if(mSpeechUnderstander.isUnderstanding()){
            mSpeechUnderstander.stopUnderstanding();
            Log.d(TAG,"停止录音");
        }else {
            int flag=mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
            if(flag!= ErrorCode.SUCCESS){
                Log.d(TAG,"语义解码失败"+flag);
            }else{
                Log.d(TAG,"请开始说话");
            }
        }
    }

    private void addListItemToCalendar() {
        String calId = CalendarUtil.queryAccountName(MainActivity.this,"简单日历");

        if (calId.equals("error")){
            Toast.makeText(MainActivity.this,"无账户将为你添加一个",Toast.LENGTH_SHORT).show();
            calId = CalendarUtil.inputAccount(MainActivity.this,"简单日历","课程表","this is account");
        }

        List<ClassRecord> list = myAdapter.returnList();
        Log.d(TAG, "addListItemToCalendar: "+calId+"\n"+list.size());
        for (int i=0;i<list.size();i++) {
            CalendarUtil.writeEvent(MainActivity.this, calId,list.get(i),startDate,df);
        }
        myAdapter.clearList();
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
                    String calId = CalendarUtil.queryAccountName(MainActivity.this,"简单日历");

                    if (calId.equals("error")){
                        Toast.makeText(MainActivity.this,"无账户将为你添加一个",Toast.LENGTH_SHORT).show();
                        calId = CalendarUtil.inputAccount(MainActivity.this,"简单日历","课程表","this is account");
                    }
                    for (int m = 0;m<eventRecordList.size();m++) {
                        Log.d(TAG, "插入了:"+eventRecordList.get(m).toString());
                        CalendarUtil.writeEvent(MainActivity.this, calId, eventRecordList.get(m));
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
        checkBoxWeeksList = new ArrayList<>();  //决定周次的checkBox，一共21个
        checkBoxDaysList = new ArrayList<>();   //决定星期几的checkBox，一共7个
        checkBoxTimesList = new ArrayList<>();  //决定第几节课的checkBox，一共6个
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
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
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
    //设置那些解析
    private void setParam(){
        // 清空参数
        mSpeechUnderstander.setParameter(SpeechConstant.PARAMS, null);


        // 设置返回结果格式
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");

        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");


        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }
}
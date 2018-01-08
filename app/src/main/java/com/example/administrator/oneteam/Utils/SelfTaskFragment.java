package com.example.administrator.oneteam.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.oneteam.Factory.ServiceFactory;
import com.example.administrator.oneteam.LoginActivity;
import com.example.administrator.oneteam.Main;
import com.example.administrator.oneteam.R;
import com.example.administrator.oneteam.Service.BrunoService;
import com.example.administrator.oneteam.add_task;
import com.example.administrator.oneteam.expense_detail;
import com.example.administrator.oneteam.model.Outcome;
import com.example.administrator.oneteam.model.Task;
import com.example.administrator.oneteam.task_detail;
import com.example.administrator.oneteam.tools.CommonAdapter;
import com.example.administrator.oneteam.tools.OneTeamCalendar;
import com.example.administrator.oneteam.tools.ViewHolder;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.FunGameBattleCityHeader;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.header.WaveSwipeHeader;
import com.scwang.smartrefresh.header.fungame.FunGameHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.scwang.smartrefresh.layout.impl.RefreshFooterWrapper;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.LogRecord;
import com.example.administrator.oneteam.tools.GreetingText;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by D105-01 on 2017/12/24.
 */

public class SelfTaskFragment extends Fragment {

    private RecyclerView self_task_rv,expense_rv;
    private View view;
    private int position;
    private CommonAdapter<Task> commonAdapter;
    private List<Task>[] array;
    private List<Task> datalist,datalist1,datalist2;
    private SmartRefreshLayout refresh;
    private int [] all_star = new int[]{R.id.item_star1,R.id.item_star2,R.id.item_star3,R.id.item_star4,R.id.item_star5};
    private static final int UPDATE_GREETING_TEXT = 1;
    private GreetingText gt_greetingText;
    private LinearLayout window;
    private TextView choose,choose1,choose2,choose3;
    private String colorbule = "#3060f0";
    private OneTeamCalendar self_calender;

    int offset[]={0,0,0};
    @SuppressLint("HandlerLeak")
    //region 线程处理函数 Handler
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int tmp = msg.what/100;
            int po=msg.what-tmp*100;
            switch (tmp){
                case 33:ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                        .create(BrunoService.class)
                        .done_task(String.valueOf(datalist.get(po).task_id))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Outcome>(){
                            @Override
                            public void onCompleted() {
                                offset[position]=0;
                            }
                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onNext(Outcome outcome) {

                            }
                        });
                    datalist.remove(po);
                    commonAdapter.notifyDataSetChanged();
                    break;
                case 34:
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .undone_task(String.valueOf(datalist.get(po).task_id))
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Outcome>(){
                                @Override
                                public void onCompleted() {
                                    offset[position]=0;
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(Outcome outcome) {

                                }
                            });
                    datalist.remove(po);
                    commonAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };
    //endregion

    /**
     * 用于在main中实例化SelfTaskFragment
     * @return
     */

    public static SelfTaskFragment newInstance(){
        return new SelfTaskFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.self_task_fragment, null);
//        gt_greetingText =  view.findViewById(R.id.gt_self_task_fragment);
//        setUpGreetingText();
        position=0;
        datalist = new ArrayList<>();
        datalist1 = new ArrayList<>();datalist2 = new ArrayList<>();
        array = new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
        init_recyclerview();
        init_reflashview();
        init_window();

        return view;
    }

    private void init_window() {
        self_calender = view.findViewById(R.id.self_calender);
        choose =view.findViewById(R.id.self_choose);
        choose1 = view.findViewById(R.id.choose1);
        choose2 = view.findViewById(R.id.choose2);
        choose3 = view.findViewById(R.id.choose3);
        window =  view.findViewById(R.id.window);

        choose.setTag(0);
        choose.setTextColor(Color.parseColor("#000000"));

        window.setVisibility(View.INVISIBLE);
        self_calender.setVisibility(View.GONE);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose.getTag().equals(1)){
                    window.setVisibility(View.INVISIBLE);
                    choose.setTag(0);
                    choose.setTextColor(Color.parseColor("#000000"));
                    self_calender.setVisibility(View.GONE);
                }
                else{
                    choose.setTag(1);
                    window.setVisibility(View.VISIBLE);
                    choose.setTextColor(Color.parseColor(colorbule));
                    self_calender.setVisibility(View.VISIBLE);
                }
            }
        });
        window.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                self_calender.setVisibility(View.GONE);
                window.setVisibility(View.INVISIBLE);
                choose.setTag(0);
                choose.setTextColor(Color.parseColor("#000000"));

            }
        });
        choose1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                self_calender.setVisibility(View.GONE);
                window.setVisibility(View.INVISIBLE);
                choose1.setTextColor(Color.parseColor(colorbule));
                choose.setText(choose1.getText().toString());
                choose2.setTextColor(Color.parseColor("#000000"));
                choose3.setTextColor(Color.parseColor("#000000"));
                choose.setTag(0);
                choose.setTextColor(Color.parseColor("#000000"));
                position=0;
                change_datalist();
                commonAdapter.notifyDataSetChanged();
            }
        });
        choose2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                self_calender.setVisibility(View.GONE);
                window.setVisibility(View.INVISIBLE);
                choose2.setTextColor(Color.parseColor(colorbule));
                choose.setText(choose2.getText().toString());
                choose1.setTextColor(Color.parseColor("#000000"));
                choose3.setTextColor(Color.parseColor("#000000"));
                choose.setTag(0);
                choose.setTextColor(Color.parseColor("#000000"));
                position=1;
                change_datalist();
                commonAdapter.notifyDataSetChanged();
            }
        });
        choose3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                self_calender.setVisibility(View.GONE);
                window.setVisibility(View.INVISIBLE);
                choose3.setTextColor(Color.parseColor(colorbule));
                choose.setText(choose3.getText().toString());
                choose1.setTextColor(Color.parseColor("#000000"));
                choose2.setTextColor(Color.parseColor("#000000"));
                choose.setTag(0);
                choose.setTextColor(Color.parseColor("#000000"));
                position=2;
                change_datalist();
                commonAdapter.notifyDataSetChanged();
            }
        });
    }

    private void init_reflashview() {
        refresh = view.findViewById(R.id.reflash);
        refresh.setRefreshHeader(new WaveSwipeHeader(getActivity()));//WaveSwipeHeader
        refresh.setEnableLoadmoreWhenContentNotFull(true);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(position==0){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks_done(3,0)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {
                                    offset[position]=0;
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;array[position].size()!=0;++i){
                                        array[position].remove(0);
                                    }
                                    for(int i=0;i< outcome.size();++i){
                                        array[position].add(outcome.get(i));
                                    }
                                   change_datalist();
                                }
                            });
                }
                else if(position==1){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks_undone(3,0)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {
                                    offset[position]=0;
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;array[position].size()!=0;++i) array[position].remove(0);
                                    for(int i=0;i< outcome.size();++i){
                                        array[position].add(outcome.get(i));
                                    }
                                    change_datalist();
                                }
                            });
                }
                else if(position==2){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks(3,0)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {
                                    offset[position]=0;
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;array[position].size()!=0;++i){
                                        array[position].remove(0);
                                    }
                                    for(int i=0;i< outcome.size();++i){
                                        Log.e("3333",String.valueOf(outcome.size()));
                                        array[position].add(outcome.get(i));
                                    }
                                    change_datalist();
                                    Log.e("3333444",String.valueOf(array[position].size()));
                                }
                            });
                }
                refreshlayout.finishRefresh();
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                offset[position]+=3;
                if(position==0){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks_done(3,offset[position])
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {

                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;i< outcome.size();++i){
                                        array[position].add(outcome.get(i));
                                    }
                                   change_datalist();
                                }
                            });
                }
                else if(position==1){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks_undone(3,offset[position])
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {

                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;i< outcome.size();++i){
                                        array[position].add(outcome.get(i));
                                    }
                                    change_datalist();
                                }
                            });
                }
               else if(position==2){
                    ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                            .create(BrunoService.class)
                            .getTasks(3,offset[position])
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Task>>(){
                                @Override
                                public void onCompleted() {

                                }
                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNext(List<Task> outcome) {
                                    for(int i=0;i< outcome.size();++i){
                                        array[position].add(outcome.get(i));
                                    }
                                   change_datalist();
                                }
                            });
                }
                refresh.finishLoadmore();
            }
        });

    }

    private void change_datalist() {
        while(datalist.size()!=0){
            datalist.remove(0);
        }
        for(int i=0;i<array[position].size();++i){
            datalist.add(array[position].get(i));
        }
        Log.e("1234",String.valueOf(datalist.size()));
        commonAdapter.notifyDataSetChanged();
    }

    private void init_recyclerview() {
        self_task_rv = view.findViewById(R.id.self_task_recyclerview);
        commonAdapter = new CommonAdapter<Task>(getActivity(),R.layout.task_recyclerview_item,datalist) {
            @Override
            public void convert(final ViewHolder holder, Task task) {
                final TextView title=holder.getView(R.id.item_title);
                final TextView ddl = holder.getView(R.id.itme_ddl);
                final String font = title.getFontFeatureSettings();
                final Button  checkbox = holder.getView(R.id.item_checkbox);
                ImageView star ;
                ddl.setText("DDL:"+ task.task_deadline);
                for (int i=task.task_mark;i<5;++i){
                    star=holder.getView(all_star[i]);
                    star.setVisibility(View.INVISIBLE);
                }
                checkbox.setTag(0);
                if(task.task_state.equals("undone")){
                    checkbox.setBackgroundResource(R.drawable.uncheck);
                    checkbox.setTag(0);
                }
                else{
                    checkbox.setBackgroundResource(R.drawable.check);
                    checkbox.setTag(1);
                }
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkbox.getTag().equals(1)){
                            title.setText(title.getText());
                            checkbox.setBackgroundResource(R.drawable.uncheck);
                            checkbox.setTag(0);
                            handler.sendEmptyMessageDelayed(34*100+holder.getAdapterPosition(),1000);
                        }else{
                            title.setText(title.getText());
                            checkbox.setTag(1);
                            checkbox.setBackgroundResource(R.drawable.check);
                            handler.sendEmptyMessageDelayed(33*100+holder.getAdapterPosition(),1000);
                        }
                    }
                });
                title.setText(task.task_name);
            }
        };
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), task_detail.class);
                intent.putExtra("id",String.valueOf(datalist.get(position).task_id));
                startActivity(intent);
            }
            @Override
            public void onLongClick(int position) {
            }
        });
        ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                .create(BrunoService.class)
                .getTasks_done(3,0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Task>>(){
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(List<Task> outcome) {
                        for(int i=0;i< outcome.size();++i){
                            array[position].add(outcome.get(i));
                        }
                        change_datalist();
                        commonAdapter.notifyDataSetChanged();
                    }
                });
        self_task_rv.setAdapter(commonAdapter);
        self_task_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        commonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    
    /**
     * 子线程更新GreetingText
     * 回调函数是Handler
     */
    private void setUpGreetingText(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = UPDATE_GREETING_TEXT;
                handler.sendMessage(msg);
                //每隔十分钟执行一次GreetingText的更新
                handler.postDelayed(this, 1000*60*10);
            }
        };
        runnable.run();
    }
}

package com.example.administrator.oneteam;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.oneteam.Factory.ServiceFactory;
import com.example.administrator.oneteam.Service.BrunoService;
import com.example.administrator.oneteam.model.Person;
import com.example.administrator.oneteam.model.Task;
import com.example.administrator.oneteam.tools.CommonAdapter;
import com.example.administrator.oneteam.tools.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class task_detail extends AppCompatActivity {
    private TextView name,presenter,newsdate,persons,rate,budget_rate,ddl,back,add_task,add_expense;
    private ImageView check_box,down_person,down_budget;
    private RecyclerView person_rv,budget_rv;
    private ConstraintLayout person_layout,budget_layout;
    private int [] all_star = new int[]{R.id.task_star1,R.id.task_star2,R.id.task_star3,R.id.task_star4,R.id.task_star5};
    private CommonAdapter<Map<String,String>> commonAdapter,commonAdapter1;
    private List<Map<String,String>> datalist,datalist1;
    String id;
    Task task;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           switch (msg.what){
               case 0:
                   ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                           .create(BrunoService.class)
                           .getUser(String.valueOf(task.task_presenter_id))
                           .subscribeOn(Schedulers.newThread())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(new Subscriber<Person>(){
                               @Override
                               public void onCompleted() {

                               }
                               @Override
                               public void onError(Throwable e) {
                                   Log.e("33",e.getMessage());
                                   Toast.makeText(task_detail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                               }
                               @Override
                               public void onNext(Person outcome){
                                   presenter.setText(outcome.name);
                               }
                           });
           }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        id = getIntent().getStringExtra("id");

        init_view();
        init_listener();
        ServiceFactory.getmRetrofit("http://172.18.92.176:3333")
                .create(BrunoService.class)
                .get_task(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Task>(){
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("343",e.getMessage());
                        Toast.makeText(task_detail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(Task outcome){
                        task = new Task();
                        task = outcome;
                        handler.sendEmptyMessage(0);
                        Toast.makeText(task_detail.this,"here",Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void init_listener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        person_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(person_rv.getVisibility()==View.GONE){
                    person_rv.setVisibility(View.VISIBLE);
                    down_person.setImageResource(R.drawable.up_gray);
                }
                else{
                    person_rv.setVisibility(View.GONE);
                    down_person.setImageResource(R.drawable.down_gray);
                }
            }
        });
        budget_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(budget_rv.getVisibility()==View.GONE){
                    budget_rv.setVisibility(View.VISIBLE);
                    down_budget.setImageResource(R.drawable.up_gray);
                }
                else{
                    budget_rv.setVisibility(View.GONE);
                    down_budget.setImageResource(R.drawable.down_gray);
                }
            }
        });
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),add_expense.class);
                startActivity(intent);
            }
        });
    }

    private void init_view() {
        name = (TextView)findViewById(R.id.task_title);
        presenter = (TextView)findViewById(R.id.task_presenter);
        newsdate = (TextView)findViewById(R.id.task_newstime);
        persons = (TextView)findViewById(R.id.task_person);
        rate = (TextView)findViewById(R.id.task_rate);
        budget_rate = (TextView)findViewById(R.id.task_budget);
        ddl = (TextView)findViewById(R.id.task_ddl);
        back = (TextView)findViewById(R.id.task_back);
        check_box = (ImageView)findViewById(R.id.task_checkbox);
        person_rv = (RecyclerView)findViewById(R.id.task_persons);
        budget_rv = (RecyclerView)findViewById(R.id.task_budgets);
        person_layout = (ConstraintLayout) findViewById(R.id.person_layout);
        budget_layout = (ConstraintLayout)findViewById(R.id.budget_layout);
        down_person = (ImageView)findViewById(R.id.task_down);
        down_budget = (ImageView)findViewById(R.id.task_budget_down);
        add_task = (TextView)findViewById(R.id.join_task);
        add_expense = (TextView)findViewById(R.id.add_enpense);
        person_rv.setVisibility(View.GONE);
        budget_rv.setVisibility(View.GONE);
        datalist = new ArrayList<>();
        datalist1 = new ArrayList<>();
        commonAdapter = new CommonAdapter<Map<String,String>>(this,R.layout.task_persons_item,datalist) {
            @Override
            public void convert(ViewHolder holder, Map<String,String> task) {
                final TextView title=holder.getView(R.id.user_name);
                final ImageView img = holder.getView(R.id.user_image);
                final TextView state = holder.getView(R.id.user_task_state);
                title.setText(task.get("name"));
                Glide.with(getApplication()).load("http://172.18.92.176:3333/"+task.get("url")).into(img);
                state.setText(task.get("state"));
            }
        };
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position) {
            }
            @Override
            public void onLongClick(int position) {
            }
        });
        commonAdapter1 = new CommonAdapter<Map<String,String>>(this,R.layout.task_persons_item,datalist1) {
            @Override
            public void convert(ViewHolder holder, Map<String,String> task) {
                final TextView title=holder.getView(R.id.user_name);
                final ImageView img = holder.getView(R.id.user_image);
                final TextView state = holder.getView(R.id.user_task_state);
                title.setText(task.get("name"));
                Glide.with(getApplication()).load("http://172.18.92.176:3333/"+task.get("url")).into(img);
                state.setText(task.get("state"));
            }
        };
        commonAdapter1.setOnItemClickListener(new CommonAdapter.OnItemClickListener(){
            @Override
            public void onClick(int position) {
            }
            @Override
            public void onLongClick(int position){
            }
        });
        Map<String,String> tmp;
        tmp = new HashMap<>();
        tmp.put("name","33");
        tmp.put("url","my.PNG");
        tmp.put("state","33/500 已报销");
        datalist1.add(tmp);
        tmp = new HashMap<>();
        tmp.put("name","3344");
        tmp.put("url","my1.PNG");
        tmp.put("state","65/500 已报销");
        datalist1.add(tmp);
        budget_rv.setAdapter(commonAdapter1);
        budget_rv.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter1.notifyDataSetChanged();
        tmp = new HashMap<>();
        tmp.put("name","33");
        tmp.put("url","my.PNG");
        tmp.put("state","未完成");
        datalist.add(tmp);
        tmp = new HashMap<>();
        tmp.put("name","3344");
        tmp.put("url","my1.PNG");
        tmp.put("state","未完成");
        datalist.add(tmp);
        person_rv.setAdapter(commonAdapter);
        person_rv.setLayoutManager(new LinearLayoutManager(this));
        commonAdapter.notifyDataSetChanged();
    }
}

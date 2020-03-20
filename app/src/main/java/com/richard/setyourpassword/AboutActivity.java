package com.richard.setyourpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initData();

        recyclerView = (RecyclerView) findViewById(R.id.updateHistoryRecyclerView);

        mAdapter = new ItemAdapter(AboutActivity.this);
        mAdapter.setDatas(mDatas);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        mDatas.add("1.2");
        mDatas.add("Added confirmation page before clearing data\n" +
                "Added about page\n" +
                "Added version update history (incomplete)");
        mDatas.add("1.1");
        mDatas.add("Added additional feature: set the maximum number of password attempts before clearing data\n" +
                "Added additional function: clear all data\n" +
                "Added \"Activate Device Admin\" checkbox");
        mDatas.add("1.0");
        mDatas.add("First version");
        /*
        for (int i = '1'; i <= '2'; i++) {
            mDatas.add("" + (char) i);
        }
         */
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tv;
            public ViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.id_num);
            }
        }
        private Context context;
        private List<String> datas;
        public ItemAdapter(Context context ){
            this.context = context;
        }

        public void setDatas(List<String> datas) {
            this.datas = datas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_update_history,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,int position) {
            holder.tv.setText(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }
}

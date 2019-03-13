package com.xrd.slidedeleteview;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xrd.slidedeleteview.weight.SlideDeleteView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SlideDeleteView targetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom=10;
            }
        });
        rv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ViewHolder holder1 = (ViewHolder) holder;
                holder1.llContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "内容", Toast.LENGTH_SHORT).show();
                    }
                });holder1.llMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "删除", Toast.LENGTH_SHORT).show();
                    }
                });
                holder1.sdv.setChangeStateListener(new SlideDeleteView.ISlideItemCallBack() {
                    @Override
                    public void slidOpen(SlideDeleteView itemView) {
                        targetView =itemView;
                    }

                    @Override
                    public void slidClose(SlideDeleteView itemView) {
                        if(targetView==itemView){
                            targetView=null;
                        }
                    }

                    @Override
                    public void onTouch(SlideDeleteView itemView) {
                        if(targetView!=null&&targetView!=itemView){
                            targetView.closeSlid2();
                        }
                    }
                });

            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llContent;
        private LinearLayout llMenu;
        private SlideDeleteView sdv;
        public ViewHolder(View itemView) {
            super(itemView);
            llContent=itemView.findViewById(R.id.ll_content);
            llMenu=itemView.findViewById(R.id.ll_delete);
            sdv=itemView.findViewById(R.id.sdv);
        }
    }
}

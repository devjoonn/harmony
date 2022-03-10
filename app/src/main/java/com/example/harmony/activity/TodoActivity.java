package com.example.harmony.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harmony.DBHelper;
import com.example.harmony.R;
import com.example.harmony.TodoItem;
import com.example.harmony.adapter.CustomAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TodoActivity extends AppCompatActivity {


    private RecyclerView mRv_todo;
    private Button mBtn_Write;
    private ArrayList<TodoItem> mTodoItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        setinit();
    }

    private void setinit() {
        mDBHelper = new DBHelper(this);
        mRv_todo = findViewById(R.id.rv_todo);
        mBtn_Write = findViewById(R.id.btn_write);
        mTodoItems = new ArrayList<>();



        loadRecentDB();



        mBtn_Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시글 작성을 위한 팝업창 띄우기
                Dialog dialog = new Dialog(TodoActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit);
                // dialog_edit 와 연결되었기 때문에 팝업창에서 작성한 값을 가져오기
                EditText edit_title =  dialog.findViewById(R.id.edit_title);
                EditText edit_content =  dialog.findViewById(R.id.edit_content);
                Button btn_OK = dialog.findViewById(R.id.btn_OK);

                btn_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // insert data
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 현재시간 년월일 시분초 가져오기
                        mDBHelper.InsertTodo(edit_title.getText().toString(),edit_content.getText().toString(), currentTime);

                        // Insert UI
                        TodoItem item = new TodoItem();
                        item.setTitle(edit_title.getText().toString());
                        item.setContent(edit_content.getText().toString());
                        item.setWriteDate(currentTime);

                        mAdapter.addItem(item); //아이템 추가
                        mRv_todo.scrollToPosition(0); // 데이터가 올라갈때마다 이쁘게 스크롤 올라가게만듬
                        dialog.dismiss();
                        Toast.makeText(TodoActivity.this, "할일 목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();

                    }
                });

                dialog.show();

            }
        });
    }

    private void loadRecentDB() {
        //저장되어있는 DB가져옴
        mTodoItems = mDBHelper.getTodoList();
        if (mAdapter == null) {
            mAdapter = new CustomAdapter(mTodoItems, this);
            mRv_todo.setHasFixedSize(true); //리사이클뷰 성능 강화
            mRv_todo.setAdapter(mAdapter);
        }


    }


}
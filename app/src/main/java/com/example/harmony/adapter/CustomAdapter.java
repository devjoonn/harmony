package com.example.harmony.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harmony.DBHelper;
import com.example.harmony.R;
import com.example.harmony.TodoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems;
    private Context mContext;
    private DBHelper mDBHelper;

    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext) {
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }


    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 아이템뷰 하나하나에 대한 뷰 연결을 여기서 해줌
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_content.setText(mTodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mTodoItems.get(position).getWriteDate());
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_writeDate;

         public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //현재 리스트 클릭한 아이템의 위치(0,1,2,3)
                    int curPos = getAdapterPosition();      //현재 아이템을 클릭한 위치
                    TodoItem todoItem = mTodoItems.get(curPos);

                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    //기본 안드로이드에서 제공하는 alert알림
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택 해주세요.");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface DialogInterface, int position) {
                            if(position == 0) {
                                // strChoiceItems 에서 0번째 = 수정하기
                                // 게시글 작성을 위한 팝업창 띄우기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                // dialog_edit 와 연결되었기 때문에 팝업창에서 작성한 값을 가져오기
                                EditText edit_title =  dialog.findViewById(R.id.edit_title);
                                EditText edit_content =  dialog.findViewById(R.id.edit_content);
                                Button btn_OK = dialog.findViewById(R.id.btn_OK);

                                edit_title.setText(todoItem.getTitle());        //수정하기를 눌렀을때 빈값이 아닌 입력했던 값 출력
                                edit_content.setText(todoItem.getContent());

                                edit_title.setSelection(edit_title.getText().toString().length() -1); // 저장된 값의 커서가 오른쪽 끝

                                btn_OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Update table
                                        String title = edit_title.getText().toString();
                                        String content = edit_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 수정하는 시점 년월일 시분초 가져오기
                                        String beforeTime = todoItem.getWriteDate(); // 클릭한 아이템의 등록 되어있던 시간

                                        mDBHelper.UpdateTodo(title,content,currentTime, beforeTime);

                                        //Update UI
                                        todoItem.setTitle(title);
                                        todoItem.setContent(content);
                                        todoItem.setWriteDate(currentTime);
                                        notifyItemChanged(curPos, todoItem); // curPos 현재 클릭한 아이템에서 todoItem 값으로 대체
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "목록 수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.show();

                            } else if (position == 1) {
                                //delete table
                                String beforeTime = todoItem.getWriteDate(); // 클릭한 아이템의 등록 되어있던 시간
                                mDBHelper.DeleteTodo(beforeTime);

                                //delete UI
                                mTodoItems.remove(curPos);
                                notifyItemRemoved(curPos);  // curPos 현재 클릭한 아이템 값을 제거
                                Toast.makeText(mContext, "목록이 제거 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.show();

                }
            });
        }
    }

    //액티비티에서 호출되는 함수 - 현재 어댑터에 새로운 게시글 아이템을 전달받아 추가하는 목적
    public void addItem(TodoItem _item) {
        mTodoItems.add(0, _item); //추가되는 아이템이 역순으로 위에서부터 정렬됨
        notifyItemInserted(0); // 새로고침

    }

}

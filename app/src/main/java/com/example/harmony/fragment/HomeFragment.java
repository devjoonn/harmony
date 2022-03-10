package com.example.harmony.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.harmony.R;
import com.example.harmony.activity.MyPageActivity;
import com.example.harmony.activity.TodoActivity;

public class HomeFragment extends Fragment {
    CardView todo, homepage, bus, information, calendar, library;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        view.findViewById(R.id.intent_todo).setOnClickListener(onClickListener);
        view.findViewById(R.id.intent_homepage).setOnClickListener(onClickListener);
        view.findViewById(R.id.intent_bus).setOnClickListener(onClickListener);
        view.findViewById(R.id.intent_information).setOnClickListener(onClickListener);
        view.findViewById(R.id.intent_calendar).setOnClickListener(onClickListener);
        view.findViewById(R.id.intent_library).setOnClickListener(onClickListener);


        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.intent_todo:
                    myStartActivity(TodoActivity.class);
                    break;
                case R.id.intent_homepage:
                    Intent hompage = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tw.ac.kr/main.do"));
                    startActivity(hompage);
                    break;
                case R.id.intent_bus:
                    Intent bus = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tw.ac.kr/contents/contents.do?ciIdx=39&menuId=864"));
                    startActivity(bus);
                    break;
                case R.id.intent_information:
                    Intent information = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tw.ac.kr/bbs/board.do?bsIdx=8&menuId=933"));
                    startActivity(information);
                    break;
                case R.id.intent_calendar:
                    Intent calendar = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.tw.ac.kr/academic/calendar.do"));
                    startActivity(calendar);
                    break;
                case R.id.intent_library:
                    Intent library = new Intent(Intent.ACTION_VIEW, Uri.parse("https://library.tw.ac.kr/"));
                    startActivity(library);
                    break;
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 1);
    }
}
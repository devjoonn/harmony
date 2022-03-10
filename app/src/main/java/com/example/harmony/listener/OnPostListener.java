package com.example.harmony.listener;

import com.example.harmony.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();
}

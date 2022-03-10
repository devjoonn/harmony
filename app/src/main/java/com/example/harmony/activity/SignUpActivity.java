package com.example.harmony.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.harmony.R;
import com.example.harmony.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private FirebaseAuth mAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private RelativeLayout loaderLayout;


    private EditText mEtEmail, mEtName, mEtPwd, mEtPwdCheck;

    private ImageView chatProfileImageView; //프로필 이미지 지정
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Harmony");


        mEtEmail = findViewById(R.id.et_email);
        mEtName = findViewById(R.id.et_name);
        mEtPwd = findViewById(R.id.et_password);
        mEtPwdCheck = findViewById(R.id.et_passwordCheck);
        loaderLayout = findViewById(R.id.loaderLayout);
        chatProfileImageView = findViewById(R.id.chatProfileImageView);



        findViewById(R.id.chatProfileImageView).setOnClickListener(onClickListener);
        findViewById(R.id.btn_register).setOnClickListener(onClickListener);



    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chatProfileImageView:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_FROM_ALBUM);
                    break;
                case R.id.btn_register:
                    signUp();

                    break;
            }
        }
    };


    private void signUp() {
        //회원가입 처리 시작
        String strEmail = mEtEmail.getText().toString();
        String strName = mEtName.getText().toString();
        String strPwd = mEtPwd.getText().toString();
        String strPwdCheck = mEtPwdCheck.getText().toString();
        ProgressBar mProgressbar = findViewById(R.id.progressBar);


        loaderLayout.setVisibility(View.VISIBLE);

        if(strEmail == null || strName == null  || strPwd == null || strPwdCheck == null || imageUri == null) {
            return;
        }

        // 비밀번호와 비밀번호 확인 비교
        if (strPwd.equals(strPwdCheck)) {
            // Firebase Auth 진행
            mAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //이름 0자이상, 폰번호 9자이상, 생년월일 5자이상, 학과명 10자이하
                    loaderLayout.setVisibility(View.GONE);
                    String uid = task.getResult().getUser().getUid();
                    FirebaseStorage.getInstance().getReference().child("usersChatProfile").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                            while (!((Task) imageUrl).isComplete()) ;

                            UserModel userModel = new UserModel();
                            userModel.userName = strName;
                            userModel.chatProfileImageUri = imageUrl.getResult().toString();
                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mDatabaseRef.child("users").child(uid).setValue(userModel);
                            Toast.makeText(SignUpActivity.this, "회원정보를 보내는데 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        } else {
            Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }



    //사진 프로필이미지뷰에 넘김
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            chatProfileImageView.setImageURI(data.getData());
            imageUri = data.getData();
            Glide.with(this).load(imageUri).centerCrop().override(400).into(chatProfileImageView);
        }
    }

    // 뒤로가기 방지
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

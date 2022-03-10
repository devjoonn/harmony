package com.example.harmony.activity;

import android.Manifest;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.harmony.R;
import com.example.harmony.UserAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MyPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private FirebaseUser firebaseUser;
    private RelativeLayout loaderLayout;

    EditText mNickName ,mPhoneNumber, mBirthDay, mMajor;


    private ImageView profileImageView; //프로필 이미지 지정
    private String profilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Harmony");

        mNickName = findViewById(R.id.et_nickname);
        mPhoneNumber = findViewById(R.id.et_phoneNumber);
        mBirthDay = findViewById(R.id.et_birthDay);
        mMajor = findViewById(R.id.et_major);
        profileImageView = findViewById(R.id.profileImageView);
        loaderLayout = findViewById(R.id.loaderLayout);

        findViewById(R.id.btn_profile_update).setOnClickListener(onClickListener);
        findViewById(R.id.profileImageView).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
        findViewById(R.id.picturebtn).setOnClickListener(onClickListener);



    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_profile_update:
                    storageUploader();
                    break;
                case R.id.profileImageView:
                    CardView cardView = findViewById(R.id.buttonsCardView);
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.picturebtn:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    // 권한이 없을떄
                    if (ContextCompat.checkSelfPermission(MyPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MyPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(MyPageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                        } else {
                            Toast.makeText(MyPageActivity.this, "권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else { //권한이 있으면(허용)
                        ActivityCompat.requestPermissions(MyPageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        myStartActivity(GalleryActivity.class);
                    }
                    break;
            }
        }
    };

    //갤러리 권한 묻는창 띄우는 코드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                } else {
                    Toast.makeText(MyPageActivity.this, "권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // 뒤로가기 방지
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //사진 프로필이미지뷰에 넘김
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0: {
                //경로에있는 파일그대로 가져옴
                if (resultCode==RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    //찍은 사진을 이미지로 지정
                    Glide.with(this).load(profilePath).centerCrop().override(400).into(profileImageView);
                }
                break;
            }
        }
    }

    private void storageUploader() {
        final String strNickName = mNickName.getText().toString();
        final String strPhoneNumber = mPhoneNumber.getText().toString();
        final String strBirthDay = mBirthDay.getText().toString();
        final String strMajor = mMajor.getText().toString();



        if (strNickName.length() > 0 && strPhoneNumber.length() > 0 && strBirthDay.length() > 0 && strMajor.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            final StorageReference mountainImagesRef = storageRef.child("users/" + firebaseUser.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                UserAccount userAccount = new UserAccount(strNickName, strPhoneNumber, strBirthDay, strMajor);
                storeUploader(userAccount);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                UserAccount userAccount = new UserAccount(strNickName, strPhoneNumber, strBirthDay, strMajor, downloadUri.toString());
                                storeUploader(userAccount);

                            } else {
                                Toast.makeText(MyPageActivity.this, "회원정보를 보내는데 실패하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            Toast.makeText(MyPageActivity.this, "회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUploader(UserAccount userAccount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseUser.getUid()).set(userAccount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MyPageActivity.this, "회원정보 등록을 성공하였습니다.",Toast.LENGTH_SHORT).show();
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyPageActivity.this, "회원정보 등록에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        loaderLayout.setVisibility(View.GONE);
                        Log.w("로그", "Error writing document", e);
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }


}


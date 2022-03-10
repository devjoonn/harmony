package com.example.harmony;

// 사용자 계정 정보 모델 클래스

public class UserAccount {

    private String nickname;
    private String phoneNumber;
    private String birthDay;
    private String major;
    private String photoUrl;

    public UserAccount(String nickname,String phoneNumber, String birthDay, String major, String photoUrl){
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.major = major;
        this.photoUrl = photoUrl;
    }

    public UserAccount(String nickname, String phoneNumber, String birthDay, String major){
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.major = major;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}

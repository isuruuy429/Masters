package com.isuruuy.tapadoc;

public class Doctor {
    private String dob;
    private String email;
    private String gender;
    private boolean isDoctor;
    private String mobile;
    private String name;
    private String specialities;
    private String profilePicture;
    private String id;

    public Doctor() {
    }

    public Doctor(String id, String dob, String email, String gender, boolean isDoctor, String mobile, String name, String specialities, String profilePicture) {
        this.id = id;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.isDoctor = isDoctor;
        this.mobile = mobile;
        this.name = name;
        this.specialities = specialities;
        this.profilePicture = profilePicture;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean getIsDoctor() {
        return isDoctor;
    }

    public void setIsDoctor(boolean isDoctor) {
        this.isDoctor = isDoctor;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialities() {
        return specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

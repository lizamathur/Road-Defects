package com.roaddefects.users;

import java.util.Comparator;

import dao.roaddefects.users.UserDAO;

public class User {
    private String name, pass, email, role, status;
    int id;
    private long mobile;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }

    public User(String email, String pass) {
        this.pass = pass;
        this.email = email;
    }

    public User(String name, String pass, String email, long mobile) {
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.mobile = mobile;
    }

    public User(String name, String pass, String email, int id, long mobile, String role, String status) {
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.id = id;
        this.mobile = mobile;
        this.role = role;
        this.status = status;
    }

    public User(int id, String name, String email, long mobile, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", mobile=" + mobile +
                '}';
    }

    public boolean addUser(){
        UserDAO obj = new UserDAO();
        boolean success = obj.insertUser(this);
        return success;
    }

    public User getUser(){
        UserDAO obj = new UserDAO();
        User user = obj.fetchUser(this.getEmail(), this.getPass());
        return user;
    }

    public String fetchStatus(long user_id) {
        UserDAO obj = new UserDAO();
        String status = obj.fetchStatus(user_id);
        return status;
    }
}

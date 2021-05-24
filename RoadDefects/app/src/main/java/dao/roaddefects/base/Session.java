package dao.roaddefects.base;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context cxt;

    public Session(Context cxt){
        this.cxt=cxt;
        prefs=cxt.getSharedPreferences("Helping", Context.MODE_PRIVATE);
        editor=prefs.edit();
    }

    public void setId(String id){
        editor.putString("id", id);
        editor.commit();
    }

    public String getId(){
        return prefs.getString("id","");
    }

    public void setMobile(String mobile){
        editor.putString("mobile", mobile);
        editor.commit();
    }

    public String getMobile(){
        return prefs.getString("mobile","");
    }

    public void setName(String name){
        editor.putString("name", name);
        editor.commit();
    }

    public String getName(){
        return prefs.getString("name", "");
    }

    public void setRole(String role){
        editor.putString("role", role);
        editor.commit();
    }

    public String getRole(){
        return prefs.getString("role", "");
    }

}

package com.richard.setyourpassword;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

import static androidx.core.content.ContextCompat.getSystemService;

public class Password {
    public static String pwd = "";

    //检查密码是否含有除字母和数字以外的字符
    public static boolean isLetterDigit() {
        String regex = "^[a-z0-9A-Z]+$";
        return pwd.matches(regex);
    }

    //初始化pwd
    public static void setPwd(String str) {
        pwd = str;
    }

    //检查密码长度
    public static boolean checkPwdLength() {
        if (pwd.length() < 4 || pwd.length() > 16) {
            return false;
        } else {
            return true;
        }
    }

    //设置锁屏密码
    public static void applyPwd(DevicePolicyManager dpm, ComponentName mDeviceAdminSample) {
        dpm.resetPassword(pwd, 0);
    }
}

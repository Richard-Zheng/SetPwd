package com.richard.setyourpassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class WipeDataConfirmActivity extends AppCompatActivity {

    public DevicePolicyManager dpm;
    public ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wipe_data_confirm);

        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(getApplicationContext(), MyAdmin.class);
    }

    public void wipeDataConfirm (View view) {
        //获取文本框内容
        EditText editText =(EditText)findViewById(R.id.editTextConfirm);
        String str1 = editText.getText().toString();

        //检查是否为yes
        if (str1.equals("yes")) {
            dpm.wipeData(1);
        } else {
            Toast.makeText(WipeDataConfirmActivity.this, R.string.toast_confirm_wipe_failed, Toast.LENGTH_SHORT).show();
        }
    }
}

package com.richard.setyourpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PickMaximumAttemptsDialogFragment.MaxAttemptsInputListener{

    public DevicePolicyManager dpm;
    public ComponentName mDeviceAdminSample;

    private CheckBox checkbox_admin;

    public void showPickMaximumAttemptsDialog() {
        DialogFragment mPickMaximumAttemptsDialogFragment = new PickMaximumAttemptsDialogFragment();
        mPickMaximumAttemptsDialogFragment.show(getSupportFragmentManager(), "PickMaximumAttemptsDialog");
    }

    //当DialogFragment中发生相应的点击事件时会自动调用到这里面的方法。
    @Override
    public void onMaxAttemptsInputComplete(DialogFragment dialog, int number) {
        // 用户点击DialogFragment中的positive按钮
        dpm.setMaximumFailedPasswordsForWipe(mDeviceAdminSample, number);
    }

    //CheckAdminPermission
    private boolean isOpen() {
        if (dpm.isAdminActive(mDeviceAdminSample)) {//判断超级管理员是否激活
            checkbox_admin.setChecked(true);
            return true;
        } else {
            checkbox_admin.setChecked(false);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_maximum_failed_pwd:
                showPickMaximumAttemptsDialog();
                //dpm.setMaximumFailedPasswordsForWipe(mDeviceAdminSample, 5);
                //Toast.makeText(MainActivity.this, R.string.toast_set_maximum_failed_passwords_for_wipe, Toast.LENGTH_SHORT).show();
                break;
            case R.id.wipe_all_data:
                Intent intent = new Intent(MainActivity.this, WipeDataConfirmActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
            default:
        }
        return true;
    }

    //回调时检查是否申请到设备管理员权限并更新checkbox
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOpen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(getApplicationContext(), MyAdmin.class);

        checkbox_admin = (CheckBox) findViewById(R.id.checkBoxAdmin);
        isOpen();

        checkbox_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//多选框勾选状态改变的监听器
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //以下这段都是API上复制的
                if (isChecked) {//多选框被勾选,激活超级管理员
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.device_admin_description));
                    /*
                     * 不能直接startActivity  因为可能在激活的时候用户点击了取消,这时候CheckBox状态是勾选的,但是实际是没激活的,
                     * 所以要等打开的Activity关闭后的回调函数里去判断是否真正激活,再对CheckBox状态进行改变
                     */
                    startActivityForResult(intent, 0);
                } else {//多选框取消勾选,取消激活超级管理员
                    dpm.removeActiveAdmin(mDeviceAdminSample);
                }
            }
        });


    }

    public void setPassword(View view) {
        if (isOpen()) {
            //获取文本框1的文本
            String str1="";
            EditText editText1 =(EditText)findViewById(R.id.editText2);
            str1=editText1.getText().toString();

            //获取文本框2的文本
            String str2="";
            EditText editText2 =(EditText)findViewById(R.id.editText3);
            str2=editText2.getText().toString();

            if (str1.equals(str2)) {
                Password.setPwd(str1);
                if (Password.checkPwdLength()) {
                    if (Password.isLetterDigit()) {
                        //set password
                        AlertDialog dialog = new AlertDialog.Builder(com.richard.setyourpassword.MainActivity.this)
                                .setTitle(R.string.ensure_set_tittle)//设置对话框的标题
                                .setMessage(getResources().getString(R.string.ensure_set_text) + "“" + Password.pwd + "”")//设置对话框的内容
                                //设置对话框的按钮
                                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                        Password.applyPwd(dpm, mDeviceAdminSample);
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    } else {
                        //include special character
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(R.string.dialog_warning)//设置对话框的标题
                                .setMessage(R.string.dialog_contains_special_characters)//设置对话框的内容
                                //设置对话框的按钮
                                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                        //set password
                                        AlertDialog dialog2 = new AlertDialog.Builder(com.richard.setyourpassword.MainActivity.this)
                                                .setTitle(R.string.ensure_set_tittle)//设置对话框的标题
                                                .setMessage(getResources().getString(R.string.ensure_set_text) + "“" + Password.pwd + "”")//设置对话框的内容
                                                //设置对话框的按钮
                                                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                                        Password.applyPwd(dpm, mDeviceAdminSample);
                                                        dialog.dismiss();
                                                    }
                                                }).create();
                                        dialog2.show();
                                        dialog.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_failed)//设置对话框的标题
                            .setMessage(R.string.dialog_failed_length)//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_failed)//设置对话框的标题
                        .setMessage(R.string.dialog_failed_donotmatch)//设置对话框的内容
                        //设置对话框的按钮
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_failed)//设置对话框的标题
                    .setMessage(R.string.dialog_no_device_admin_permission)//设置对话框的内容
                    //设置对话框的按钮
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }
    }
}

/*
AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("我是对话框")//设置对话框的标题
                    .setMessage("我是对话框的内容")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "点击了确定的按钮", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
 */
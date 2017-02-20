package com.demo.navigation.navigationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.demo.navigation.navigationdemo.gaode.Gaode3DMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void buttonClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.gaode_btn:
                intent.setClass(this,Gaode3DMap.class);
                break;
            case R.id.to_rechage_btn:
                intent.setClass(this,RechargeActivity.class);
                break;
        }
        startActivity(intent);
    }
}

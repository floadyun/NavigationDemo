package com.demo.navigation.navigationdemo.gaode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.demo.navigation.navigationdemo.R;
import com.demo.navigation.navigationdemo.util.LocationHelper;

/**
 * Created by yixiaofei on 2017/2/16 0016.
 */

public class GaodeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gaode_list);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationHelper.sharedInstance(this).destroyLocation();
    }
    public void buttonClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.open_2d_btn:
               // intent.setClass(this,Gaode2DMap.class);
                startActivity(intent);
                break;
            case R.id.open_3d_btn:
                intent.setClass(this,Gaode3DMap.class);
                startActivity(intent);
                break;
            case R.id.open_navigation_btn:
                intent.setClass(this,NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.location_btn:
                LocationHelper.sharedInstance(this).startLocation();
                break;
        }
    }
}

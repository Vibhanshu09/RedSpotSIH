package com.sistec.redspot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isInternetAvailable()){
                    if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, secondsDelayed * 2000);
    }
    private boolean isInternetAvailable(){
        if (AppConnectivityStatus.isOnline(getApplicationContext())) {
            return true;
        } else {
            noInternetAlert();
            return false;
        }
    }
    private void noInternetAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(SplashActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Connectivity Error")
                .setMessage("No Internet Access!!!")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SplashActivity.this.finish();
                    }
                })
                .setIcon(R.drawable.ic_error_red_24dp)
                .setCancelable(false)
                .show();
    }
}

package com.example.user.late;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
boolean f;
    public void onClickNovZap(View view) {
        ConnectivityManager cm=(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);//проверка сети
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();//получение информации о сети
        if(networkInfo!=null){//если сеть есть
            Intent intent = new Intent(MainActivity.this, NovZap.class);//переход на создание записи
            startActivity(intent);
        }
        else{//оповещение об ошибке
    Toast.makeText(getApplicationContext(),"Нет интернет соединения",Toast.LENGTH_LONG).show();
}
f=true;
    }

    public void onClickOldZ(View view) {
        Intent intent2 = new Intent(MainActivity.this, OldZap.class);//переход на список с записями
        startActivity(intent2);
    }


}
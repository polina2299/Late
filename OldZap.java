package com.example.user.late;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class OldZap extends AppCompatActivity {
    ArrayList<String> al;
    ArrayList<String> als;

    static String selectedItem="";
    static String upic="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_zap);
        ListView lv=(ListView) findViewById(R.id.lv);//установка списка

      al=new ArrayList<String>();
      als=new ArrayList<String>();

        NovZap.db=getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        NovZap.db.execSQL("CREATE TABLE IF NOT EXISTS first (name TEXT, date TEXT)");
        NovZap.db.execSQL("CREATE TABLE IF NOT EXISTS second (names TEXT, dates TEXT)");
        Cursor query = NovZap.db.rawQuery("SELECT * FROM first;", null);//устанвока курсара для бд, с помощью которго будем получать данные из таблицы 1

        if(query.moveToFirst()){//выполняется, пока не пройдет все строки строки
            do{
                String name = query.getString(0);
                String datetime = query.getString(1);
                String sum=name+"*"+datetime;
                al.add(sum);//запись в лист
            }
            while(query.moveToNext());
        }
        query.close();
       Cursor query1=NovZap.db.rawQuery("SELECT * FROM second;", null);//устанвока курсара для бд, с помощью которго будем получать данные из таблицы 2

        if(query1.moveToFirst()){
            do{
                String urlpic = query1.getString(0);
                String edittext = query1.getString(1);

                String sums=urlpic+"*"+edittext;
                als.add(sums);
            }
            while(query1.moveToNext());
        }
        query1.close();
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, //создание адаптера, который будет получать данные из массивного листа и по ним добавлять строки в лист на экране
                android.R.layout.simple_list_item_1, al);
        lv.setAdapter(adapter);
   lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//слушатель на касание по строке
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NovZap.db=getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
             selectedItem=al.get(position);//получение названия строки (дата+местоположение)
            upic=als.get(position);//получение id изображения и описания, которое ввел пользователь

            Intent intents=new Intent(OldZap.this,OpenItem.class);
            startActivity(intents);

         //   NovZap.db.close();

        }
    });

    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//установка слушателя на длительное касание строки
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int positiond, long id) {
            AlertDialog.Builder builder=new AlertDialog.Builder(OldZap.this);//диалоговое окно
            builder.setTitle("Удалить эту запись?");//текст окна
            builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {//устанвока кнопки
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem1=al.get(positiond);//получение названия строки
                    String deletedata;
                    int ind=selectedItem1.indexOf('*');//поиск индекса разделителя даты и местоположения
                    deletedata=selectedItem1.substring(ind+1,selectedItem1.length());//поиск даты
                    NovZap.db.delete("first","date=?",new String[]{deletedata});//удаление из бд строки, в котрой есть эта дата
                    al.remove(positiond);//удаление из массивного списка
                    String selectedItem2=als.get(positiond);//олучение изображения и описания
                    String deletetext;
                    int ind1=selectedItem2.indexOf('*');
                    deletetext=selectedItem2.substring(ind1,selectedItem2.length());
                   NovZap.db.delete("second","dates=?",new String[]{deletetext});
                    als.remove(positiond);
                    adapter.notifyDataSetChanged();
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {//установка кнопки отмена
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert=builder.create();
            alert.show();
            return true;
        }
    });

    }

    public void onClickMenu(View view) {//кнопка для возврата в меню
        Intent intntmenu=new Intent(OldZap.this,MainActivity.class);
        startActivity(intntmenu);
    }
}

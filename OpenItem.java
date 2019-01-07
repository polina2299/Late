package com.example.user.late;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class OpenItem extends AppCompatActivity {
TextView date;
TextView location;
ImageView imageView;
TextView txtdesc;
String den;
String place;
String urlpic;
String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_item);
        date=(TextView) findViewById(R.id.textViewd);
        location=(TextView) findViewById(R.id.textView2s);
        txtdesc=(TextView) findViewById(R.id.txtdesc);
        imageView=(ImageView) findViewById(R.id.imageView);
        int ind=OldZap.selectedItem.indexOf('*');//поиск индекса разделителя для даты и локации
        den=OldZap.selectedItem.substring(0,ind);//дата
        place=OldZap.selectedItem.substring(ind,OldZap.selectedItem.length());//локация
       date.setText(den);
      location.setText(place);
      int dens=OldZap.upic.indexOf('*');//находим индекс разделителя для картинки и описания
       urlpic=OldZap.upic.substring(0,dens);//картинка
        description=OldZap.upic.substring(dens+1,OldZap.upic.length());//описание
        txtdesc.setText(description);
       try {
            final InputStream imageStream = getContentResolver().openInputStream(Uri.parse(urlpic));//установка изображения
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(selectedImage);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
           Toast.makeText(getApplicationContext(),"Ошибка в добавлении фотографии",Toast.LENGTH_LONG).show();
        }
    }

    public void OnClickBack(View view) {
        Intent inte=new Intent(OpenItem.this,OldZap.class);
        startActivity(inte);
    }
}

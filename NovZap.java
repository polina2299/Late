package com.example.user.late;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivateKey;
import java.util.Date;

public class NovZap extends AppCompatActivity  implements View.OnTouchListener{
    TextView txtdate;
    TextView txtplace;
    String picture="abc";
    private final int Pick_image = 1;
    ProgressDialog pDialog;
    NodeList nodeList;
    View view;
    EditText desc;
    Date date;
    ImageView imageview;
public static String c="";
    String URL = "http://ip-api.com/xml"; //ссылка на api, с помощью которого будет определяться местоположение
  public static SQLiteDatabase db;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nov_zap);
        txtdate = (TextView) findViewById(R.id.textView);//место для даты
         date = new Date();
        desc=(EditText) findViewById(R.id.editText);//место для ввода описания
        txtdate.setText(date.toString());//установка даты
        txtplace = (TextView) findViewById(R.id.textView2);//место для местоположения
        imageview=(ImageView) findViewById(R.id.imageView);//место для картинки
      new DownloadXML().execute(URL);//загрузка файла XML для получения местоположения с помощью api
view=(View) findViewById(R.id.vid);//инициализация всего экрана
view.setOnTouchListener(this);//установка слушателя касания

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {//метод для загрузки изображения из галереи
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){//если фотография выбрана
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();//получение id для изобраения
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri); //загрузка изображения
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);//преобразование
                        imageview.setImageBitmap(selectedImage);//установка
                        picture=imageUri.toString();//сохранение id изображения, чтобы записать его в БД

                    } catch (FileNotFoundException e) {//ошибка , если изображение не найдено
                        e.printStackTrace();
                    }

                }
        }}

    @Override
    public boolean onTouch(View v, MotionEvent event) {//метод для установки изображения (по касанию )
            float x=event.getX();
        float y=event.getY();
        if(x>0&&x<250&&y>0&&y<250){//если касание попало в данную область, открывается галерея для выбора  изображения
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            //Тип получаемых объектов - image:
            photoPickerIntent.setType("image/*");
            //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
            startActivityForResult(photoPickerIntent, Pick_image);
        }
        return false;
    }

    public void OnClickSave(View view) {//сохранение данных
        Toast.makeText(getApplicationContext(),"Изменения сохранены",Toast.LENGTH_LONG).show();//оповещение о сохранении
        db=getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);//проверка наличия бд(ее создание)
        db.execSQL("CREATE TABLE IF NOT EXISTS first (name TEXT, date TEXT)");//проверка наличия таблицы(ее создание)
        String ins="INSERT INTO first VALUES ('"+c+"', '"+date.toString()+"');";//запись в бд местоположения и даты
       db.execSQL(ins);//выполнение запроса

        db.execSQL("CREATE TABLE IF NOT EXISTS second (names TEXT, dates TEXT)");//проверка наличия таблицы(ее создание)
        String inss="INSERT INTO second VALUES ('"+picture+"', '"+desc.getText().toString()+"');";//запись в бд id изображения и описания
        db.execSQL(inss);//выполнение запроса

        Intent intent1=new Intent(NovZap.this,MainActivity.class);//переход на меню
        startActivity(intent1);//выполнение перехода

        }


    private class DownloadXML extends AsyncTask<String, Void, Void> {//специальный класс для выполнения тяжелых задач в новом потоке
        @Override
        protected void onPreExecute(

        ) {//обязательный метод класса
            super.onPreExecute();
            pDialog = new ProgressDialog(NovZap.this);//создание диалогового окна
            pDialog.setTitle("Загрузка");//название окна
            pDialog.setMessage("Загрузка...");//сообщение в окне
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... Url) {//обязательный метод класса
            try {
                java.net.URL url = new URL(Url[0]);
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(new InputSource(url.openStream()));//подключение к api
                doc.getDocumentElement().normalize();

                nodeList = doc.getElementsByTagName("query");//поиск текста с тегом "query"

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {//обязательный метод класса
            Node nNode = nodeList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String a = getNode("country", eElement);//поиск данных с данным тегом
                String b = getNode("city", eElement);//поиск данных с данным тегом

                c = a + ", " + b; //соединение полученных данных (страна+город)
                txtplace.setText(c);//установка местооложения

                pDialog.dismiss();//удаление диалогового окна
                super.onPostExecute(aVoid);
            }
        }


    private  String getNode(String sTag, Element eElement) {//метод для получения узла (узел - информация, заключенная между двумя тегами)
        NodeList nList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nList.item(0);
        return nValue.getNodeValue();
    }}



}
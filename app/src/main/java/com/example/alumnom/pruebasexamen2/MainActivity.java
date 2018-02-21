package com.example.alumnom.pruebasexamen2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String URL ="http://api.openweathermap.org/data/2.5/forecast?id=3130616&APPID=625af28da655aac47ecc3c329383833b";

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.layout);

        RequestQueue request = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectPrincipal = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {

                //listDatosMeteorologicos = new ArrayList<DatosMeteorologicos>();

                try{
                    //tv1.setText(response.toString(0));
                    JSONObject jsonObjectAuxiliar = new JSONObject(response.toString(0));
                    JSONArray jsonList = jsonObjectAuxiliar.getJSONArray("list");
                    for(int i = 0; i < jsonList.length();i++){
                        //Toast.makeText(getApplicationContext(),i,Toast.LENGTH_SHORT).show();
                        String temperatura = jsonList.getJSONObject(i).getJSONObject("main").getString("temp");
                        String aspecto= jsonList.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                        String fechaHora= jsonList.getJSONObject(i).getString("dt_txt");
                        alta(temperatura,aspecto,fechaHora);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(jsonObjectPrincipal);
        listarTodos();

    }





        //sqLiteDatabase.execSQL("create table tiempo(codigo int primary key, temperatura text, aspecto text, fechaHora text)");


    public void alta (String temperatura, String aspecto, String fechaHora){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administrador", null, 1);
        SQLiteDatabase bd=admin.getWritableDatabase();

        int nuevoCodigo = 1;

        Cursor fila=bd.rawQuery("select max(codigo) from tiempo", null);
        if(fila.moveToFirst()){
            nuevoCodigo = fila.getInt(0)+1;
        }



        ContentValues registro=new ContentValues();
        registro.put("codigo", nuevoCodigo);
        registro.put("temperatura", temperatura);
        registro.put("aspecto", aspecto);
        registro.put("fechaHora", fechaHora);
        try {
            bd.insert("tiempo", null, registro);
        }catch (Exception e){
            Toast.makeText(this, "No se cargó el registro en la BD", Toast.LENGTH_LONG).show();
        }

        bd.close();
        Toast.makeText(this, "Se cargó en registro en la BD", Toast.LENGTH_LONG).show();
    }

    public void listarTodos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administrador", null, 1);
        SQLiteDatabase bd=admin.getWritableDatabase();
        Cursor fila=bd.rawQuery("select codigo, temperatura, aspecto, fechaHora from tiempo", null);
        layout.removeAllViews();
        if(fila.moveToFirst()){
            do{
                TextView tv = new TextView(this);
                tv.setText(fila.getString(0)+" : "+fila.getString(1)+" : ("+fila.getString(2)+") : "+fila.getString(3));
                layout.addView(tv);
            }while(fila.moveToNext());
        }
        bd.close();
    }
}

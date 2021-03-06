package com.gentritibishi.lyricstranslator;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LyricsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static int responseCode = 0;
    public static String responseString = "";
    TextView tv_lyricsToSet;
    String language = null;
    Spinner spinner1;
    String lyricsText = null;
    String lyricsEncoded = null;
    String savedEnglishLyrics = null;
    String sourceByDefault = "en";
    String[] targetLanguage=new String[] {"en","it", "de", "es","fr","tr"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        tv_lyricsToSet = findViewById(R.id.tv_lyricsToSet);
        spinner1 = findViewById(R.id.spinner1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lyricsText = extras.getString("lyrics");
            savedEnglishLyrics = extras.getString("lyrics");
            tv_lyricsToSet.setText(lyricsText);
            //The key argument here must match that used in the other activity

        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(LyricsActivity.this, R.array.targets, R.layout.spinner_style);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(text.equals("EN")){
                    //from DETECTED LANGAUGE to EN
                    detectLanguage();
                    translateFun(text);

                }else if (text.equals("IT")){
                    //from DETECTED LANGAUGE to IT
                    detectLanguage();
                    translateFun(text);

                }else if(text.equals("DE")){
                    //from DETECTED LANGAUGE to DE
                    detectLanguage();
                    translateFun(text);

                }else if(text.equals("ES")){
                    //from DETECTED LANGAUGE to ES
                    detectLanguage();
                    translateFun(text);

                }else if(text.equals("FR")){
                    //from DETECTED LANGAUGE to FR
                    detectLanguage();
                    translateFun(text);

                }else if(text.equals("TR")){
                    //from DETECTED LANGAUGE to TR
                    detectLanguage();
                    translateFun(text);

                }



            }
        });
        //Spinneri END
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static String compress(String str) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(str.getBytes());
        os.close();
        gos.close();
        return Base64.encodeToString(os.toByteArray(),Base64.DEFAULT);
    }

    void detectLanguage(){
        try {
            // split
            String newStr = lyricsText.substring(0, 20);
            String newStrEncoded = URLEncoder.encode(newStr, String.valueOf(StandardCharsets.UTF_8));
            OkHttpClient client = new OkHttpClient();
            try {
                // Build the request
                RequestBody body = new FormBody.Builder()
                        .add("q", newStrEncoded)
                        .build();

                Request request = new Request.Builder()
                        .url("https://google-translate1.p.rapidapi.com/language/translate/v2/detect")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("Accept-Encoding", "application/gzip")
                        .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                        .addHeader("X-RapidAPI-Key", "cb5af54ae2msh8519ab6c4d423d1p1c6cd3jsn8d8003432950")
                        .build();
                Response responses = null;

                // Reset the response code
                responseCode = 0;

                // Make the request
                responses = client.newCall(request).execute();

                if ((responseCode = responses.code()) == 200) {
                    // Get response
                    String jsonData = responses.body().string();

                    try {
                        // Transform reponse to JSon Object
                        JSONObject json = new JSONObject(jsonData);
                        JSONObject data = json.getJSONObject("data");
                        JSONArray detections = data.getJSONArray("detections");
                        JSONArray last = detections.getJSONArray(0);
                        JSONObject lastobj = last.getJSONObject(0);
                        language = lastobj.getString("language");
                        if (language != null && !language.isEmpty()) {
                            if(language.equals("en")){
                                spinner1.setSelection(0);
                            }else if(language.equals("it")){
                                spinner1.setSelection(1);
                            }else if(language.equals("de")){
                                spinner1.setSelection(2);
                            }else if(language.equals("es")){
                                spinner1.setSelection(3);
                            }else if(language.equals("fr")){
                                spinner1.setSelection(4);
                            }else if(language.equals("it")){
                                spinner1.setSelection(5);
                            }
                        }

                        Toast.makeText(LyricsActivity.this, language, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }

            } catch (IOException e) {
                responseString = e.toString();
            }

        } catch (IOException e) {
            Toast.makeText(LyricsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void translateFun(String text) {
        try {
            // split
            // String newStr = lyricsText.substring(0, 20);
            lyricsEncoded = URLEncoder.encode(lyricsText, String.valueOf(StandardCharsets.UTF_8));
            OkHttpClient client = new OkHttpClient();
            try {
                // Build the request
                RequestBody body = new FormBody.Builder()
                        .add("q", lyricsEncoded)
                        .add("target", text)
                        .add("source", language)
                        .build();

                Request request = new Request.Builder()
                        .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("Accept-Encoding", "application/gzip")
                        .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                        .addHeader("X-RapidAPI-Key", "0f2e369b28msh4221b47047c23d6p19e7dejsn9c97811e263a")
                        .build();

                Response responses = null;

                // Reset the response code
                responseCode = 0;

                // Make the request
                responses = client.newCall(request).execute();

                if ((responseCode = responses.code()) == 200) {
                    // Get response
                    String jsonData = responses.body().string();

                    try {
                        // Transform reponse to JSon Object
                        JSONObject json = new JSONObject(jsonData);
                        JSONObject data = json.getJSONObject("data");
                        JSONArray translations = data.getJSONArray("translations");
                        JSONObject rec = translations.getJSONObject(0);
                        String translatedText = rec.getString("translatedText");
                        tv_lyricsToSet.setText(translatedText);

                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }

            } catch (IOException e) {
                responseString = e.toString();
            }

        } catch (IOException e) {
            Toast.makeText(LyricsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
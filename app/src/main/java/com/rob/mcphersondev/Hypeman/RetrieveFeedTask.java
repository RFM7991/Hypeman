package com.rob.mcphersondev.Hypeman;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private Exception exception;
    private Activity context;


    protected void onPreExecute() {
    }

    protected String doInBackground(String... strings) {
        String word = strings[0];
        String link = strings[1];

        Log.i("INFO", "param " + word);
        //https://api.datamuse.com/words?rel_rhy=
        // http://rhymebrain.com/talk?function=getWordInfo&getRhymes&word=
        String API_URL = link;
        // Do some validation here

        try {
            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("secret-key", "$2a$10$EPfJXSb9ngHBmHphdZ8Zfuk0YzPduXaA9LaGPAuuzjYFDgAKrIZ3S");


            try {
                StringBuilder stringBuilder;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                }
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            System.exit(0);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
    }


    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }
}


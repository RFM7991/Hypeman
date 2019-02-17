package com.rob.mcphersondev.Hypeman;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSON {

    private final String GET_URL = "http://rhymebrain.com/talk?function=getRhymes&word=";

    private ArrayList<String> wordList, rootList, rootPool;
    private int rootIndex;

    public JSON(ArrayList inRootList) {
        wordList = new ArrayList<String>();
        rootList = inRootList;
        rootPool = inRootList;
    }

    public void setRootIndex(int i) {
        rootIndex = i;
    }

    public int getRootIndex() {
        return rootIndex;
    }

    public void clearWordList() {
        wordList.clear();
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }

    public ArrayList<String> getRootList() {
        return rootList;
    }

    public ArrayList<String> getPool() {
        return rootPool;
    }


    public ArrayList<String> getRhyme(String word) throws IOException {
        String returnLine = "";
        String searchResults = "";
        String Search_Term = word;

        Log.i("MyApplication2","Processing Get Request...");
        for (int j = 1; j < 2; j++) {
            int perPage = 100;
            // int currentPage = 1;
            String Per_Page = "&per_page=" + perPage;
            String Curr_Page = "?page=" + j;

            URL obj = new URL(GET_URL + Search_Term);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //	con.setRequestProperty("Authorization", "Bearer ");
            int responseCode = con.getResponseCode();
            Log.i("MyApplication2","j + \": GET Response Code :: \" + responseCode");
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();

                // Formatting
                while ((inputLine = in.readLine()) != null) {
                    int last = 0;
                    for (int i = 1; i < inputLine.length(); i++) {
                        if (inputLine.substring(last, i).endsWith(",")) {
                            response.append(inputLine.substring(last, i) + "\n");
                            last = i;
                        }
                    }
                    response.append(inputLine.substring(last, inputLine.length()));
                }

                //		returnLine = inputLine;
                //		System.out.println(returnLine);
                in.close();
                //		System.out.println("Results: " + "\n" + response.toString());

                // JSON Reader
                try {
                    JSONParser parser = new JSONParser();

                    Object object = parser.parse(response.toString());
                    JSONArray jsonArr = (JSONArray) object;

                    // Get data for Results array
                    for (int i = 0; i < jsonArr.size(); i++) {
                        // Store the JSON objects in an array
                        // Get the index of the JSON object and print the values as per the index
                        JSONObject jsonObj1 = (JSONObject) jsonArr.get(i);

                        if (!jsonObj1.get("word").toString().contains(" ")
                                && jsonObj1.get("flags").toString().contains("bc")) {
                            searchResults += jsonObj1.get("word") + "\n";
                            wordList.add(jsonObj1.get("word").toString());
                        }

                    }
                    Thread.sleep(250);
                } catch (ParseException | InterruptedException e) {
                    e.printStackTrace();
                }
//				System.out.println(searchResults);
                System.out.println("GET DONE");
            } else {
                System.out.println("GET request failed");
            }

        }
        return wordList;
    }
}
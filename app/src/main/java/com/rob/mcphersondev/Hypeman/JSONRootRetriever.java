package com.rob.mcphersondev.Hypeman;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JSONRootRetriever {

    private  String GET_URL = "";
    private ArrayList<Root> rootArray = new ArrayList<>();

    public JSONRootRetriever(String url) {
        GET_URL = url;
    }

    public ArrayList<Root> getRoots() throws IOException {

        Log.i("JSONRootRetriever","Processing Get Request...");
        for (int j = 1; j < 2; j++) {
            int perPage = 100;
            // int currentPage = 1;
            String Per_Page = "&per_page=" + perPage;
            String Curr_Page = "?page=" + j;

            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //	con.setRequestProperty("Authorization", "Bearer ");
            int responseCode = con.getResponseCode();
            Log.i("JSONRootRetriever","j + \": GET Response Code :: \" + responseCode");
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
                		System.out.println("Results: " + "\n" + response.toString());

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

                        String root = jsonObj1.get("root").toString();
                        String url = jsonObj1.get("url").toString();

                        rootArray.add(new Root(root, url));


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
        return rootArray;
    }
}

package com.example.switchingactivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


public class ActivityGame1 extends AppCompatActivity{
    private Button btnCheck;
    private EditText inputBox1;
    private Button btnHome;
    private TextView tv1;
    private String str;
    private ConstraintLayout background;
    private ProgressBar timerBar;


    //game setup
    private ArrayList<TextView> letters = new ArrayList<>();
    private TextView resultLbl;

    //api feeder
    String url;
    private String word = "";

    //point system and api response variables
    ArrayList<String> parts = new ArrayList<>();
    private ArrayList<Word> allWords = new ArrayList<>();
    private ArrayList<Word> normWrds = new ArrayList<>();
    private ArrayList<Word> extraWrds = new ArrayList<>();

    //timer variables
    private boolean gameStart = false;
    private Date timeBench = new Date();
    private int seconds = 20;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        background = findViewById(R.id.background);
        tv1 = (TextView) findViewById(R.id.tv1);

        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//this line of code closes this screen. Prevents the back arrow from incorrectly moving back and forth between screens
            }
        });

        letters.add((TextView)findViewById(R.id.letter1));
        letters.add((TextView)findViewById(R.id.letter2));
        letters.add((TextView)findViewById(R.id.letter3));
        letters.add((TextView)findViewById(R.id.letter4));
        letters.add((TextView)findViewById(R.id.letter5));
        letters.add((TextView)findViewById(R.id.letter6));
        letters.add((TextView)findViewById(R.id.letter7));
        letters.add((TextView)findViewById(R.id.letter8));

        getWords();

        Timer x = new Timer();
        x.schedule(new TimerTask() {
            @Override
            public void run() {
                if (allWords.size()!=0) {
                    gameStart = true;
                    timeBench = new Date();

                    for (TextView x:letters) {
                        x.setEnabled(true);
                    }

//                    sortLists();
                    for (Word x: allWords) {
                        System.out.println(x.getName() + ": " + x.getCategory());
                    }

                    System.out.println("NORMAL: " + normWrds);
                    System.out.println("EXTRA: " + extraWrds);
                    this.cancel();
                }
            }
        }, 0, 25);

        timerBar = findViewById(R.id.timeBar);
        Timer y = new Timer();
        y.schedule(new TimerTask() {
            Date timeCurr = new Date();
            @Override
            public void run() {
                if (gameStart) {
                    timeCurr = new Date();
                    if (seconds>20) {
                        seconds = 20;
                    }
                    if (TimeUnit.MILLISECONDS.toSeconds(timeCurr.getTime()-timeBench.getTime()) > .5) {//second has passed
                        timeBench = new Date();
                        seconds--;
                        timerBar.setProgress((int) (100/20) * seconds);
                    }

                    if (timerBar.getProgress()==0) {
                        System.out.println("WE OUT THIS BISSH");
                        //end game reset variables etc etc
                        this.cancel();
                    }
                }
            }
        },0, 25);


//        //button onclicklistener
//        //remove words already solved for in allwords
//        Word randWord = allWords.get((int) (Math.random()*allWords.size()));
//        String urlDef = new
//        new AsyncHttpClient().get()
    }

    public void sortLists() {
        for (Word x: allWords) {
            if (x.getCategory()==1) {
                normWrds.add(new Word(x.getName(), x.getFrequency()));
            } else if (x.getCategory()==2) {
                extraWrds.add(new Word(x.getName(), x.getFrequency()));
            }
        }

        System.out.println("NORMAL: " + normWrds);
        System.out.println("EXTRA: " + extraWrds);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean letterClicked = false;//checks if you actually clicked on a letter when you started pressing on screen
        int prevId= 0;
        //sees what part of mouseevent you are on
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN://when you press the screen
                for (TextView tv: letters) {//this if statement checks if your point started on a textview
                    int[] locPop = new int[2];
                    tv.getLocationOnScreen(locPop);
                    int y = locPop[1];
                    int x = locPop[0];

                    if (event.getX() >= x && event.getX() <= (x+tv.getWidth()) && event.getY() >= y && event.getY() <= (y+tv.getHeight())) {
                        if (word.length()>0 && !(word.substring(word.length()-1).equals(tv.getText().toString()))) {//this checks if you already have the letter you are on in the string word to avoid repetition of letters
                            word = tv.getText().toString();
                        }
                        if (word.length()==0) {//if you dont have any letters this is your starting letter
                            word = tv.getText().toString();
                        }
                        tv.setBackgroundColor(Color.parseColor("magenta"));
                        letterClicked = true;
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE://when you slide finger across screen
                for (TextView tv: letters) {//this if statement sees if a pointer (finger) goes over a textview/letter and checks if the word string already has the letter in it to avoid repetition and checks the length of word string to make sure there are actually letters in there
                    int[] locPop = new int[2];
                    tv.getLocationOnScreen(locPop);
                    int y = locPop[1];
                    int x = locPop[0];


                    if (word.length()>0 && event.getX() >= x && event.getX() <= (x+tv.getWidth()) && event.getY() >= y && event.getY() <= (y+tv.getHeight()) && !(tv.getHighlightColor() == Color.YELLOW) && !(word.substring(word.length()-1).equals(tv.getText().toString()))) {
                        word += tv.getText().toString();
                        tv.setBackgroundColor(Color.parseColor("magenta"));
                    }
                }
                break;
            case MotionEvent.ACTION_UP://when you lift finger up from screen
                if (word.length()>1) {
                    word = word.toLowerCase();
                    if (parts.contains(word)){
                        tv1.setText("wordnhjedfvewngvhewnkgyjvehwbglejnlhfwvlnyewvbrghew,nhvr,hkjsenvafnh,jesv,hnkfzs,nshkvhnvbf");

                        int color = Color.RED;
                        //if word made is in normal
                        color = Color.GREEN;
                        //if word made is in extra
                        color = Color.YELLOW;
                        //else
                        color = Color.RED;

                        background.setBackgroundColor(color);
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            Date benchDate = new Date();
                            Date currDate = new Date();
                            @Override
                            public void run() {
                                currDate = new Date();
                                if (TimeUnit.MILLISECONDS.toSeconds(currDate.getTime()-benchDate.getTime()) > 1) {
                                    background.setBackgroundColor(Color.BLUE);
                                    this.cancel();
                                }
                            }
                        },0,25);
                    }
                }
                word = "";//clears word for next use and clears highlights
                for (TextView tv: letters) {
                    tv.setBackgroundColor(Color.parseColor("white"));
                }


                break;
        }
        return true;
    }


    public void getWords(){
//        while (parts.size()<10) {
            String url1 = "http://www.anagramica.com/all/:ghabcdef";//api URL MUST HAVE HTTPS:// THAT IS NOT OPTIONAL

            new AsyncHttpClient().get(url1, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    str = new String(responseBody);//this will show all the raw data which can be substringed for individual data
                    str = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
                    str = str.replaceAll("\"", "");
                    str = str.replaceAll(" ", "");
                    str = str.replaceAll("\n", "");
                    String[] p = str.split(",");
                    parts.addAll(Arrays.asList(p));
                    for (int i = 0; i < parts.size(); i++) {
                        if (parts.get(i).length() <= 2) {
                            parts.remove(i);
                            i = 0;
                        }
                    }
                    if (parts.size()<30) {
                        getWords();
                        return;
                    }

                    for (String x : parts) {
                        firstCallMuse(x);
                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();//wil print error message
                }

            });
//        }
        System.out.println(parts);
    }



    public void firstCallMuse(final String word) {
        final ArrayList<String> wordsFC = new ArrayList<>();
        String url = "https://api.datamuse.com/words?ml=" + word + "&md=f&max=10";

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        asyncClient.setTimeout(120*1000);
        asyncClient.get(url, new AsyncHttpResponseHandler() {
            JsonArray jsonResp = new JsonArray();
            JsonParser parser = new JsonParser();

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String rawResponse = new String(responseBody);
                if (rawResponse.equals("[]")) {
                    parts.remove(word);
                    System.out.println("NOT A WORDDDDD");
                } else {
                    System.out.println(word + ": CALL 1 GOOD");
                    jsonResp = (JsonArray) parser.parse(rawResponse);

                    try {
                        for (int i = 0;i<jsonResp.size();i++) {
                            wordsFC.add(jsonResp.get(i).getAsJsonObject().get("word").getAsString());
                        }
                        secondCallMuse(wordsFC, word);
                    }catch (Exception e) {
                        System.out.println("FIRST CALL ERROR");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("FAILURERERERERERERERER");
            }
        });
        //first call collect all words
    }

    public void secondCallMuse(ArrayList<String> FCwords, final String word) {
        //search each word
        //create new word

        for (String x: FCwords) {
            String url = "https://api.datamuse.com/words?ml=" + x + "&md=f&max=10";

            AsyncHttpClient asyncClient = new AsyncHttpClient();
            asyncClient.setTimeout(120*1000);
            asyncClient.get(url, new AsyncHttpResponseHandler() {
                JsonArray myResponse = new JsonArray();
                JsonParser parser = new JsonParser();
//mfxbaesg
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    myResponse = (JsonArray) parser.parse(new String(responseBody));
                    for (int i = 0;i<myResponse.size();i++) {
                        try {
                            if (myResponse.get(i).getAsJsonObject().get("word").getAsString().equals(word)) {
                                JsonArray tempjson = myResponse.get(i).getAsJsonObject().get("tags").getAsJsonArray();
                                //find frequency
                                String freqString = tempjson.get(tempjson.size()-1).getAsString();
                                double frequency = Double.parseDouble(freqString.substring(2));
                                //make word obj
                                boolean wordDupl = false;
                                for (Word x: allWords) {
                                    if (x.getName().equals(word)) {
                                        wordDupl = true;
                                    }
                                }
                                if (!wordDupl) {
                                    allWords.add(new Word(myResponse.get(i).getAsJsonObject().get("word").getAsString(), frequency));
                                    switch (allWords.get(allWords.size()-1).getCategory()) {
                                        case 1:
                                            normWrds.add(allWords.get(allWords.size()-1));
                                            break;
                                        case 2:
                                            extraWrds.add(allWords.get(allWords.size()-1));
                                            break;
                                    }
                                    System.out.println("INDEX: "+i+", SIZE: " + allWords.size() + ", word: " + allWords.get(allWords.size() - 1).getName() + ", category: " + allWords.get(allWords.size() - 1).getCategory());
                                }
                                //put it in list
                                break;
                            }
                        }catch (Exception e) {
                            System.out.println("SECOND CALL ERROR");
                            e.printStackTrace();
                        }
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }

            });
        }
    }
}

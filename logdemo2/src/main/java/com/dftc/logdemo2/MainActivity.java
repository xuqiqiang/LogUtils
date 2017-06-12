package com.dftc.logdemo2;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dftc.logutils.LogUtils;
import com.dftc.logutils.config.LogConfig;
import com.dftc.logutils.parser.ObjParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuqiqiang on 2017/6/2.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testLog();
            }
        });

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "log2";
        LogUtils.initialize(LogConfig.newBuilder()
                .debug(BuildConfig.DEBUG)
                .enableWrite(this, dirPath)
                .reportCrash(true)
                .build());
    }

    private void testLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                while (true) {

                    LogUtils.json("test", "{\"firstName\": \"Brett\", \"lastName\": \"McLaughlin\"}");
                    LogUtils.xml("test",
                            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");

                    LogUtils.dLog("MainActivity", "String[]:" + ObjParser.parseObject(new String[]{"abc", "123"}));

                    LogUtils.dLog("MainActivity", "String[][]:"
                            + ObjParser.parseObject(
                            new String[][]{
                                    {"abc", "123"},
                                    {"def", "456"}
                            }));

                    LogUtils.dLog("MainActivity", "String[][][]:"
                            + ObjParser.parseObject(
                            new String[][][]{
                                    {{"abc", "123"}, {"def", "456"}},
                                    {{"qwe", "123"}, {"zxc", "456"}}
                            }));

                    List<String> list = new ArrayList<>();
                    list.add("list1");
                    list.add("list2");
                    LogUtils.dLog("MainActivity", "list:" + ObjParser.parseObject(list));

                    Map map = new HashMap();
                    map.put("key1", "value1");
                    map.put("key2", "value2");
                    LogUtils.dLog("MainActivity", "map:" + ObjParser.parseObject(map));

                    Shoes[] shoes = new Shoes[]{
                            new Shoes("A", "red"),
                            new Shoes("B", "blue"),
                    };
                    LogUtils.dLog("MainActivity", "shoes:" + ObjParser.parseObject(shoes));

                    List<Shoes> shoesList = new ArrayList<>();
                    Collections.addAll(shoesList, shoes);
                    LogUtils.dLog("MainActivity", "shoesList:" + ObjParser.parseObject(shoesList));

                    Map shoesMap = new HashMap();
                    shoesMap.put("shoes1", shoes[0]);
                    shoesMap.put("shoes2", shoes[1]);
                    LogUtils.dLog("MainActivity", "shoesMap:" + ObjParser.parseObject(shoesMap));

                    Map shoesMap1 = new HashMap();
                    shoesMap1.put(shoes[0], shoes[1]);
                    LogUtils.dLog("MainActivity", "shoesMap1:" + ObjParser.parseObject(shoesMap1));

                    LogUtils.dLog("MainActivity", "object:" + ObjParser.parseObject(new Person("Harry", "male")));

                    LogUtils.dLog("test", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA : %d %s", 123, "abc");
                    LogUtils.dLog("test", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + (sum++));

                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.eLog("test", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" + (sum++));
                    LogUtils.iLog("test", "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC" + (sum++));

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                while (true) {
                    LogUtils.dLog("test", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + (sum++));

                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.eLog("test", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY" + (sum++));
                    LogUtils.iLog("test", "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" + (sum++));

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class Person {
        String name;
        int age;
        String sex;
        Clothes clothes;

        Shoes[] shoes;
        String[] array1;
        String[][] array2;
        String[][][] array3;
        String[][][][] array4;
        List<String> list;
        Map map;

        public Person(String name, String sex) {
            this.name = name;
            this.age = 25;
            this.sex = sex;
            this.clothes = new Clothes("T-shirt", "white");
            this.shoes = new Shoes[]{
                    new Shoes("A", "red"),
                    new Shoes("B", "blue"),
            };

            array1 = new String[]{"abc", "123"};
            array2 = new String[][]{
                    {"abc", "123"},
                    {"def", "456"}
            };
            array3 = new String[][][]{
                    {{"abc", "123"}, {"def", "456"}},
                    {{"qwe", "123"}, {"zxc", "456"}}
            };
            list = new ArrayList<String>();
            list.add("list1");
            list.add("list2");

            map = new HashMap<String, String>();
            map.put("key1", "value1");
            map.put("key2", "value2");
        }


        class Clothes {
            String name;
            String color;
            String[] array1;

            public Clothes(String name, String color) {
                this.name = name;
                this.color = color;

                array1 = new String[]{"abc", "123"};
            }
        }
    }

    class Shoes {
        String name;
        String color;

        public Shoes(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}

package com.github.FalledCan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.github.FalledCan.Main.timeStamp;

public class PSO2NotificationFetcher {

    private static final String URL = "https://pso2.jp/players/news/";

    public static ArrayList<String[]> get() throws IOException {

        Document doc = Jsoup.connect(URL).get();
        System.out.println("[" + timeStamp() + "] サイト取得完了");
        Element topicList = doc.select("ul.topicList").first();
        ArrayList<String[]> list = new ArrayList<>();
        if(topicList != null) {
            Elements listItems = topicList.select("li");
            for(Element listItem : listItems){
                if(!listItem.select("span.new").isEmpty()){
                    String[] output = new String[4];
                    String title = listItem.text().replace("NEW ", "");
                    int firstSpaceIndex = title.indexOf(" ");
                    if(firstSpaceIndex != -1){
                       output[0] = title.substring(0,firstSpaceIndex);//お知らせとかのやつ
                       output[1] = title.substring(firstSpaceIndex+1);//本文
                       output[2] = "";//時間
                       for (int i = 0; i<2;i++){
                           int lastSpaceIndex = output[1].lastIndexOf(" ");
                           output[2] = output[1].substring(lastSpaceIndex + 1) + ((i == 0)?"":" ") + output[2];
                           output[1] = output[1].substring(0,lastSpaceIndex);
                       }
                       if(!checkTime(output[2])){//時間チェック
                           continue;
                       }
                       output[3] = listItem.select("a").attr("href");//リンク
                    }
                    list.add(output);
                }
            }

        }
        return list;
    }

    private static boolean checkTime(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime specifiedDateTime = LocalDateTime.parse(time, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.isEqual(specifiedDateTime);
    }

}

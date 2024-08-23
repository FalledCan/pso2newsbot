package com.github.FalledCan;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws InterruptedException {

            jda = JDABuilder.createDefault("").build();
            jda.awaitReady();
            schedule(jda.getGuildById("").getTextChannelById(""));
    }

    public static String timeStamp(){
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(now);
    }

    private static void schedule(TextChannel channel) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[" + timeStamp() + "] スケジュール実行");
            if (channel != null) {
                ArrayList<String[]> list = null;
                try {
                    System.out.println("[" + timeStamp() + "] 通知リスト取得中...");
                    list = PSO2NotificationFetcher.get();
                    System.out.println("[" + timeStamp() + "] 通知リスト取得完了");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(!list.isEmpty()) {
                    for (String[] data:list){
                        System.out.println("[" + timeStamp() + "] 通知内容[" +data[0] + "," +data[1] + "," +data[2] + "," + data[3] + "]");
                        channel.sendMessageEmbeds(createEmbed(data).build()).queue();
                        System.out.println("[" + timeStamp() + "] メッセージ送信完了");
                    }
                }else {
                    System.out.println("[" + timeStamp() + "] 通知無し");
                }
            }
            System.out.println("[" + timeStamp() + "] スケジュール終了");
        }, 0, 1, TimeUnit.MINUTES);
    }

    private static EmbedBuilder createEmbed(String[] data) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(data[0]);
        embedBuilder.setTitle(data[1],data[3]);
        embedBuilder.setFooter(data[2]);
        switch (data[0]){
            case "お知らせ":
                embedBuilder.setColor(0x2F93CC);
                break;
            case "対応状況":
                embedBuilder.setColor(0xB755B4);
                break;
            case "重要":
                embedBuilder.setColor(0xE31414);
                break;
            case "WEB":
                embedBuilder.setColor(0x776E49);
                break;
            case "メンテナンス":
                embedBuilder.setColor(0x278D96);
                break;
            case "アップデート":
                embedBuilder.setColor(0x33428A);
                break;
            case "イベント":
                embedBuilder.setColor(0xF156AE);
                break;
            case "キャンペーン":
                embedBuilder.setColor(0x7B50D4);
                break;
            case "メディア":
                embedBuilder.setColor(0x9F3232);
                break;
        }

        return embedBuilder;
    }
}
package com.infinite.virtualmusicplayer.receivers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LyricsFinder {

    private static final int MIN_LENGTH = 100;

    public static String find(String title, String artist) {
        List<String> urls = searchGoogle(title + artist + " lyrics");
        for (String url : urls) {
            String lyrics = extractLyrics(url);
            if (lyrics != null && lyrics.length() > MIN_LENGTH) {
                return lyrics;
            }
        }
        return null;
    }

    private static List<String> searchGoogle(String query) {
        List<String> resultUrls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.google.com/search?q=" + query + "jiosaavan" ).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                if (href.startsWith("http") && !href.contains("google.com")) {
                    resultUrls.add(href);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultUrls;
    }

    private static String extractLyrics(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("p[class*=u-margin-bottom-none@sm] span");
            StringBuilder lyrics = new StringBuilder();
            for (Element element : elements) {
                // Append text of the span
                lyrics.append(element.text());
                // Add a new line after every span tag
                lyrics.append("\n");
            }
            return lyrics.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
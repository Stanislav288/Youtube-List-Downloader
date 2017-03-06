package app;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;


import javax.script.ScriptException;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String YOUTUBE_VIDEOS_ROOT_URL = "https://www.youtube.com/watch?v=";

    private static final String PLAYLIST_VIDEO_PATTERN = "data-video-id=\"(.+?)\"";

    private static final String YOUTUBE_MP3_CONVERTER = "http://www.youtube-mp3.org/";

    public static void main(String[] args) throws IOException {
        ApplicationMenu.launch();

       // String urlSourceCode=getUrlSourceCode(scanIn.nextLine());
        //String urlSourceCode = getUrlSourceCode("https://www.youtube.com/playlist?list=PLIqrg0Qwbpopx_wfXRWrQeAqmLtyxPrKk");

        Pattern pattern = Pattern.compile(PLAYLIST_VIDEO_PATTERN);
        //Matcher matcher = pattern.matcher(readFile(FILE_PATH));
        Matcher matcher = pattern.matcher(urlSourceCode);

        List<String> videos = new LinkedList<>();
        while (matcher.find()) {
            videos.add(YOUTUBE_VIDEOS_ROOT_URL + matcher.group(1));
        }

        try {
            submitVideos(videos);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getUrlSourceCode(String urlString) {
        StringBuilder a = new StringBuilder();
        try {
            URL url = new URL(urlString);
            URLConnection connection = null;
            connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a.toString();
    }



    private static void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void submitVideos(List<String> videos) throws ScriptException, IOException, InterruptedException {
        // turn off htmlunit warnings
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);


        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());

        int position = 1;
        for (String video : videos) {

            HtmlPage page = webClient.getPage(YOUTUBE_MP3_CONVERTER);
            HtmlTextInput searchBox = (HtmlTextInput) page.getElementById("youtube-url");
            HtmlSubmitInput convertVideoButtonButton =
                    (HtmlSubmitInput) page.getElementById("submit");

            searchBox.setValueAttribute(video);

            page = convertVideoButtonButton.click();

            HtmlDivision resultStatsDiv = (HtmlDivision) page.getElementById("dl_link");
            webClient.waitForBackgroundJavaScript(1000);

            if (resultStatsDiv == null) {
                webClient.waitForBackgroundJavaScript(400);
                page = webClient.getPage(YOUTUBE_MP3_CONVERTER);
                resultStatsDiv = (HtmlDivision) page.getElementById("dl_link");
            }

            DomNodeList<DomNode> domElements = resultStatsDiv.getChildNodes();
            String downloadUrl = getTheLongestUrl(domElements);

            openUrl(YOUTUBE_MP3_CONVERTER + downloadUrl);

            System.out.println(position + ". " + YOUTUBE_MP3_CONVERTER + downloadUrl);
            position++;

        }

        webClient.close();
    }

    ////The press of the button produce several url, the one of the song is the longest one
    private static String getTheLongestUrl(DomNodeList<DomNode> domElements) {
        String longestUrl = "";
        HtmlAnchor htmlAnchor = null;
        for (DomNode domElement : domElements) {
            htmlAnchor = (HtmlAnchor) domElement;

            String url = htmlAnchor.getHrefAttribute();
            if (url.length() > longestUrl.length()) {
                longestUrl = url;
            }
        }

        return longestUrl;
    }
}

package org.ketul;

import com.google.common.collect.Lists;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String USER_AGENT = "Mozilla/5.0";

    private static int sendGET(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setReadTimeout(10000);
            con.setConnectTimeout(10000);

            if (con.getResponseCode() == 301 || con.getResponseCode() == 302) {
                String location = con.getHeaderField("Location");
                System.out.println("Redirected URL : - " + location);
                return sendGET(location);
            } else
                return con.getResponseCode();

        } catch (Exception e) {
            return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("urls.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;

        List<String> list = new ArrayList<>();

        while ((st = br.readLine()) != null)
            list.add(st);

        List<List<String>> partition = Lists.partition(list, 10);
        System.out.println(partition.size());
        multi(partition);
    }

    public static void multi(List<List<String>> list) {
        for (int k = 0; k < list.size(); k++) {
            String fileName = "file_" + k + ".txt";
            List<String> innerList = list.get(k);
            Thread thread = new Thread(
                    () -> {
                        File file = new File(fileName);

                            try {
                                file.createNewFile();
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                                for (String s1 : innerList) {
                                    String[] stringList = s1.trim().split(" ");
                                    System.out.println(s1);
                                    switch (stringList.length) {
                                        case 1:
                                            writer.write(stringList[0] + "\n");
                                            writer.flush();
                                            break;

                                        case 2:
                                            int status = sendGET(stringList[1]);
                                            writer.write(stringList[0] + " | " + stringList[1] + " | " + status + "\n");
                                            writer.flush();
                                            break;

                                        default:
                                            System.out.println("NO");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Thread Info : - " + Thread.currentThread().getName() + " --- " + LocalDateTime.now());
                        });
            thread.start();
        }

    }
}

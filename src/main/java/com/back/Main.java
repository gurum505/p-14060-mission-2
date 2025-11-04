package com.back;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        class Wise {
            static int lastNo = 0;
            String author;
            String content;
            final int no;

            public Wise(String author, String content, int no) {
                this.author = author;
                this.content = content;
                this.no = no;
            }

            public static void setLastNo(int lastNo){
                Wise.lastNo = lastNo;
            }

            public static int getLastNo(){
                return Wise.lastNo;
            }

            @Override
            public String toString() {
                return "{author: " + this.author + " content: " + this.content + " id: " + this.no + "}";
            }
        }

        class FileIO{
            public void saveFile(Wise w){
                String stringJson = wiseToJson(w);
                String fileName = "db/wiseSaying/" + w.no + ".json";

                //fIXME: encoding문제 outputStreamWriter사용?
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
                    bw.write(stringJson);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private String wiseToJson(Wise w){
                return "{\n" +
                        "\t\"id\": " + w.no + ",\n" +
                        "\t\"content\": \"" + w.content + "\",\n" +
                        "\t\"author\": \"" + w.author + "\"\n" +
                        "}";
            }

            public void saveLastId(){
                if (Wise.lastNo == 0){
                    return;
                }
                String fileName = "db/wiseSaying/lastId.txt";

                //fIXME: encoding문제 outputStreamWriter사용?
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
                    bw.write(String.valueOf(Wise.lastNo));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            public TreeMap<Integer, Wise> readFiles(){
                TreeMap<Integer, Wise> originWiseList = new TreeMap<>();
                String pathName = "db/wiseSaying";
                File directory = new File(pathName);
                //todo:Optional사용해서 null지옥빠져나가기?
                if (directory.isDirectory()){
                    String[] fileNames = directory.list();
                    if (fileNames != null){
                        for(String fileName:fileNames){
                            Wise w = readFile(pathName+"/"+fileName);
                            if (w != null){
                                originWiseList.put(-w.no,w);
                            }

                        }
                    }
                }
                if (new File(pathName +"/"+"lastId.txt").exists()){
                    readLastId(pathName +"/"+"lastId.txt");
                }


                return originWiseList;
            }

            private Wise readFile(String fileName){
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    String line;
                    HashMap<String,String> keyValue = new HashMap<>();
                    while((line = br.readLine()) != null) {
                        String[] temp = line.split(":");
                        if (temp.length >= 2){
                            //fixme:regex사용
                            //따옴표와쉼표 제거하고 map에 추가
                            String key = temp[0].trim().substring(1,temp[0].length()-2);
                            if (key.equals("id")){
                                String value = temp[1].trim().substring(0,temp[1].length()-2);
                                keyValue.put(key,value);
                            }
                            else if(key.equals("content")) {
                                String value = temp[1].trim().substring(1,temp[1].length()-3);
                                keyValue.put(key,value);
                            }
                            else if (key.equals("author")){
                                String value = temp[1].trim().substring(1,temp[1].length()-2);
                                keyValue.put(key,value);
                            }
                        }
                    }

                    if (!keyValue.isEmpty() && keyValue.size() >= 3){
                        //System.out.println(keyValue);
                        return new Wise(
                                keyValue.get("author"),
                                keyValue.get("content"),
                                Integer.parseInt(keyValue.get("id"))
                                );
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            private void readLastId(String fileName){
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
                    String line;
                    while((line = br.readLine()) != null){
                        int lastNo = Integer.parseInt(line);
                        Wise.setLastNo(lastNo);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            public void build(TreeMap<Integer,Wise> wizeList){
                try(BufferedWriter bw = new BufferedWriter(new FileWriter("data.json"))){
                    StringBuffer stringJson = new StringBuffer("[\n");
                    for(Map.Entry<Integer,Wise> entry: wizeList.entrySet()){
                        stringJson.append(wiseToJson(entry.getValue()));
                        stringJson.append(",\n");
                    }
                    stringJson.delete(stringJson.length()-2,stringJson.length()-1);
                    stringJson.append("\n]");
                    bw.write(String.valueOf(stringJson));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


        }

        System.out.println("== 명언 앱 ==");
        Scanner sc = new Scanner(System.in);

        FileIO fileIO = new FileIO();

        TreeMap<Integer, Wise> wiseList = fileIO.readFiles();

        while (true) {
            System.out.print("명령) ");
            String input = sc.nextLine();

            HashMap<String,String> queryMap = new HashMap<>();
            
            //todo: 유효치 않은 문자열을 받았을 때 무시하도록 수정필요.
            String[] cmdQuery = input.split("\\?");
            String cmd = cmdQuery[0];
            if (cmdQuery.length >= 2){
                String[] queries = cmdQuery[1].split("&");
                for (String query : queries){
                    String[] keyValue = query.split("=");
                    if (keyValue.length == 2){
                        queryMap.put(keyValue[0],keyValue[1]);
                    }
                }
            }

            if (cmd.equals("등록")) {
                System.out.print("명언 : ");
                String content = sc.nextLine();

                System.out.print("작가 : ");
                String author = sc.nextLine();

                Wise.lastNo += 1;
                //내림차순 목록을 위해 음수로 저장
                wiseList.put(-Wise.lastNo,new Wise(author, content, Wise.lastNo));

                System.out.println(Wise.lastNo + "번 명언이 등록되었습니다.");

            }
            else if (cmd.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");

                //todo: map.foreach
                for (Map.Entry<Integer, Wise> entry: wiseList.entrySet())
                {
                    Wise w = entry.getValue();
                    System.out.println(
                                w.no + " / " +
                                w.author + " / " +
                                w.content
                    );
                }

            }
            else if (cmd.startsWith("삭제") && cmdQuery.length >= 2) {
                int no = Integer.parseInt(queryMap.get("id"));

                if (no > Wise.lastNo || wiseList.get(-no) == null) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                } else {
                    wiseList.remove(-no);
                    System.out.println(no + "번 명언이 삭제되었습니다.");
                }

            }
            else if (cmd.startsWith("수정") && cmdQuery.length >= 2) {
                int no = Integer.parseInt(queryMap.get("id"));

                if (no > Wise.lastNo || wiseList.get(-no) == null) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                } else {
                    Wise origin = wiseList.get(-no);

                    System.out.println("명언(기존) : " + origin.content);
                    System.out.print("명언 : ");
                    origin.content = sc.nextLine();

                    System.out.println("작가(기존) : " + origin.author);
                    System.out.print("작가 : ");
                    origin.author = sc.nextLine();
                }

            }
            else if (cmd.equals("빌드")) {
                fileIO.build(wiseList);
                System.out.println("data.json파일의 내용이 갱신되었습니다.");
            }
            else if (cmd.equals("종료")) {
                //directory비우기

                File directory = new File("db/wiseSaying");

                if (directory.exists()){
                    File[] files = directory.listFiles();
                    if(files != null){
                        for(File file:files){
                            System.out.println(file.getName());
                            file.delete();
                        }
                    }
                }

                for (Map.Entry<Integer,Wise> entry: wiseList.entrySet()){
                    fileIO.saveFile(entry.getValue());
                }
                fileIO.saveLastId();


                System.out.println("성공적으로 종료되었습니다.");
                break;
            }

        }

        sc.close();
    }
}
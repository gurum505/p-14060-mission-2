package com.back;

import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        class wise {
            static int lastno = 0;
            String author;
            String content;
            final int no;


            public wise(String author, String content, int no) {
                this.author = author;
                this.content = content;
                this.no = no;
            }
        }

        System.out.println("== 명언 앱 ==");
        Scanner sc = new Scanner(System.in);

        TreeMap<Integer,wise> wiseList = new TreeMap<>();

        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine();

            if (cmd.equals("등록")) {
                System.out.print("명언 : ");
                String content = sc.nextLine();

                System.out.print("작가 : ");
                String author = sc.nextLine();

                wise.lastno += 1;
                //내림차순 목록을 위해 음수로 저장
                wiseList.put(-wise.lastno,new wise(author, content,wise.lastno));

                System.out.println(wise.lastno + "번 명언이 등록되었습니다.");

            } else if (cmd.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");

                //todo: map.foreach
                for (Map.Entry<Integer,wise> entry: wiseList.entrySet())
                {
                    wise w = entry.getValue();
                    System.out.println(
                                w.no + " / " +
                                w.author + " / " +
                                w.content
                    );
                }

            } else if (cmd.startsWith("삭제")) {
                //fixme: check
                //목록?keywordType=author&keyword=작자
                String query = cmd.substring(3);
                //Map<String,Integer> params = new HashMap<>();
                int no = Integer.parseInt(query.substring(3));

                if (no > wise.lastno || wiseList.get(-no) == null) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                } else {
                    wiseList.remove(-no);
                    System.out.println(no + "번 명언이 삭제되었습니다.");
                }

            } else if (cmd.startsWith("수정")) {
                //fixme: check
                String query = cmd.substring(3);
                int no = Integer.parseInt(query.substring(3));

                if (no > wise.lastno || wiseList.get(-no) == null) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                } else {
                    wise origin = wiseList.get(-no);

                    System.out.println("명언(기존) :" + origin.content);
                    System.out.print("명언 : ");
                    origin.content = sc.nextLine();

                    System.out.println("작가(기존) :" + origin.author);
                    System.out.print("작가 : ");
                    origin.author = sc.nextLine();
                }

            } else if (cmd.equals("종료")) {
                break;
            }


        }
    }
}
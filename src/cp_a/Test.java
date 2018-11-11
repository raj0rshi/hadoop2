/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cp_a;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author rajorshi
 */
public class Test {

    public static void main(String[] args) throws FileNotFoundException {

        File folder = new File("/home/rajorshi/Downloads/SmallDataset");
        File[] listOfFiles = folder.listFiles();
        HashMap<Long, JSONObject> CommentsMap = new HashMap<Long, JSONObject>();
        int commentcount=0;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                //    System.out.println("File " + listOfFiles[i].getAbsolutePath());

                Scanner scn = new Scanner(listOfFiles[i]);
                if (scn.hasNext()) {
                    scn.nextLine();
                }
                String Line = "";

                while (scn.hasNext()) {
                    Line = scn.nextLine();

                    JSONParser parser = new JSONParser();
                    try {

                        JSONObject obj = (JSONObject) parser.parse(Line);

                        JSONObject payload = (JSONObject) obj.get("payload");
                        //System.out.println(payload);

                        JSONArray pages = (JSONArray) payload.get("page");

                        for (int j = 0; j < pages.size(); j++) {
                            JSONObject comment = (JSONObject) pages.get(j);
                            // System.out.println(comment);

                            long id = (long) comment.get("id");
                            // System.out.println(id);
                            CommentsMap.put(id, comment);
                            commentcount++;

                        }

                    } catch (ParseException pe) {

                        System.out.println(listOfFiles[i].getAbsolutePath());
                        //  System.out.println("position: " + pe.getPosition());
                        pe.printStackTrace();
                        System.out.println(Line);
                        System.exit(1);
                    }

                }
            }

        }
        
        //output
        
        File output=new File("output.txt");
        PrintWriter pw=new PrintWriter(output);
        for(JSONObject comment: CommentsMap.values())
        {
            pw.println(comment.toJSONString());
        }
        pw.close();
        System.out.println("comments:"+ commentcount);
        System.out.println("unique comments:"+ CommentsMap.size());

    }

}

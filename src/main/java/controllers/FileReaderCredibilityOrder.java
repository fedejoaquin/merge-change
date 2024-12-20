package controllers;

import sets.CredibilityBase;
import sets.CredibilityElement;
import sets.CredibilityOrder;

import java.io.*;
import java.util.Set;

public class FileReaderCredibilityOrder {

    public static CredibilityBase<Integer,Integer> processCredibilityBaseFromFile(String filePathA, String filePathB){
        CredibilityBase<Integer, Integer> cb = new CredibilityBase<Integer, Integer>();
        if(readFile(filePathA, cb)){
            if(filePathB!=null && !readFile(filePathB, cb)){
                return null;
            }
        }else{
            return null;
        }
        return cb;
    }

    private static boolean readFile(String csvFile, CredibilityBase<Integer, Integer> cb){
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            if(br.readLine().equals("context;credibility_order")) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    int context;
                    if(parts.length == 2){
                        try{
                            context = Integer.parseInt(parts[0]);
                        }catch(NumberFormatException e){
                            return false;
                        }
                    }else{
                        // File with wrong syntax
                        return false;
                    }

                    String[] prior = parts[1].split(",");
                    for(String p: prior){
                        String[] credibilityElement = p.split(">");
                        if (credibilityElement.length == 2) {
                            int morePriorAgent;
                            int lessPriorAgent;
                            try{
                                morePriorAgent = Integer.parseInt(credibilityElement[0].trim().toLowerCase());
                                lessPriorAgent = Integer.parseInt(credibilityElement[1].trim().toLowerCase());
                            }catch(NumberFormatException e){
                                return false;
                            }

                            cb.addCredibilityObject(morePriorAgent, lessPriorAgent, context);

                        }else {
                            // File with wrong syntax
                            return false;
                        }
                    }
                }
            }else {
                // File with wrong syntax
                return false;
            }
        }catch (IOException e) {
            return false;
        }

        return true;
    }

    public static void writeCredibilityBaseFromData(String folderPath, String name, Set<CredibilityOrder<Integer, Integer>> credibilityOrders){
        String fileName = name+".csv";
        String filePath = folderPath+"\\"+fileName;

        createFile(filePath, credibilityOrders);
    }

    private static void createFile(String filePath, Set<CredibilityOrder<Integer, Integer>> credibilityOrders){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("context;credibility_order");
            writer.newLine();

            int context;
            for(CredibilityOrder<Integer, Integer> co : credibilityOrders) {
            	context = co.getID();
            	writer.write(context+";");
            	for(CredibilityElement<Integer> ce : co.relationSet()) {
            		writer.write(ce.getMostCredible()+">"+ce.getLessCredible());
                    writer.write(",");
            	}
            	writer.newLine();
                context++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

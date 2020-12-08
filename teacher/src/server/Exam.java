package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Exam {

    ArrayList<String> questions = new ArrayList<>();
    ArrayList<ArrayList<String>> choices = new ArrayList<>();
    ArrayList<Integer> answers = new ArrayList<>();

    public Exam(String filepath){
        read_csv(filepath);
    }

    private void read_csv(String filepath){
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(filepath));
            String row = csvReader.readLine();
            while (row != null) {
                String[] data = row.split(";");
                questions.add(data[0]);
                choices.add(new ArrayList<>(Arrays.asList(data).subList(1, data.length - 1)));
                String[] answer = data[data.length - 1].split("\\.");
                answers.add(Integer.parseInt(answer[0]));
                row = csvReader.readLine();
            }
            csvReader.close();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
}

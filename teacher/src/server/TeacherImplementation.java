package server;
import common.ServerInterface;
import common.ClientInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import okhttp3.*;


public class TeacherImplementation extends UnicastRemoteObject implements ServerInterface {

    final Map<ClientInterface, String> ids = new HashMap<>();
    final Map<ClientInterface, Integer> marks = new HashMap<>();
    final Exam exam;
    final Map<ClientInterface, Integer> progress = new HashMap<>();
    private boolean exam_on = false;

    public TeacherImplementation(String exam_path) throws RemoteException {
        this.exam = new Exam(exam_path);
        String url_get = "http://127.0.0.1:8000/api/exams/?search=" + exam.name;
        if(GET_empty_body(url_get)){
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\n    \"title\": \""+ exam.name +"\",\n    \"description\": \""+ exam.description +"\",\n    \"date\": \""+exam.date+"\",\n    \"time_start\": \""+ exam.time_start+"\",\n    \"time_end\": \""+exam.time_finish+"\",\n    \"location\": \""+ exam.location +"\"\n}");
            String url_post = "http://127.0.0.1:8000/api/exams/";
            sendPOST(body, url_post);
        }
    }


    @Override
    public void register(ClientInterface student, String id, String name) throws RemoteException{
        synchronized (Server.class) {
            Server.class.notify();
        }
        String url = "http://127.0.0.1:8000/api/students/" + id;
        if(exam_on){
            student.denyConnection("Exam has already started.");
        }
        if(GET_empty_body(url)){
            student.denyConnection("You are not registered on the WS.");
        }
        if (!ids.containsValue(id)){
            this.ids.put(student, id);
            this.marks.put(student, 0);
            this.progress.put(student, 0);
            System.out.println("Student with id: " + id + " has been registered." + " There are " +
                        marks.size() + " students in the room.");
            student.sendMessage("You have correctly registered. Wait for the exam to start.");
        }else{
            System.err.println("Denying connection to student with id " + id + "");
            student.denyConnection("A student with this id has already been registered.");
        }
    }

    @Override
    public void receiveAnswer(ClientInterface student, String answer) throws RemoteException{
        String id = ids.get(student);
        int guess = Integer.parseInt(answer);
        int question_index = progress.get(student);
        if(question_index == exam.questions.size()){
            give_mark(student);
        }else{
            if(guess < 0 || guess > exam.choices.get(question_index).size()) {
                student.sendMessage("Please enter a valid number.");
            } else{
                if(exam.answers.get(question_index) == guess){
                    marks.put(student, marks.get(student) + 1);
                }
                System.out.println("Student " + id + " answered with " + answer);
                sendQuestion(student);
            }
        }
    }


    public void sendQuestion(ClientInterface student) throws RemoteException{
        Integer question_index = progress.get(student);
        ArrayList<String> choices = exam.choices.get(question_index);
        student.sendMessage("Enter a number from 1 to " + choices.size() + " to answer question nº " + (question_index + 1) + "");
        student.sendMessage(exam.questions.get(question_index));
        for (String choice : choices) {
            student.sendMessage(choices.indexOf(choice) + 1 + "): " + choice);
        }
        progress.put(student, question_index + 1);

    }


    public void give_mark(ClientInterface student) throws RemoteException{
        synchronized (Server.class){
            Server.class.notify();
        }
        int correct = marks.get(student);
        int total = exam.questions.size();
        student.give_mark(correct, total);
    }

    public void write_results(){
        try {
            PrintWriter writer = new PrintWriter("results.txt", StandardCharsets.UTF_8);
            writer.println("<Student_id>: <mark>");
            for(ClientInterface s: marks.keySet()){
                String id = ids.get(s);
                String line = id + ": " + marks.get(s);
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void notifyStart(){
        ArrayList<ClientInterface> error_clients = new ArrayList<>();
        for (ClientInterface s : marks.keySet()) {
            try{
                s.notifyStart();
            }catch(RemoteException e){
                System.out.println("Client is not reachable");
                error_clients.add(s);
            }
        }
        for(ClientInterface s: error_clients){
            this.marks.remove(s);
        }
    }

    public void start_exam(){
        System.out.println("The exam has started, students are being notified.");
        exam_on = true;
    }

    private void sendPOST(RequestBody body, String url){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.code() == 201){
                System.out.println("POST completed successfully.");
            }else{
                System.out.println("POST didn't work.");
                System.out.println(response.code());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean GET_empty_body(String url){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String response_body = response.body().string();
            System.out.println(response_body);
            if(response_body.equals("[]")){
                System.out.println("GET had empty body.");
                return true;
            }else{
                System.out.println("GET didn't have empty body.");
                System.out.println(response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

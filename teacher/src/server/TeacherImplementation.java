package server;
import common.ServerInterface;
import common.ClientInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class TeacherImplementation extends UnicastRemoteObject implements ServerInterface {
    public TeacherImplementation() throws RemoteException {}

    final Set<String> ids = new HashSet<>();
    final Map<ClientInterface, Integer> marks = new HashMap<>();
    final Exam exam = new Exam("/home/gfelis/udl-exam-RMI/test/exam2.csv");
    final Map<ClientInterface, Integer> progress = new HashMap<>();
    private boolean exam_on = false;

    @Override
    public void register(ClientInterface student) throws RemoteException{
        synchronized (Server.class) {
            Server.class.notify();
        }
        String id = student.getId();
        if(exam_on){
            student.denyConnection("Exam has already started.");
        }
        if (!ids.contains(id)){
            this.ids.add(id);
            this.marks.put(student, 0);
            this.progress.put(student, 0);
            System.out.println("Student with id: " + id + " has been registered." + " There are " +
                    marks.size() + " students in the room.");
            student.sendMessage("You have correctly registered. Wait for the exam to start.");
        }else{
            System.err.println("Denying connection to student with id " + id + ".");
            student.denyConnection("A student with this id has already been registered.");
        }
    }

    @Override
    public void receiveAnswer(ClientInterface student, String answer) throws RemoteException{
        String id = student.getId();
        int guess = Integer.parseInt(answer);
        int question_index = progress.get(student);
        if(guess < 0 || guess > exam.choices.get(question_index).size()){
            student.sendMessage("Please enter a valid number.");
        }else{
            if(progress.get(student) == exam.questions.size()){
                give_mark(student);
            }else{
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
        student.sendMessage("Enter a number from 1 to " + choices.size() + " to answer question nº " + (question_index + 1) + ".");
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
                String id = s.getId();
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
}

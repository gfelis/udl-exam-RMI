package server;
import common.ServerInterface;
import common.ClientInterface;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TeacherImplementation extends UnicastRemoteObject implements ServerInterface {
    public TeacherImplementation() throws RemoteException {}

    private final ArrayList<ClientInterface> students = new ArrayList<>();
    private final Map<String, Integer> marks = new HashMap<>();
    private final Exam exam = new Exam("/home/gfelis/udl-exam-RMI/teacher/exam.csv");
    private boolean exam_on = false;

    public synchronized void register(ClientInterface student) throws RemoteException{
        String id = student.getId();
        if(exam_on){
            student.denyConnection("Exam has already started.");
        }
        if (!marks.containsKey(id)){
            this.students.add(student);
            this.marks.put(student.getId(), 0);
            System.out.println("Student with id: " + id + " has been registered." + " There are " +
                    marks.size() + " students in the room.");
            student.sendMessage("You have correctly registered. Wait for the exam to start.");
        }else{
            System.err.println("Denying connection to student with id " + id + ".");
            student.denyConnection("A student with this id has already been registered.");
        }
    }

    public void start_exam(){
        System.out.println("The exam has started, students are being notified.");
        exam_on = true;
        for (ClientInterface s: this.students){
            try{
                s.sendMessage("Exam is starting.");
            } catch(RemoteException e){
                System.out.println("Error in notifying."); e.printStackTrace();
            }
        }
    }
}

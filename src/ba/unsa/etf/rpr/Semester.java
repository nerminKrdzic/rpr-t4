package ba.unsa.etf.rpr;

import java.util.ArrayList;

public class Semester {
    private ArrayList<Student> students =  new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();
    Integer indexCounter = 15000;
    public Semester(ArrayList<Tuple<String, String, Integer, Class>> subjects)throws IllegalArgumentException, NotEnoughPoints{
        if(subjects == null) throw new IllegalArgumentException();
        int sum = 0;
        ArrayList<Subject> newSubjects = new ArrayList<>();
        for(Tuple<String, String, Integer, Class> t : subjects) {
            sum += t.getItem3();
            if(t.getItem4() == Obligatory.class)
                newSubjects.add(new Obligatory(t.getItem1(), t.getItem2(), t.getItem3()));
            else newSubjects.add(new Electoral(t.getItem1(), t.getItem2(), t.getItem3()));
        }
        if(sum < 30) throw new NotEnoughPoints();
        this.subjects = newSubjects;
    }

    public String getElectoralSubjects(){
        String result = new String();
        for(int i = 0; i < this.subjects.size(); i++){
            if(this.subjects.get(i) instanceof Electoral){
                result += Integer.toString(i + 1) + ". subjectName: " + this.subjects.get(i).getSubjectName() + ", responsibleTeacher: "
                        + this.subjects.get(i).getResponsibleTeacher() + ", ECTS: " + Integer.toString(this.subjects.get(i).getNumberOfECTSPoints()) + "\n";
            }
        }
        return result;
    }
    public String getObligatorySubjects(){
        String result = new String();
        for(int i = 0; i < this.subjects.size(); i++){
            if(this.subjects.get(i) instanceof Obligatory){
                result += Integer.toString(i + 1) + ". subjectName: " + this.subjects.get(i).getSubjectName() + ", responsibleTeacher: "
                        + this.subjects.get(i).getResponsibleTeacher() + ", ECTS: " + Integer.toString(this.subjects.get(i).getNumberOfECTSPoints()) + "\n";
            }
        }
        return result;
    }
    public void enrollStudent(Student student, ArrayList<String> electoralSubjects) throws NotEnoughPoints{
        boolean ima = false; // check if electorals are correct
        for(String electoral : electoralSubjects) {
            ima = false;
            for (Subject s : this.subjects) {
                if (electoral.equalsIgnoreCase(s.getSubjectName())){
                    ima = true;
                    break;
                }
            }
        }
        if(!ima) throw new IllegalArgumentException();
        for(Subject s : this.subjects){ // enroll student in subjects
            if(s instanceof Obligatory) s.enrollStudent(student);
            else {
                for(String electoral : electoralSubjects)
                    if(electoral.equalsIgnoreCase(s.getSubjectName())){
                        s.enrollStudent(student);
                        break;
                    }
            }
        }
        this.students.add(student); // add student to the semestar
        student.setIndex(indexCounter++);
        if(student.getNumberOfECTSPoints() < 30){ // if not enugh ECTS delete student
            deleteStudent(student.getIndex());
            indexCounter--;
            throw new NotEnoughPoints();
        }
    }
    public void deleteStudent(Integer index){
        for(Subject subject : this.subjects)
            subject.deleteStudent(index);
        for(int i = 0; i < this.students.size(); i++){
            if(this.students.get(i).getIndex().equals(index)){
                this.students.remove(i);
                return;
            }
        }
    }
    public void addSubject(Subject subject){
        for(Subject s : this.subjects)
            if(s.getSubjectName().equalsIgnoreCase(subject.getSubjectName()))
                throw new IllegalArgumentException();
        this.subjects.add(subject);
    }
    public void deleteSubject(String subjectName){
        Subject subject = null;
        for(Subject s : this.subjects)
            if(s.getSubjectName().equalsIgnoreCase(subjectName)){
                subject = s;
                break;
            }
        if(subject == null) throw new IllegalArgumentException();
        this.subjects.remove(subject);
        for(Student s : this.students){
            subject.deleteStudent(s.getIndex());
            if(s.getNumberOfECTSPoints() < 30)
                deleteStudent(s.getIndex());
        }
    }
}

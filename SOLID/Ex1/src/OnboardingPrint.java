import java.util.List;

public class OnboardingPrint {
    public void printSuccess(StudentRecord r,int total){
        System.out.println("OK: created student " + r.id);
        System.out.println("Saved. Total students: " + total);
        System.out.println("CONFIRMATION:");
        System.out.println(r);
    }

    public void printErrors(List<String> errors){
           if (!errors.isEmpty()) {
            System.out.println("ERROR: cannot register");
            for (String e : errors) System.out.println("- " + e);
            return;
        }
    }

    public void printInput(String raw){
         System.out.println("INPUT: " + raw);
         return;
    }
    
}

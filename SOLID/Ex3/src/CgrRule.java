public class CgrRule implements Rules {
    public String validate(StudentProfile s) {
       if (s.cgr < 8.0) {
           return "CGR below 8.0";
       }
       return null;
   }
}
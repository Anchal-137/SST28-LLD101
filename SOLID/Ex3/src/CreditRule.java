public class CreditRule implements Rules{
    public String validate(StudentProfile s) {
        if (s.earnedCredits < 20) {
            return "credits below 20";
        }
        return null;
    }
}
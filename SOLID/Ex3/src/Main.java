import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Placement Eligibility ===");

        FakeEligibilityStore store=new FakeEligibilityStore();

         List<Rules> rules = Arrays.asList(
                new DisciplinaryRule(),
                new CgrRule(),
                new AttendanceRule(),
                new CreditRule()
        );

        EligibilityEngine engine = new EligibilityEngine(store, rules);

        StudentProfile s = new StudentProfile("23BCS1001", "Ayaan", 8.10, 72, 18, LegacyFlags.NONE);
        engine.runAndPrint(s);
    }
}
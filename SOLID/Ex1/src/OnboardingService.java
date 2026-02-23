import java.util.*;

public class OnboardingService {
    private final StudentParse parser;
    private final StudentValidator validator;
    private final StudentRepository repo;
    private final OnboardingPrint printer;

    

    public OnboardingService(StudentParse parser, StudentValidator validator, StudentRepository repo, OnboardingPrint printer) {
        this.parser = parser;
        this.validator = validator; 
        this.repo = repo;
        this.printer = printer;
    }

    public void registerFromRawInput(String raw) {
        printer.printInput(raw);

        Map<String,String> kv= parser.parse(raw);

        List<String> errors = validator.validate(kv);

        if (!errors.isEmpty()) {
            System.out.println("ERROR: cannot register");
            for (String e : errors) System.out.println("- " + e);
            return;
        }

        String name = kv.getOrDefault("name", "");
        String email = kv.getOrDefault("email", "");
        String phone = kv.getOrDefault("phone", "");
        String program = kv.getOrDefault("program", "");

        String id = IdUtil.nextStudentId(repo.count());
        StudentRecord rec = new StudentRecord(id, name, email, phone, program);

        repo.save(rec);

        printer.printSuccess(rec, repo.count());
    }
}

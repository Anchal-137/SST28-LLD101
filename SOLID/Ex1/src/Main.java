public class Main {
    public static void main(String[] args) {

        StudentRepository repo = new FakeDb();
        StudentParse parser = new StudentParse();
        StudentValidator validator = new StudentValidator();
        OnboardingPrint printer = new OnboardingPrint();

        System.out.println("=== Student Onboarding ===");

        OnboardingService svc = new OnboardingService(parser,validator,repo,printer);

        String raw = "name=Riya;email=riya@sst.edu;phone=9876543210;program=CSE";
        svc.registerFromRawInput(raw);

        System.out.println();
        System.out.println("-- DB DUMP --");
        System.out.print(TextTable.render3(repo));
    }
}
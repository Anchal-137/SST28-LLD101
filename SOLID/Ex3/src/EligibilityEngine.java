
import java.util.*;

public class EligibilityEngine {
    private final FakeEligibilityStore store;
    private final List<Rules> rules;

    public EligibilityEngine(FakeEligibilityStore store, List<Rules> rules) { 
        this.store = store;
        this.rules=rules;
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityEngineResult r = evaluate(s); // giant conditional inside
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityEngineResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        String status = "NOT_ELIGIBLE";
        
        for(Rules rule : rules){
            String failure = rule.validate(s);
            if (failure != null) {
                reasons.add(failure);
                break;
            }
        }

        if(reasons.isEmpty()){
            status="ELIGIBLE";
        }

        return new EligibilityEngineResult(status, reasons);
    }
}

class EligibilityEngineResult {
    public final String status;
    public final List<String> reasons;
    public EligibilityEngineResult(String status, List<String> reasons) {
        this.status = status;
        this.reasons = reasons;
    }
}

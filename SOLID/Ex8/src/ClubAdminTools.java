// After ISP refactoring this fat interface is no longer used directly.
// Kept for reference — individual roles now depend on FinanceOperations,
// MinutesOperations, or EventOperations only.

public interface ClubAdminTools extends FinanceOperations, MinutesOperations, EventOperations {
    // aggregates all capabilities — no tool implements this anymore
}

import java.time.LocalDate;

public class Employee {
    private Long employeeId;
    private LocalDate dataStart;
    private LocalDate dataEnd;

    public Employee(Long employeeId, LocalDate dataStart, LocalDate dataEnd) {
        this.employeeId = employeeId;
        this.dataStart = dataStart;
        this.dataEnd = dataEnd;
    }

    public Long getEmployeeId() {
        return this.employeeId;
    }

    public LocalDate getDataStart() {
        return this.dataStart;
    }

    public LocalDate getDataEnd() {
        return this.dataEnd;
    }
}

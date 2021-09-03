import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Map<Integer, List<Employee>> projectIdToEmployeesMap = getProjectIdToEmployeesMap();

        Map<String, Period> teamNameToWorkingHoursMap = getTeamNameToWorkingHoursMap(projectIdToEmployeesMap);

        printTeamWithMostWorkingHoursForAllProjects(teamNameToWorkingHoursMap);

    }

    private static Map<Integer, List<Employee>> getProjectIdToEmployeesMap() {
        Map<Integer, List<Employee>> projectIdToEmployeesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("EmployeesInfo.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] info = line.split(",");

                Long empId = Long.parseLong(info[0].trim());
                int projectId = Integer.parseInt(info[1].trim());

                String dataStartAsString = info[2].trim();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate dataStart = LocalDate.parse(dataStartAsString, formatter);

                LocalDate dataEnd = getDataEnd(info[3], formatter);

                projectIdToEmployeesMap.putIfAbsent(projectId, new ArrayList<>());
                projectIdToEmployeesMap.get(projectId).add(new Employee(empId, dataStart, dataEnd));
            }
        } catch (IOException e) {
            System.out.println("Please add EmployeesInfo.csv in the application folder.");
            System.exit(0);
        }

        return projectIdToEmployeesMap;
    }

    private static Map<String, Period> getTeamNameToWorkingHoursMap(Map<Integer, List<Employee>> projectIdToEmployees) {
        Map<String, Period> teamNameToWorkingHoursMap = new LinkedHashMap<>();

        for (Map.Entry<Integer, List<Employee>> theBigMap : projectIdToEmployees.entrySet()) {

            List<Employee> listOfEmployees = theBigMap.getValue();

            if (listOfEmployees.size() > 1) {

                for (int i = 0; i < listOfEmployees.size(); i++) {
                    // findTeamsAndPutThemInMap
                    for (int j = i + 1; j < listOfEmployees.size(); j++) {

                        Employee employee1 = listOfEmployees.get(i);
                        Employee employee2 = listOfEmployees.get(j);

                        Long employee1Id = employee1.getEmployeeId();
                        Long employee2Id = employee2.getEmployeeId();

                        if (!(employee1Id.equals(employee2Id))) {

                            LocalDate emp1StartWorkingDate = employee1.getDataStart();
                            LocalDate emp1EndWorkingDate = employee1.getDataEnd();

                            LocalDate emp2StartWorkingDate = employee2.getDataStart();
                            LocalDate emp2EndWorkingDate = employee2.getDataEnd();

                            if (emp1StartWorkingDate.isBefore(emp2EndWorkingDate) &&
                                    emp1EndWorkingDate.isAfter(emp2StartWorkingDate)) {

                                LocalDate startDateOfWorkingTogether = getTheLatestDate(emp1StartWorkingDate, emp2StartWorkingDate);
                                LocalDate endDateOfWorkingTogether = getTheEarlierDate(emp1EndWorkingDate, emp2EndWorkingDate);
                                Period timeWorkingTogether = findPeriodBetweenDates(startDateOfWorkingTogether, endDateOfWorkingTogether);

                                String teamName = getTeamName(employee1Id, employee2Id);

                                teamNameToWorkingHoursMap.putIfAbsent(teamName, Period.of(0, 0, 0));
                                teamNameToWorkingHoursMap.put(teamName, teamNameToWorkingHoursMap.get(teamName).plus(timeWorkingTogether));
                            }
                        }
                    }
                }

            }
        }
        return teamNameToWorkingHoursMap;
    }

    private static LocalDate getTheEarlierDate(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    private static LocalDate getTheLatestDate(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }

    static Period findPeriodBetweenDates(LocalDate date1, LocalDate date2) {
        return Period.between(date1, date2);
    }

    private static String getTeamName(Long employee1Id, Long employee2Id) {
        String teamName;
        if (employee1Id < employee2Id) {
            teamName = employee1Id + " & " + employee2Id;
        } else {
            teamName = employee2Id + " & " + employee1Id;
        }
        return teamName;
    }

    private static LocalDate getDataEnd(String s, DateTimeFormatter formatter) {
        LocalDate dataEnd;
        try {
            String dataEndAsString = s.trim();
            dataEnd = LocalDate.parse(dataEndAsString, formatter);
        } catch (java.time.format.DateTimeParseException ex) {
            dataEnd = LocalDate.now();
        }
        return dataEnd;
    }

    private static void printTeamWithMostWorkingHoursForAllProjects(Map<String, Period> teamNameAndWorkingHours) {

        teamNameAndWorkingHours.entrySet().stream().sorted((a, b) -> {
            int sort = Long.compare(b.getValue().toTotalMonths(), a.getValue().toTotalMonths());
            if (sort == 0) {
                sort = Integer.compare(b.getValue().getDays(), a.getValue().getDays());
            }
            return sort;
        })
                .findFirst()
                .ifPresentOrElse(e -> System.out.printf(
                        "Employees with IDs %s have been working together for the longest time: %s",
                        e.getKey(), printPeriod(e.getValue())),
                        () -> System.out.println(
                                "There are no employees that have been working together on the same project at the same time."));

    }

    private static String printPeriod(Period period) {
        StringBuilder sb = new StringBuilder();

        period = period.normalized();

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if (years == 1) {
            sb.append(years).append(" year ");
        } else if (years > 1) {
            sb.append(years).append(" years ");
        }

        if (months == 1) {
            sb.append(months).append(" month ");
        } else if (months > 1) {
            sb.append(months).append(" months ");
        }

        if (!sb.isEmpty()) {
            sb.append("and ");
        }

        if (days == 1) {
            sb.append(days).append(" day.");
        } else if (days > 1) {
            sb.append(days).append(" days.");
        }

        return sb.toString();
    }


}

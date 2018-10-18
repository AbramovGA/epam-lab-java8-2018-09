package streams.part2.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("ConstantConditions")
class Exercise2 {

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, когда-либо работавших в этой компании].
     * <p>
     * Входные данные:
     * [
     * {
     * {Иван Мельников 30},
     * [
     * {2, dev, "EPAM"},
     * {1, dev, "google"}
     * ]
     * },
     * {
     * {Александр Дементьев 28},
     * [
     * {2, tester, "EPAM"},
     * {1, dev, "EPAM"},
     * {1, dev, "google"}
     * ]
     * },
     * {
     * {Дмитрий Осинов 40},
     * [
     * {3, QA, "yandex"},
     * {1, QA, "EPAM"},
     * {1, dev, "mail.ru"}
     * ]
     * },
     * {
     * {Анна Светличная 21},
     * [
     * {1, tester, "T-Systems"}
     * ]
     * }
     * ]
     * <p>
     * Выходные данные:
     * [
     * "EPAM" -> [
     * {Иван Мельников 30},
     * {Александр Дементьев 28},
     * {Дмитрий Осинов 40}
     * ],
     * "google" -> [
     * {Иван Мельников 30},
     * {Александр Дементьев 28}
     * ],
     * "yandex" -> [ {Дмитрий Осинов 40} ]
     * "mail.ru" -> [ {Дмитрий Осинов 40} ]
     * "T-Systems" -> [ {Анна Светличная 21} ]
     * ]
     */
    @Test
    void employersStuffList() {
        List<Employee> employees = getEmployees();

        Function<Employee, Set<String>> getEmployers = (employee) -> {
            Set<String> employers = new HashSet<>();
            employee.getJobHistory().forEach((job) -> employers.add(job.getEmployer()));
            return employers;
        };
        Function<Employee, Set<Person>> employeeToSetPerson = (Employee e) -> {
            Set<Person> set = new HashSet<Person>();
            set.add(e.getPerson());
            return set;
        };

        BinaryOperator<Set<Person>> mergeSets = (set1, set2) -> {
            Set<Person> result = new HashSet<>();
            result.addAll(set1);
            result.addAll(set2);
            return result;
        };

        Function<Entry<Set<String>, Set<Person>>, Stream<Entry<String, Set<Person>>>> flatMapEmployers = (entry) -> {
            Map<String, Set<Person>> result = new HashMap<>();
            entry.getKey().forEach((key) -> result.put(key, entry.getValue()));
            return result.entrySet().stream();
        };

        Map<String, Set<Person>> result = employees.stream()
                .collect(toMap(getEmployers, employeeToSetPerson, mergeSets))
                .entrySet()
                .stream()
                .flatMap(flatMapEmployers)
                .collect(toMap(Entry::getKey, Entry::getValue, mergeSets));


        assertThat(result, hasEntry((is("yandex")), contains(employees.get(2).getPerson())));
        assertThat(result, hasEntry((is("mail.ru")), contains(employees.get(2).getPerson())));
        assertThat(result, hasEntry((is("google")), containsInAnyOrder(employees.get(0).getPerson(), employees.get(1).getPerson())));
        assertThat(result, hasEntry((is("T-Systems")), containsInAnyOrder(employees.get(3).getPerson(), employees.get(5).getPerson())));
        assertThat(result, hasEntry((is("EPAM")), containsInAnyOrder(
                employees.get(0).getPerson(),
                employees.get(1).getPerson(),
                employees.get(4).getPerson(),
                employees.get(5).getPerson()))
        );
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, начавших свою карьеру в этой компании].
     * <p>
     * Пример.
     * <p>
     * Входные данные:
     * [
     * {
     * {Иван Мельников 30},
     * [
     * {2, dev, "EPAM"},
     * {1, dev, "google"}
     * ]
     * },
     * {
     * {Александр Дементьев 28},
     * [
     * {2, tester, "EPAM"},
     * {1, dev, "EPAM"},
     * {1, dev, "google"}
     * ]
     * },
     * {
     * {Дмитрий Осинов 40},
     * [
     * {3, QA, "yandex"},
     * {1, QA, "EPAM"},
     * {1, dev, "mail.ru"}
     * ]
     * },
     * {
     * {Анна Светличная 21},
     * [
     * {1, tester, "T-Systems"}
     * ]
     * }
     * ]
     * <p>
     * Выходные данные:
     * [
     * "EPAM" -> [
     * {Иван Мельников 30},
     * {Александр Дементьев 28}
     * ],
     * "yandex" -> [ {Дмитрий Осинов 40} ]
     * "T-Systems" -> [ {Анна Светличная 21} ]
     * ]
     */
    @Test
    void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();

        Function<Employee, String> getFirstEmployer = (employee) -> employee.getJobHistory().get(0).getEmployer();

        Function<Employee, Set<Person>> employeeToSetPerson = (Employee e) -> {
            Set<Person> set = new HashSet<Person>();
            set.add(e.getPerson());
            return set;
        };

        BinaryOperator<Set<Person>> mergeSets = (set1, set2) -> {
            Set<Person> result = new HashSet<>();
            result.addAll(set1);
            result.addAll(set2);
            return result;
        };

        Map<String, Set<Person>> result = employees.stream()
                .collect(toMap(getFirstEmployer, employeeToSetPerson, mergeSets));

        assertThat(result, hasEntry(is("yandex"), contains(employees.get(2).getPerson())));
        assertThat(result, hasEntry(is("T-Systems"), containsInAnyOrder(employees.get(3).getPerson(), employees.get(5).getPerson())));
        assertThat(result, hasEntry(is("EPAM"), containsInAnyOrder(
                employees.get(0).getPerson(),
                employees.get(1).getPerson(),
                employees.get(4).getPerson()
        )));
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> сотрудник, суммарно проработавший в ней наибольшее время].
     * Гарантируется, что такой сотрудник будет один.
     */
    @Test
    void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();

        Function<Employee, Set<String>> getEmployers = (employee) -> {
            Set<String> employers = new HashSet<>();
            employee.getJobHistory().forEach((job) -> employers.add(job.getEmployer()));
            return employers;
        };
        Function<Employee, Set<Employee>> employeeToSetEmployee = (Employee e) -> {
            Set<Employee> set = new HashSet<>();
            set.add(e);
            return set;
        };

        BinaryOperator<Set<Employee>> mergeSets = (set1, set2) -> {
            Set<Employee> result = new HashSet<>();
            result.addAll(set1);
            result.addAll(set2);
            return result;
        };

        Function<Entry<Set<String>, Set<Employee>>, Stream<Entry<String, Set<Employee>>>> flatMapEmployers = (entry) -> {
            Map<String, Set<Employee>> result = new HashMap<>();
            entry.getKey().forEach((key) -> result.put(key, entry.getValue()));
            return result.entrySet().stream();
        };

        BiFunction<Employee, String, Integer> getYearOnEmployers = (employee, employer) -> {
            int result = 0;
            for (JobHistoryEntry job : employee.getJobHistory()) {
                if (job.getEmployer().equals(employer))
                    result += job.getDuration();
            }
            return result;
        };

        Function<Entry<String, Set<Employee>>, Entry<String, Employee>> getMaxWorkingEmployee = (entry) -> {
            String employer = entry.getKey();
            Employee maxWorkingEmployee = entry.getValue().iterator().next();
            for (Employee employee : entry.getValue()) {
                if (getYearOnEmployers.apply(employee, employer) > getYearOnEmployers.apply(maxWorkingEmployee, employer))
                    maxWorkingEmployee = employee;
            }
            HashMap<String, Employee> temp = new HashMap<>();
            temp.put(employer, maxWorkingEmployee);
            return temp.entrySet().iterator().next();
        };

        Map<String, Person> collect = employees.stream()
                .collect(toMap(getEmployers, employeeToSetEmployee, mergeSets))
                .entrySet()
                .stream()
                .flatMap(flatMapEmployers)
                .collect(toMap(Entry::getKey, Entry::getValue, mergeSets))
                .entrySet()
                .stream()
                .map(getMaxWorkingEmployee)
                .collect(toMap(Entry::getKey, (entry) -> entry.getValue().getPerson()));

        assertThat(collect, hasEntry("EPAM", employees.get(4).getPerson()));
        assertThat(collect, hasEntry("google", employees.get(1).getPerson()));
        assertThat(collect, hasEntry("yandex", employees.get(2).getPerson()));
        assertThat(collect, hasEntry("mail.ru", employees.get(2).getPerson()));
        assertThat(collect, hasEntry("T-Systems", employees.get(5).getPerson()));
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("Иван", "Мельников", 30),
                        Arrays.asList(
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Александр", "Дементьев", 28),
                        Arrays.asList(
                                new JobHistoryEntry(1, "tester", "EPAM"),
                                new JobHistoryEntry(2, "dev", "EPAM"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("Дмитрий", "Осинов", 40),
                        Arrays.asList(
                                new JobHistoryEntry(3, "QA", "yandex"),
                                new JobHistoryEntry(1, "QA", "mail.ru"),
                                new JobHistoryEntry(1, "dev", "mail.ru")
                        )),
                new Employee(
                        new Person("Анна", "Светличная", 21),
                        Collections.singletonList(
                                new JobHistoryEntry(1, "tester", "T-Systems")
                        )),
                new Employee(
                        new Person("Игорь", "Толмачёв", 50),
                        Arrays.asList(
                                new JobHistoryEntry(5, "tester", "EPAM"),
                                new JobHistoryEntry(6, "QA", "EPAM")
                        )),
                new Employee(
                        new Person("Иван", "Александров", 33),
                        Arrays.asList(
                                new JobHistoryEntry(2, "QA", "T-Systems"),
                                new JobHistoryEntry(3, "QA", "EPAM"),
                                new JobHistoryEntry(1, "dev", "EPAM")
                        ))
        );
    }
}
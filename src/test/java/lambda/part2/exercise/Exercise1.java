package lambda.part2.exercise;

import lambda.data.Person;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"unused", "ConstantConditions"})
class Exercise1 {

    private static String getFullName(Person person) {
        return String.format("%s %s", person.getFirstName(), person.getLastName());
    }

    private static BiFunction<Person, Person, Integer> createExtractorAgeOfPersonWithTheLongestFullName(Function<Person, String> getName) {
        return (p1, p2) -> getName.apply(p1).length() - getName.apply(p2).length() >= 0 ? p1.getAge() : p2.getAge();
    }

    @Test
    void ageExtractorFromPersonUsingMethodReference() {
        Person person = new Person("Иван", "Мельников", 33);

        Function<Person, Integer> ageExtractor = Person::getAge;

        assertThat(ageExtractor.apply(person), is(33));
    }

    @Test
    void sameAgesCheckerUsingBiPredicate() {
        Person person1 = new Person("Иван", "Мельников", 33);
        Person person2 = new Person("Дмитрий", "Гущин", 33);
        Person person3 = new Person("Илья", "Жирков", 22);

        BiPredicate<Person, Person> sameAgesChecker = (p1, p2) -> p1.getAge() == p2.getAge();

        assertThat(sameAgesChecker.test(person1, person2), is(true));
        assertThat(sameAgesChecker.test(person1, person3), is(false));
        assertThat(sameAgesChecker.test(person2, person3), is(false));

    }

    @Test
    void getAgeOfPersonWithTheLongestFullName() {
        Person person1 = new Person("Иван", "Мельников", 33);
        Person person2 = new Person("Илья", "Жирков", 22);

        Function<Person, String> getFullName = Exercise1::getFullName;

        BiFunction<Person, Person, Integer> extractorAgeOfPersonWithTheLongestFullName = Exercise1
                .createExtractorAgeOfPersonWithTheLongestFullName(
                        getFullName);

        assertThat(extractorAgeOfPersonWithTheLongestFullName.apply(person1, person2), is(33));
    }
}

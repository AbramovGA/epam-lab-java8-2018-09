package lambda.part2.exercise;

import lambda.data.Person;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class Exercise2 {

    @Test
    void personHasNotEmptyLastNameAndFirstName() {

        Predicate<Person> validate = p1 -> p1.getFirstName().length() > 0 && p1.getLastName().length() > 0;

        assertThat(validate.test(new Person("Алексей", "Доренко", 40)), is(true));
        assertThat(validate.test(new Person("Николай", "", 30)), is(false));
        assertThat(validate.test(new Person("", "Мельников", 20)), is(false));
    }

    private Predicate<Person> negateUsingLogicalOperator(Predicate<Person> predicate) {
        return p1 -> !predicate.test(p1);
    }

    private Predicate<Person> andUsingLogicalOperator(Predicate<Person> left, Predicate<Person> right) {
        return p1 -> left.test(p1) && right.test(p1);
    }

    @Test
    void personHasNotEmptyLastNameAndFirstNameUsingLogicalOperators() {
        Predicate<Person> personHasEmptyFirstName = p1 -> p1.getFirstName().length() == 0;
        Predicate<Person> personHasEmptyLastName = p1 -> p1.getLastName().length() == 0;

        Predicate<Person> personHasNotEmptyFirstName = negateUsingLogicalOperator(personHasEmptyFirstName);
        Predicate<Person> personHasNotEmptyLastName = negateUsingLogicalOperator(personHasEmptyLastName);

        Predicate<Person> personHasNotEmptyLastNameAndFirstName = andUsingLogicalOperator(personHasNotEmptyFirstName, personHasNotEmptyLastName);

        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Алексей", "Доренко", 40)), is(true));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Николай", "", 30)), is(false));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("", "Мельников", 20)), is(false));
    }

    private <T> Predicate<T> negate(Predicate<T> predicate) {
        return x -> !predicate.test(x);
    }

    private <T> Predicate<T> and(Predicate<T> left, Predicate<T> right) {
        return x -> left.test(x) && right.test(x);
    }

    @Test
    void personHasNotEmptyLastNameAndFirstNameUsingGenericPredicates() {
        Predicate<Person> personHasEmptyFirstName = p1 -> p1.getFirstName().length() == 0;
        Predicate<Person> personHasEmptyLastName = p1 -> p1.getLastName().length() == 0;

        Predicate<Person> personHasNotEmptyFirstName = negate(personHasEmptyFirstName);
        Predicate<Person> personHasNotEmptyLastName = negate(personHasEmptyLastName);

        Predicate<Person> personHasNotEmptyLastNameAndFirstName = and(personHasNotEmptyFirstName, personHasNotEmptyLastName);

        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Алексей", "Доренко", 40)), is(true));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Николай", "", 30)), is(false));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("", "Мельников", 20)), is(false));
    }

    @Test
    void personHasNotEmptyLastNameAndFirstNameUsingStandardMethods() {
        Predicate<Person> personHasEmptyFirstName = p1 -> p1.getFirstName().length() == 0;
        Predicate<Person> personHasEmptyLastName = p1 -> p1.getLastName().length() == 0;

        Predicate<Person> personHasNotEmptyFirstName = personHasEmptyFirstName.negate();
        Predicate<Person> personHasNotEmptyLastName = personHasEmptyLastName.negate();

        Predicate<Person> personHasNotEmptyLastNameAndFirstName = personHasNotEmptyFirstName.and(personHasNotEmptyLastName);

        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Алексей", "Доренко", 40)), is(true));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("Николай", "", 30)), is(false));
        assertThat(personHasNotEmptyLastNameAndFirstName.test(new Person("", "Мельников", 20)), is(false));
    }
}

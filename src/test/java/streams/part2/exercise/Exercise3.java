package streams.part2.exercise;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"unused", "ConstantConditions"})
class Exercise3 {

    @Test
    void createLimitedStringWithOddNumbersSeparatedBySpaces() {
        int countNumbers = 10;

        String result = Stream.iterate(1, prev -> prev + 2)
                .map(Objects::toString)
                .limit(countNumbers)
                .collect(joining(" "));

        assertThat(result, is("1 3 5 7 9 11 13 15 17 19"));
    }

    @Test
    void extractEvenNumberedCharactersToNewString() {
        String source = "abcdefghijklm";

        String result = Stream.of(source.split(""))
                .filter(character -> source.indexOf(character) % 2 == 0)
                .collect(Collectors.joining());

        /**
         * More fancy option:
         StringOddIterator iterator = StringOddIterator.of(source);
         String result = StreamSupport.stream(((Iterable<Character>)() -> iterator).spliterator(), false).map(StringBuffer::new).collect(Collectors.joining());
         */
        assertThat(result, is("acegikm"));
    }

}

class StringOddIterator implements Iterator {

    char[] source;
    int index = 0;

    private StringOddIterator(char[] source) {
        this.source = source;
    }

    public static StringOddIterator of(String source) {
        return new StringOddIterator(source.toCharArray());
    }

    @Override
    public boolean hasNext() {
        return source.length % 2 == 0 ? index < (source.length - 2) : index < (source.length - 1);
    }

    @Override
    public Character next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return source[index++];
    }
}

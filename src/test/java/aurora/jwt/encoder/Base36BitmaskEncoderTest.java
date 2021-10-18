package aurora.jwt.encoder;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import aurora.jwt.common.util.Base36BitmaskEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class Base36BitmaskEncoderTest {
    private Base36BitmaskEncoder encoder;

    // 1000010
    private List<Integer> numbers = Arrays.asList(1, 6);


    @BeforeEach
    public void setUp() {
        this.encoder = new Base36BitmaskEncoder();
    }

    @Test
    public void shouldEncodeAsBase36() {
        assertEquals(this.encoder.encode(numbers), "1U");
    }

    @Test
    public void ensureNoOverflowWithBigId() {
        // Each Secured Asset has an ID and we have currently (as of February 2021) only 187 Secured Assets
        List<Integer> bigNumbers = Arrays.asList(187);
        assertEquals(this.encoder.encode(bigNumbers), "1UDLE4Y0JQ44A499B73CERL8CPYJ0SZNG9URK");
        assertEquals(this.encoder.decode("1UDLE4Y0JQ44A499B73CERL8CPYJ0SZNG9URK"), bigNumbers);

        Integer maxBabylonSecuredAssetId = 187;
        this.encoder.encode(Arrays.asList(maxBabylonSecuredAssetId));
    }

    @Test
    public void shouldEncodeTo0IfGivenListIsEmpty() {
        assertEquals(this.encoder.encode(Collections.emptyList()), "0");
        Assertions.assertTrue(this.encoder.decode("0").isEmpty());
    }

    @Test
    public void decoding() {
        assertEquals(this.encoder.decode("1U"), this.numbers);

        // sanity test
        // base-36 | base-10 | base-2
        // 1       | 1       |  1
        // 2       | 2       | 10
        // 3       | 3       | 11
        // Z       | 35      | 100011
        // 10      | 36      | 100100
        assertEquals(this.encoder.decode("1"), Arrays.asList(0));       // 1
        assertEquals(this.encoder.decode("2"), Arrays.asList(1));       // 10
        assertEquals(this.encoder.decode("3"), Arrays.asList(0, 1));    // 11
        assertEquals(this.encoder.decode("Z"), Arrays.asList(0, 1, 5)); // 100011 (35)
        assertEquals(this.encoder.decode("10"), Arrays.asList(2, 5));   // 100100 (36)


        Assertions.assertTrue(this.encoder.decode("0").isEmpty());
    }
}

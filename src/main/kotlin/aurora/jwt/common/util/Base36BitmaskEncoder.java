package aurora.jwt.common.util;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class Base36BitmaskEncoder {

    private static final int RADIX_2 = 2;
    private static final int RADIX_36 = 36;
    private static final String BITMASK_ZERO = "0";
    private static final char BINARY_BIT_1 = '1';

    /**
     * Each Secured Asset could be represented as a Bit indicating whether the user is granted this Secured Asset or not.
     *
     * e.g: assets granted: (1, 6), would have the 1th and 6th bit enabled
     * BitSet: 1000010  represents that numbers 1 and 6 are granted
     * base36: 1U
     *
     * @param numbers numbers that prepresent bitmask
     * @return Base-36 encoded form of the number that represents the Bitmask, "0" will be returned if numbers is null or empty
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public String encode(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return BITMASK_ZERO;
        }

        BitSet bitSet = new BitSet();
        for(int num: numbers) {
            bitSet.set(num);
        }

        StringBuilder binaryString = new StringBuilder(bitSet.length());
        for (int i = bitSet.length() - 1; i >= 0; i--) {
            binaryString.append(bitSet.get(i) ? 1 : 0);
        }

        // base-36
        return new BigInteger(binaryString.toString(), RADIX_2).toString(RADIX_36).toUpperCase();
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public List<Integer> decode(String base36EncodedFormOfNumbers) {
        if (base36EncodedFormOfNumbers == null
                || base36EncodedFormOfNumbers.isEmpty()
                || BITMASK_ZERO.equals(base36EncodedFormOfNumbers)) {
            return Collections.emptyList();
        }

        // e.g: for "1000010" ([1],[0],[0],[0],[0],[1],[0]) => {1,6}
        String binaryString = new BigInteger(base36EncodedFormOfNumbers, RADIX_36).toString(RADIX_2);
        char[] binaryBits = binaryString.toCharArray();

        List<Integer> numbers = new ArrayList<>();
        for (int i = binaryBits.length - 1, number = 0; i >= 0; i--, number++) {
            if (binaryBits[i] == BINARY_BIT_1) {
                numbers.add(number);
            }
        }

        return numbers;
    }
}

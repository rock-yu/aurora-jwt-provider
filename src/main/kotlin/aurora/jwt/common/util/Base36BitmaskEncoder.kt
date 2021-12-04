package aurora.jwt.common.util

import java.math.BigInteger
import java.util.BitSet

open class Base36BitmaskEncoder {
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
    open fun encode(numbers: List<Int>): String {
        if (numbers.isEmpty()) {
            return BITMASK_ZERO
        }
        val bitSet = BitSet()
        for (num in numbers) {
            bitSet.set(num)
        }
        val binaryString = StringBuilder(bitSet.length())
        for (i in bitSet.length() - 1 downTo 0) {
            binaryString.append(if (bitSet[i]) 1 else 0)
        }

        // base-36
        return BigInteger(binaryString.toString(), RADIX_2).toString(RADIX_36).toUpperCase()
    }

    fun decode(base36EncodedFormOfNumbers: String?): List<Int> {
        if (base36EncodedFormOfNumbers == null || base36EncodedFormOfNumbers.isEmpty() || BITMASK_ZERO == base36EncodedFormOfNumbers
        ) {
            return emptyList()
        }

        // e.g: for "1000010" ([1],[0],[0],[0],[0],[1],[0]) => {1,6}
        val binaryString = BigInteger(base36EncodedFormOfNumbers, RADIX_36).toString(RADIX_2)
        val binaryBits = binaryString.toCharArray()
        val numbers: MutableList<Int> = ArrayList()
        var i = binaryBits.size - 1
        var number = 0
        while (i >= 0) {
            if (binaryBits[i] == BINARY_BIT_1) {
                numbers.add(number)
            }
            i--
            number++
        }
        return numbers
    }

    companion object {
        private const val RADIX_2 = 2
        private const val RADIX_36 = 36
        private const val BITMASK_ZERO = "0"
        private const val BINARY_BIT_1 = '1'
    }
}

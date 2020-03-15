/**
 * See link for examples.
 * https://junit.org/junit5/docs/current/user-guide/
 */
package bfst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FirstJunit5Test {

    @Test public void testHelloIsHello() {
        assertEquals("hello", "hello");
    }
}

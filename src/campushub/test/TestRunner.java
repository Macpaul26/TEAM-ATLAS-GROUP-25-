package campushub.test;

/**
 * A minimal, dependency-free test harness. We avoid pulling in JUnit
 * so the whole project can be compiled and marked with nothing more
 * than a JDK on the marker's machine (javac/java only, no build tool
 * or internet access required).
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void assertTrue(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + testName);
        } else {
            failed++;
            System.out.println("  [FAIL] " + testName);
        }
    }

    public static void assertEquals(String testName, Object expected, Object actual) {
        boolean ok = (expected == null) ? actual == null : expected.equals(actual);
        assertTrue(testName + " (expected=" + expected + ", actual=" + actual + ")", ok);
    }

    public static void assertEquals(String testName, double expected, double actual, double tolerance) {
        boolean ok = Math.abs(expected - actual) <= tolerance;
        assertTrue(testName + " (expected=" + expected + ", actual=" + actual + ")", ok);
    }

    public static void section(String title) {
        System.out.println("\n== " + title + " ==");
    }

    public static void printSummary() {
        System.out.println("\n============================");
        System.out.println("TOTAL: " + (passed + failed) + "  PASSED: " + passed + "  FAILED: " + failed);
        System.out.println("============================");
        if (failed > 0) {
            System.exit(1);
        }
    }
}

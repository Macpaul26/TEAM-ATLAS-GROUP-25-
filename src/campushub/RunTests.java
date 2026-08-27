package campushub;

import campushub.test.AlgorithmTests;
import campushub.test.DataStructureTests;
import campushub.test.NewAlgorithmTests;
import campushub.test.NewStructureTests;
import campushub.test.TestRunner;

/** Entry point that runs the full test suite and prints a pass/fail summary. */
public class RunTests {
    public static void main(String[] args) {
        System.out.println("Running TEAM ATLAS - GROUP 25 test suite...");
        DataStructureTests.run();
        NewStructureTests.run();
        AlgorithmTests.run();
        NewAlgorithmTests.run();
        TestRunner.printSummary();
    }
}

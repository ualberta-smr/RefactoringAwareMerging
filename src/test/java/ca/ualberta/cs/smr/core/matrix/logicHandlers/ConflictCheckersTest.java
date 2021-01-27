package ca.ualberta.cs.smr.core.matrix.logicHandlers;

import org.junit.Assert;
import org.junit.Test;

public class ConflictCheckersTest {

    @Test
    public void testCheckNamingConflict() {
        String originalElement = "foo";
        String originalVisitor = "bar";
        String refactoredElement = "newFoo";
        String refactoredVisitor = "newBar";
        boolean expectedFalse = ConflictCheckers.checkNamingConflict(originalElement, originalVisitor,
                                                                            refactoredElement, refactoredVisitor);
        Assert.assertFalse("Expected false because the renamings do not conflict", expectedFalse);

        originalVisitor = "foo";
        boolean expectedTrue = ConflictCheckers.checkNamingConflict(originalElement, originalVisitor,
                                                                            refactoredElement, refactoredVisitor);
        Assert.assertTrue("Expected true because an element is renamed to two names", expectedTrue);
        refactoredVisitor = "newFoo";
        expectedFalse = ConflictCheckers.checkNamingConflict(originalElement, originalVisitor,
                                                                            refactoredElement, refactoredVisitor);
        Assert.assertFalse("Expected false because the renamings are the same", expectedFalse);
        originalVisitor = "bar";
        expectedTrue = ConflictCheckers.checkNamingConflict(originalElement, originalVisitor,
                                                                            refactoredElement, refactoredVisitor);
        Assert.assertTrue("Expected true because two elements are renamed to the same name", expectedTrue);
    }
}

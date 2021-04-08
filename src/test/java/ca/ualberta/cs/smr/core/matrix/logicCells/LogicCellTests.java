package ca.ualberta.cs.smr.core.matrix.logicCells;

import ca.ualberta.cs.smr.core.dependenceGraph.Node;
import ca.ualberta.cs.smr.testUtils.GetDataForTests;
import ca.ualberta.cs.smr.utils.MatrixUtils;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Assert;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogicCellTests extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testCheckNamingConflict() {
        String originalLeft = "foo";
        String originalRight = "bar";
        String refactoredLeft = "newFoo";
        String refactoredRight = "newBar";
        boolean expectedFalse = MatrixUtils.checkNamingConflict(originalLeft, originalRight,
                                                                            refactoredLeft, refactoredRight);
        Assert.assertFalse("Expected false because the renamings do not conflict", expectedFalse);

        originalRight = "foo";
        boolean expectedTrue = MatrixUtils.checkNamingConflict(originalLeft, originalRight,
                                                                            refactoredLeft, refactoredRight);
        Assert.assertTrue("Expected true because an element is renamed to two names", expectedTrue);
        refactoredRight = "newFoo";
        expectedFalse = MatrixUtils.checkNamingConflict(originalLeft, originalRight,
                                                                            refactoredLeft, refactoredRight);
        Assert.assertFalse("Expected false because the renamings are the same", expectedFalse);
        originalRight = "bar";
        expectedTrue = MatrixUtils.checkNamingConflict(originalLeft, originalRight,
                                                                            refactoredLeft, refactoredRight);
        Assert.assertTrue("Expected true because two elements are renamed to the same name", expectedTrue);
    }

    public void testCheckRenameMethodRenameMethodOverrideConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodOverrideConflict/original";
        String refactoredPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodOverrideConflict/refactored";
        String configurePath = "renameMethodRenameMethodFiles/methodOverrideConflict/original/Override.java";
        myFixture.configureByFiles(configurePath);
        List<Refactoring> refactorings = GetDataForTests.getRefactorings("RENAME_METHOD", originalPath, refactoredPath);
        assert refactorings != null;
        assert refactorings.size() == 5;
        Refactoring renameParentFooMethod = refactorings.get(0);
        Refactoring renameOtherFooMethod = refactorings.get(1);
        Refactoring renameChildBarMethod = refactorings.get(2);
        Refactoring renameOtherBarMethod = refactorings.get(3);
        Refactoring renameFooBarMethod = refactorings.get(4);
        RenameMethodRenameMethodCell renameMethodRenameMethodCell = new RenameMethodRenameMethodCell(project);
        boolean isConflicting = renameMethodRenameMethodCell.checkOverrideConflict(new Node(renameParentFooMethod), new Node(renameOtherFooMethod));
        Assert.assertFalse("Renamings in the same class should not result in override conflict", isConflicting);
        isConflicting = renameMethodRenameMethodCell.checkOverrideConflict(new Node(renameParentFooMethod), new Node(renameOtherBarMethod));
        Assert.assertFalse("Methods that have no override relation, before or after, should not conflict", isConflicting);
        isConflicting = renameMethodRenameMethodCell.checkOverrideConflict(new Node(renameParentFooMethod), new Node(renameFooBarMethod));
        Assert.assertFalse("Classes that have no inheritance should not result in override conflicts", isConflicting);
        isConflicting = renameMethodRenameMethodCell.checkOverrideConflict(new Node(renameParentFooMethod), new Node(renameChildBarMethod));
        Assert.assertTrue("Originally overriding methods that are renamed to different names conflict", isConflicting);
    }

    public void testCheckRenameMethodRenameMethodOverloadConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodOverloadConflict/original";
        String refactoredPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodOverloadConflict/refactored";
        String configurePath = "renameMethodRenameMethodFiles/methodOverloadConflict/original/OverloadClasses.java";
        myFixture.configureByFiles(configurePath);
        List<Refactoring> refactorings = GetDataForTests.getRefactorings("RENAME_METHOD", originalPath, refactoredPath);
        assert refactorings != null;
        assert refactorings.size() == 3;
        Refactoring leftRefactoring = refactorings.get(0);
        Refactoring rightRefactoring = refactorings.get(2);
        RenameMethodRenameMethodCell renameMethodRenameMethodCell = new RenameMethodRenameMethodCell(project);
        boolean isConflicting = renameMethodRenameMethodCell.checkOverloadConflict(new Node(leftRefactoring), new Node(rightRefactoring));
        Assert.assertFalse("Methods in the same class that do not have related names " +
                "before or after being refactored should not conflict", isConflicting);
    }

    public void testCheckRenameMethodRenameMethodNamingConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodNamingConflict/original";
        String refactoredPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodNamingConflict/refactored";
        List<Refactoring> refactorings = GetDataForTests.getRefactorings("RENAME_METHOD", originalPath, refactoredPath);
        assert refactorings != null;
        Refactoring leftRef = refactorings.get(1);
        Refactoring rightRef = refactorings.get(2);
        Node leftNode = new Node(leftRef);
        Node rightNode = new Node(rightRef);
        RenameMethodRenameMethodCell renameMethodRenameMethodCell = new RenameMethodRenameMethodCell(project);
        boolean expectedFalse = renameMethodRenameMethodCell.checkMethodNamingConflict(leftNode, rightNode);
        Assert.assertFalse("Methods in different classes should not have naming conflicts", expectedFalse);
        rightRef = refactorings.get(0);
        rightNode = new Node(rightRef);
        boolean expectedTrue = renameMethodRenameMethodCell.checkMethodNamingConflict(leftNode, rightNode);
        Assert.assertTrue("Methods renamed to the same name in the same class should return true", expectedTrue);
        expectedTrue = renameMethodRenameMethodCell.checkMethodNamingConflict(rightNode, leftNode);
        Assert.assertTrue("The same refactorings in a different order should return true", expectedTrue);
        expectedFalse = renameMethodRenameMethodCell.checkMethodNamingConflict(rightNode, rightNode);
        Assert.assertFalse("A method renamed to the same name in both versions should not conflict", expectedFalse);
    }

    public void testNestedRenameMethodRenameMethodNamingConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodNamingConflict/original";
        String refactoredPath = basePath + "/src/test/testData/renameMethodRenameMethodFiles/methodNamingConflict/refactored";
        List<Refactoring> methodRefactorings = GetDataForTests.getRefactorings("RENAME_METHOD", originalPath, refactoredPath);
        List<Refactoring> classRefactorings = GetDataForTests.getRefactorings("RENAME_CLASS", originalPath, refactoredPath);
        assert methodRefactorings != null;
        assert classRefactorings != null;
        Refactoring dispatcherRef = methodRefactorings.get(0);
        Refactoring rightRef = methodRefactorings.get(3);
        Refactoring classRef = classRefactorings.get(0);
        Node dispatcherNode = new Node(dispatcherRef);
        Node rightNode = new Node(rightRef);
        Node classNode = new Node(classRef);
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(classNode);
        rightNode.addDependsList(nodes);
        RenameMethodRenameMethodCell renameMethodRenameMethodCell = new RenameMethodRenameMethodCell(project);
        boolean isConflicting = renameMethodRenameMethodCell.checkMethodNamingConflict(dispatcherNode, rightNode);
        Assert.assertTrue(isConflicting);
    }

    public void testCheckRenameClassRenameClassNamingConflict() {
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameClassRenameClassFiles/renameClassNamingConflict/original";
        String refactoredPath = basePath + "/src/test/testData/renameClassRenameClassFiles/renameClassNamingConflict/refactored";
        List<Refactoring> refactorings = GetDataForTests.getRefactorings("RENAME_CLASS", originalPath, refactoredPath);
        assert refactorings != null && refactorings.size() == 3;
        Refactoring foo = refactorings.get(0);
        Refactoring foo2 = refactorings.get(1);
        Refactoring bar = refactorings.get(2);
        boolean isConflicting = RenameClassRenameClassCell.checkClassNamingConflict(new Node(foo), new Node(bar));
        Assert.assertFalse("Classes without related refactorings should not conflict", isConflicting);
        isConflicting = RenameClassRenameClassCell.checkClassNamingConflict(new Node(foo), new Node(foo2));
        Assert.assertTrue("Classes renamed to the same name in the same package conflict", isConflicting);

    }


    public void testCheckRenameMethodRenameClassDependence() {
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/renameMethodRenameClassFiles/dependence/original";
        String refactoredPath = basePath + "/src/test/testData/renameMethodRenameClassFiles/dependence/refactored";
        List<Refactoring> methodRefs = GetDataForTests.getRefactorings("RENAME_METHOD", originalPath, refactoredPath);
        List<Refactoring> classRefs = GetDataForTests.getRefactorings("RENAME_CLASS", originalPath, refactoredPath);
        assert methodRefs != null;
        Refactoring methodRef = methodRefs.get(0);
        assert classRefs != null;
        Refactoring classRef = classRefs.get(0);
        Node classNode = new Node(classRef);
        Node methodNode = new Node(methodRef);
        boolean isDependent = RenameClassRenameMethodCell.checkRenameMethodRenameClassDependence(methodNode, classNode);
        Assert.assertTrue(isDependent);
    }

    public void testCheckExtractMethodRenameClassDependence() {
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodRenameClassFiles/dependence/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodRenameClassFiles/dependence/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION", originalPath, refactoredPath);
        assert extractMethodRefactorings != null;
        extractMethodRefactorings.addAll(Objects.requireNonNull(GetDataForTests.getRefactorings("EXTRACT_AND_MOVE_OPERATION",
                originalPath, refactoredPath)));
        List<Refactoring> renameClassRefactorings = GetDataForTests.getRefactorings("RENAME_CLASS", originalPath, refactoredPath);
        assert renameClassRefactorings != null;
        Node extractMethodNode = new Node(extractMethodRefactorings.get(0));
        Node renameClassNode = new Node(renameClassRefactorings.get(0));
        boolean isDependent = ExtractMethodRenameClassCell.checkExtractMethodRenameClassDependence(renameClassNode, extractMethodNode);
        Assert.assertFalse(isDependent);
        extractMethodNode = new Node(extractMethodRefactorings.get(1));
        isDependent = ExtractMethodRenameClassCell.checkExtractMethodRenameClassDependence(renameClassNode, extractMethodNode);
        Assert.assertTrue(isDependent);
        extractMethodNode = new Node(extractMethodRefactorings.get(2));
        isDependent = ExtractMethodRenameClassCell.checkExtractMethodRenameClassDependence(renameClassNode, extractMethodNode);
        Assert.assertTrue(isDependent);
    }

    public void testCheckExtractMethodRenameMethodOverrideConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/refactored";

        String configurePath = "extractMethodRenameMethodFiles/refactored/OverloadInheritance.java";
        myFixture.configureByFiles(configurePath);

        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);
        List<Refactoring> renameMethodRefactorings = GetDataForTests.getRefactorings("RENAME_METHOD",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        assert renameMethodRefactorings != null;
        Node extractMethodNode = new Node(extractMethodRefactorings.get(0));
        Node renameMethodNode = new Node(renameMethodRefactorings.get(0));
        ExtractMethodRenameMethodCell cell = new ExtractMethodRenameMethodCell(project);
        boolean isDependent = cell.checkOverrideConflict(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
        renameMethodNode = new Node(renameMethodRefactorings.get(2));
        isDependent = cell.checkOverrideConflict(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
        extractMethodNode = new Node(extractMethodRefactorings.get(3));
        renameMethodNode = new Node(renameMethodRefactorings.get(5));
        isDependent = cell.checkOverrideConflict(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
        extractMethodNode = new Node(extractMethodRefactorings.get(1));
        renameMethodNode = new Node(renameMethodRefactorings.get(3));

        configurePath = "extractMethodRenameMethodFiles/refactored/Override.java";
        myFixture.configureByFiles(configurePath);

        isDependent = cell.checkOverrideConflict(renameMethodNode, extractMethodNode);
        Assert.assertTrue(isDependent);
    }

    public void testCheckExtractMethodRenameMethodOverloadConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/refactored";

        String configurePath = "extractMethodRenameMethodFiles/refactored/OverloadInheritance.java";
        myFixture.configureByFiles(configurePath);

        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);
        List<Refactoring> renameMethodRefactorings = GetDataForTests.getRefactorings("RENAME_METHOD",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        assert renameMethodRefactorings != null;
        Node extractMethodNode = new Node(extractMethodRefactorings.get(0));
        Node renameMethodNode = new Node(renameMethodRefactorings.get(0));
        ExtractMethodRenameMethodCell cell = new ExtractMethodRenameMethodCell(project);
        boolean isDependent = cell.checkOverloadConflict(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
        renameMethodNode = new Node(renameMethodRefactorings.get(2));
        isDependent = cell.checkOverloadConflict(renameMethodNode, extractMethodNode);
        Assert.assertTrue(isDependent);
        extractMethodNode = new Node(extractMethodRefactorings.get(3));
        renameMethodNode = new Node(renameMethodRefactorings.get(5));
        isDependent = cell.checkOverloadConflict(renameMethodNode, extractMethodNode);
        Assert.assertTrue(isDependent);
    }

    public void testCheckExtractMethodRenameMethodNamingConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);
        List<Refactoring> renameMethodRefactorings = GetDataForTests.getRefactorings("RENAME_METHOD",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        assert renameMethodRefactorings != null;
        Node extractMethodNode = new Node(extractMethodRefactorings.get(2));
        Node renameMethodNode = new Node(renameMethodRefactorings.get(4));
        ExtractMethodRenameMethodCell cell = new ExtractMethodRenameMethodCell(project);
        boolean isDependent = cell.checkMethodNamingConflict(renameMethodNode, extractMethodNode);
        Assert.assertTrue(isDependent);
        renameMethodNode = new Node(renameMethodRefactorings.get(3));
        isDependent = cell.checkMethodNamingConflict(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
    }

    public void testCheckExtractMethodRenameMethodDependence() {
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodRenameMethodFiles/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);
        List<Refactoring> renameMethodRefactorings = GetDataForTests.getRefactorings("RENAME_METHOD",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        assert renameMethodRefactorings != null;
        Node extractMethodNode = new Node(extractMethodRefactorings.get(0));
        Node renameMethodNode = new Node(renameMethodRefactorings.get(1));
        boolean isDependent = ExtractMethodRenameMethodCell.checkExtractMethodRenameMethodDependence(renameMethodNode,
                extractMethodNode);
        Assert.assertTrue(isDependent);
        renameMethodNode = new Node(renameMethodRefactorings.get(2));
        isDependent = ExtractMethodRenameMethodCell.checkExtractMethodRenameMethodDependence(renameMethodNode, extractMethodNode);
        Assert.assertFalse(isDependent);
    }

    public void testCheckExtractMethodExtractMethodOverlappingFragmentsConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        Node node1 = new Node(extractMethodRefactorings.get(0));
        Node node2 = new Node(extractMethodRefactorings.get(1));
        ExtractMethodExtractMethodCell cell = new ExtractMethodExtractMethodCell(project);
        boolean isConflicting = cell.checkOverlappingFragmentsConflict(node1, node2);
        Assert.assertFalse(isConflicting);
        node1 = new Node(extractMethodRefactorings.get(7));
        isConflicting = cell.checkOverlappingFragmentsConflict(node1, node2);
        Assert.assertTrue(isConflicting);
    }

    public void testCheckExtractMethodExtractMethodOverrideConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/refactored";
        String configurePath = "extractMethodExtractMethodFiles/refactored/Override.java";
        myFixture.configureByFiles(configurePath);
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        Node node1 = new Node(extractMethodRefactorings.get(2));
        Node node2 = new Node(extractMethodRefactorings.get(3));
        ExtractMethodExtractMethodCell cell = new ExtractMethodExtractMethodCell(project);
        boolean isConflicting = cell.checkOverrideConflict(node1, node2);
        Assert.assertTrue(isConflicting);
    }

    public void testCheckExtractMethodExtractMethodOverloadConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        Node node1 = new Node(extractMethodRefactorings.get(5));
        Node node2 = new Node(extractMethodRefactorings.get(6));
        ExtractMethodExtractMethodCell cell = new ExtractMethodExtractMethodCell(project);
        boolean isConflicting = cell.checkOverloadConflict(node1, node2);
        Assert.assertTrue(isConflicting);
        isConflicting = cell.checkOverloadConflict(node1, node1);
        Assert.assertFalse(isConflicting);
    }

    public void testCheckExtractMethodExtractMethodNamingConflict() {
        Project project = myFixture.getProject();
        String basePath = System.getProperty("user.dir");
        String originalPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/original";
        String refactoredPath = basePath + "/src/test/testData/extractMethodExtractMethodFiles/refactored";
        List<Refactoring> extractMethodRefactorings = GetDataForTests.getRefactorings("EXTRACT_OPERATION",
                originalPath, refactoredPath);

        assert extractMethodRefactorings != null;
        Node node1 = new Node(extractMethodRefactorings.get(5));
        ExtractMethodExtractMethodCell cell = new ExtractMethodExtractMethodCell(project);
        boolean isConflicting = cell.checkMethodNamingConflict(node1, node1);
        Assert.assertTrue(isConflicting);
        Node node2 = new Node(extractMethodRefactorings.get(6));
        isConflicting = cell.checkMethodNamingConflict(node1, node2);
        Assert.assertFalse(isConflicting);

    }

}

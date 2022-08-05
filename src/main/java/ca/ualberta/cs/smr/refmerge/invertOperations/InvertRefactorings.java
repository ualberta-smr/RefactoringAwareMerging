package ca.ualberta.cs.smr.refmerge.invertOperations;

import ca.ualberta.cs.smr.refmerge.refactoringObjects.RefactoringObject;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

// To Do: Refactor to call invert/replay from objects
public class InvertRefactorings {
    /*
     * invertRefactorings takes a list of refactorings and performs the inverse for each one.
     */
    public static int invertRefactorings(ArrayList<RefactoringObject> refactoringObjects,
                                                                  Project project) {
        long time = System.currentTimeMillis();

        int failedRefactorings = 0;
        // Iterate through the list of refactorings and undo each one
        for(RefactoringObject refactoringObject : refactoringObjects) {
            long time2 = System.currentTimeMillis();
            // If it has been 14 minutes, it will take more than 15 minutes to complete RefMerge
            if((time2 - time) > 780000) {
                System.out.println("RefMerge Timed Out");
                // Save all of the refactoring changes from memory onto disk
                FileDocumentManager.getInstance().saveAllDocuments();
                return failedRefactorings;
            }
            switch (refactoringObject.getRefactoringType()) {
                case RENAME_CLASS:
                case MOVE_CLASS:
                case MOVE_RENAME_CLASS:
                    try {
                        // Undo the rename class refactoring. This is commented out because of the prompt issue
                        InvertMoveRenameClass invertMoveRenameClass = new InvertMoveRenameClass(project);
                        invertMoveRenameClass.invertMoveRenameClass(refactoringObject);
                    } catch (Exception e) {
                        failedRefactorings++;
                        e.printStackTrace();
                    }
                    break;
                case RENAME_METHOD:
                case MOVE_OPERATION:
                case MOVE_AND_RENAME_OPERATION:
                    // Undo the rename method refactoring
                    try {
                        InvertMoveRenameMethod invertMoveRenameMethod = new InvertMoveRenameMethod(project);
                        invertMoveRenameMethod.invertMoveRenameMethod(refactoringObject);
                    } catch (Exception e) {
                        failedRefactorings++;
                        e.printStackTrace();
                    }
                    break;
                case EXTRACT_OPERATION:
                    try {
                        InvertExtractMethod invertExtractMethod = new InvertExtractMethod(project);
                        refactoringObject = invertExtractMethod.invertExtractMethod(refactoringObject);
                    }
                    catch(Exception e) {
                        failedRefactorings++;
                        e.printStackTrace();
                        break;
                    }
                    if(refactoringObject == null) {
                        break;
                    }
                    int index = refactoringObjects.indexOf(refactoringObject);
                    refactoringObjects.set(index, refactoringObject);
                    break;
                case INLINE_OPERATION:
                    try {
                        InvertInlineMethod invertInlineMethod = new InvertInlineMethod(project);
                        invertInlineMethod.invertInlineMethod(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case RENAME_ATTRIBUTE:
                case MOVE_ATTRIBUTE:
                case MOVE_RENAME_ATTRIBUTE:
                    try {
                        InvertMoveRenameField invertMoveRenameField = new InvertMoveRenameField(project);
                        invertMoveRenameField.invertRenameField(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case PULL_UP_OPERATION:
                    try {
                        InvertPullUpMethod invertPullUpMethod = new InvertPullUpMethod(project);
                        invertPullUpMethod.invertPullUpMethod(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case PUSH_DOWN_OPERATION:
                    try {
                        InvertPushDownMethod invertPushDownMethod = new InvertPushDownMethod(project);
                        invertPushDownMethod.invertPushDownMethod(refactoringObject);
                    } catch(Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case PULL_UP_ATTRIBUTE:
                    try {
                        InvertPullUpField invertPullUpField = new InvertPullUpField(project);
                        invertPullUpField.invertPullUpField(refactoringObject);
                    } catch(Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case PUSH_DOWN_ATTRIBUTE:
                    try {
                        InvertPushDownField invertPushDownField = new InvertPushDownField(project);
                        invertPushDownField.invertPushDownField(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case RENAME_PACKAGE:
                    try {
                        InvertRenamePackage invertRenamePackage = new InvertRenamePackage(project);
                        invertRenamePackage.invertRenamePackage(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;
                case RENAME_PARAMETER:
                    try {
                        InvertRenameParameter invertRenameParameter = new InvertRenameParameter(project);
                        invertRenameParameter.invertRenameParameter(refactoringObject);
                    } catch (Exception exception) {
                        failedRefactorings++;
                        exception.printStackTrace();
                    }
                    break;

            }

        }
        // Save all of the refactoring changes from memory onto disk
        FileDocumentManager.getInstance().saveAllDocuments();
        return failedRefactorings;
    }
}

package ca.ualberta.cs.smr.refmerge.matrix.receivers;

import ca.ualberta.cs.smr.refmerge.matrix.dispatcher.*;
import ca.ualberta.cs.smr.refmerge.refactoringObjects.RefactoringObject;
import com.intellij.openapi.project.Project;

/*
 * The Receiver superclass contains each receive method that the receiver classes will need to use. Each time we
 * add a new refactoring type, we need to add a new receive method with the corresponding refactoring element. No
 * methods other than `receive()` should be added to this class.
 */

public class Receiver {
    Project project;
    RefactoringObject refactoringObject;
    boolean isTransitive;
    boolean isConflicting;

    public void set(RefactoringObject refactoringObject,  Project project) {
        this.refactoringObject = refactoringObject;
        this.project = project;
        this.isConflicting = false;
        this.isTransitive = false;
    }

    public void set(RefactoringObject refactoringObject) {
        this.refactoringObject = refactoringObject;
        this.isTransitive = false;
        this.isConflicting = false;
    }

    public boolean hasTransitivity() {
        return isTransitive;
    }

    public boolean isConflicting() {
        return isConflicting;
    }

    /*
     * Any method that overrides this will dispatch to a logic cell containing a rename, move, or move and rename method refactoring.
     */
    public void receive(MoveRenameMethodDispatcher dispatcher) {
        // This is empty because subclasses will override this to dispatch to the correct logic cell.
    }

    /*
     * Any method that overrides this will dispatch to a logic cell containing a move, rename, or move and rename class refactoring.
     */
    public void receive(MoveRenameClassDispatcher dispatcher) {
        // This is empty because subclasses will override this to dispatch to the correct logic cell.
    }

    /*
     * Any method that overrides this will dispatch to a logic cell containing an extract method refactoring.
     */
    public void receive(ExtractMethodDispatcher dispatcher) {
        // This is empty because subclasses will override this to dispatch to the correct logic cell.
    }

    /*
     * Any method that overrides this will dispatch to a logic cell containing an inline method refactoring.
     */
    public void receive(InlineMethodDispatcher dispatcher) {
        // This is empty because subclasses will override this to dispatch to the correct logic cell.
    }

    /*
     * Any method that overrides this will dispatch to a logic cell containing a rename field refactoring.
     */
    public void receive(MoveRenameFieldDispatcher dispatcher) {
        // This is empty because subclasses that receive a rename+move field dispatcher will override this method.
    }

    public void receive(PullUpMethodDispatcher dispatcher) {
        // This is empty because subclasses that receive a pull up method dispatcher will override this method.
    }

    public void receive(PushDownMethodDispatcher dispatcher) {
        // This is empty because subclasses that receive a push down method dispatcher will override this method.
    }

    public void receive(PullUpFieldDispatcher dispatcher) {
        // This is empty because subclasses that receive a pull up field dispatcher will override this method.
    }

    public void receive(PushDownFieldDispatcher dispatcher) {
        // This is empty because subclasses that receive a push down field dispatcher will override this method.
    }

    public void receive(RenamePackageDispatcher dispatcher) {
        // This is empty because subclasses that receive a rename package dispatcher will override this method.
    }

    public void receive(RenameParameterDispatcher dispatcher) {
        // This is empty because subclasses that receive a rename parameter dispatcher will override this method.
    }

    public void receive(AddParameterDispatcher dispatcher) {
        // This is empty because subclasses that receive an add parameter dispatcher will override this method.
    }

    public void receive(RemoveParameterDispatcher dispatcher) {
        // This is empty because subclasses that receive an add parameter dispatcher will override this method.
    }

    public void receive(ReorderParameterDispatcher dispatcher) {
        // This is empty because subclasses that receive a reorder parameter dispatcher will override this method.
    }

    public void receive(ChangeParameterTypeDispatcher dispatcher) {
        // This is empty because subclasses that receive a change parameter type dispatcher will override this method.
    }

}

package seedu.address.model;

import java.util.Stack;

import seedu.address.model.note.UniqueNoteList;
import seedu.address.model.note.exceptions.InvalidRedoException;
import seedu.address.model.note.exceptions.InvalidUndoException;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class VersionedNoteBook extends NoteBook {
    private Stack<NoteBookWithCommand> undoStack = new Stack<>();
    private Stack<NoteBookWithCommand> redoStack = new Stack<>();

    public VersionedNoteBook(ReadOnlyNoteBook noteBook) {
        super(noteBook);
    }

    public VersionedNoteBook() {
        super();
    }

    /**
     * Commits the current state of the application into the list of state as the latest state.
     */
    public void commit(String command) {
        UniqueNoteList currentUniqueNoteList = new UniqueNoteList();
        currentUniqueNoteList.setNotes(getNotes());
        if (undoStack.empty()) {
            ReadOnlyNoteBook currentNoteBookState = new NoteBook(currentUniqueNoteList, getSortByCond());
            undoStack.push(new NoteBookWithCommand(currentNoteBookState, command));
        } else {
            ReadOnlyNoteBook previousNoteBookState = undoStack.peek().getNoteBook();
            if (!(previousNoteBookState.getNoteList().equals(getNoteList())
                    && previousNoteBookState.getSortByCond().equals(getSortByCond()))) {
                ReadOnlyNoteBook currentNoteBookState = new NoteBook(currentUniqueNoteList, getSortByCond());
                undoStack.push(new NoteBookWithCommand(currentNoteBookState, command));
            }
        }
        while (!redoStack.empty()) {
            redoStack.pop();
        }
    }


    /**
     * Undo by changing the state of the NoteBook to the previous state.
     */
    public String undo() {
        if (!undoStack.empty()) {
            ReadOnlyNoteBook previousNoteBookState = undoStack.peek().getNoteBook();
            String previousCommand = undoStack.peek().getCommand();
            if (!(previousNoteBookState.getNoteList().equals(getNoteList())
                    && previousNoteBookState.getSortByCond().equals(getSortByCond()))) {
                UniqueNoteList currentUniqueNoteList = new UniqueNoteList();
                currentUniqueNoteList.setNotes(getNotes());
                ReadOnlyNoteBook currentNoteBookState = new NoteBook(currentUniqueNoteList, getSortByCond());
                redoStack.push(new NoteBookWithCommand(currentNoteBookState, previousCommand));
                setNotes(previousNoteBookState.getNoteList());
                setSortByCond(previousNoteBookState.getSortByCond());
                undoStack.pop();
                return previousCommand;
            } else {
                undoStack.pop();
                return undo();
            }
        } else {
            throw new InvalidUndoException();
        }
    }


    /**
     * Redo by changing the state of the NoteBook to the next state.
     */
    public String redo() {
        //TODO: refactor this to make it cleaner
        if (!redoStack.empty()) {
            String nextCommand = redoStack.peek().getCommand();
            UniqueNoteList currentUniqueNoteList = new UniqueNoteList();
            currentUniqueNoteList.setNotes(getNotes());
            ReadOnlyNoteBook currentNoteBookState = new NoteBook(currentUniqueNoteList, getSortByCond());
            undoStack.push(new NoteBookWithCommand(currentNoteBookState, nextCommand));
            ReadOnlyNoteBook nextNoteBookState = redoStack.pop().getNoteBook();
            setNotes(nextNoteBookState.getNoteList());
            setSortByCond(nextNoteBookState.getSortByCond());
            return nextCommand;
        } else {
            throw new InvalidRedoException();
        }
    }

    /**
     * Couples the undoable command with the Notebook state.
     */
    private class NoteBookWithCommand {
        private ReadOnlyNoteBook notebook;
        private String command;

        public NoteBookWithCommand(ReadOnlyNoteBook notebook, String command) {
            this.notebook = notebook;
            this.command = command;
        }

        public ReadOnlyNoteBook getNoteBook() {
            return this.notebook;
        }

        public String getCommand() {
            return this.command;
        }
    }

}

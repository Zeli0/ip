import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class Parser {
    // Interprets user input
    Ui ui;
    TaskList list;
    public Parser(Ui ui, TaskList list) {
        this.ui = ui;
        this.list = list;
    }

    public void processInput(String input) {
        String[] inputAnalyzed = input.split(" ");
        if (input.contains("#")) {
            ui.showInvalidInputError("Illegal character '#'.");
            return;
        }
        try {
            switch (inputAnalyzed[0].toLowerCase(Locale.ROOT)) {
                case "bye":
                    byeOperation(inputAnalyzed);
                    break;
                case "list":
                    listOperation(inputAnalyzed);
                    break;
                case "mark":
                    markOperation(inputAnalyzed);
                    break;
                case "unmark":
                    unmarkOperation(inputAnalyzed);
                    break;
                case "deadline":
                    ddlOperation(input);
                    break;
                case "todo":
                    todoOperation(input);
                    break;
                case "event":
                    eventOperation(input);
                    break;
                case "delete":
                    deleteOperation(inputAnalyzed);
                    break;
                default:
                    ui.unknownCommand();
            }
        } catch (InvalidInputException e) {
            ui.showInvalidInputError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            ui.showArrayOutOfBoundsError();
        } catch (NumberFormatException e) {
            ui.showNumberFormatError();
        }

    }
    private void byeOperation(String[] inputAnalyzed) throws InvalidInputException {
        //Check if there is anything other than bye
        if (inputAnalyzed.length > 1) {
            throw new InvalidInputException("Incorrect format. Correct form should be \"bye\".");
        } else {
            ui.close();
        }
    }

    private void listOperation(String[] inputAnalyzed) throws InvalidInputException {
        //Check if there is anything other than list
        if (inputAnalyzed.length > 1) {
            throw new InvalidInputException("Incorrect format. Correct form should be \"list\".");
        } else {
            ui.showListState(list);
        }
    }

    private void markOperation(String[] inputAnalyzed) throws InvalidInputException,
            IndexOutOfBoundsException, NumberFormatException {
        // Parse
        if (inputAnalyzed.length != 2) {
            throw new InvalidInputException("Incorrect format. Correct form should be \"mark i\", " +
                    "with i being an integer.");
        }
        int index = parseInt(inputAnalyzed[1]);
        // List
        list.mark(index);
        // Ui
        ui.showMarkSuccess(list.get(index - 1));
    }

    private void unmarkOperation(String[] inputAnalyzed) throws InvalidInputException, IndexOutOfBoundsException,
            NumberFormatException {
        if (inputAnalyzed.length != 2) {
            throw new InvalidInputException("Incorrect format. Correct form should be \"unmark i\", " +
                    "with i being an integer.");
        }
        int index = parseInt(inputAnalyzed[1]);
        list.unmark(index - 1);
        ui.showUnmarkSuccess(list.get(index - 1));
    }

    private void deleteOperation(String[] inputAnalyzed) throws InvalidInputException, IndexOutOfBoundsException,
            NumberFormatException {
        if (inputAnalyzed.length != 2) {
            throw new InvalidInputException("Incorrect format. Correct form should be \"delete i\", " +
                    "with i being an integer.");
        }
        int index = parseInt(inputAnalyzed[1]);
        Task temp = list.get(index - 1);
        list.remove(index - 1);
        ui.showDeleteSuccess(temp, list);
    }

    private void ddlOperation(String input) throws IndexOutOfBoundsException, InvalidInputException {
        String[] deadlineAnalyze = input.split("/by");
        String deadline;
        try {
            deadline = deadlineAnalyze[1].trim();
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("Missing deadline date.");
        }
        String deets = deadlineAnalyze[0].split("deadline")[1].trim();
        if (deets.equals("")) {
            throw new InvalidInputException("Missing deadline description.");
        }
        Deadline newDead = new Deadline(deets, deadline);
        list.add(newDead);
        ui.showAddTaskSuccess(newDead, list);
    }

    private void todoOperation(String input) throws InvalidInputException {
        //Possible Errors:
        //No descriptor
        String[] todoAnalyze = input.split("todo ");
        //Analyze
        Todo newTodo;
        try {
            newTodo = new Todo(todoAnalyze[1].trim());
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("Missing to-do description.");
        }
        //List
        list.add(newTodo);
        //Ui
        ui.showAddTaskSuccess(newTodo, list);
    }

    private void eventOperation(String input) throws IndexOutOfBoundsException, InvalidInputException {
        //Analyze
        String[] eventAnalyze;
        String[] timeAnalyze;
        try {
            eventAnalyze = input.split("/from");
            timeAnalyze = eventAnalyze[1].split("/to");
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidInputException("Missing /from or /to. Format is \"event [name] /from [time] /to [time]\"");
        }
        String start = timeAnalyze[0].trim();
        String over = timeAnalyze[1].trim();
        String details = eventAnalyze[0].split("event")[1].trim();
        if (start.equals("") || over.equals("") || details.equals("")) {
            throw new InvalidInputException("Missing details for at least one of the sections.");
        }
        //Add Item
        Event newEvent = new Event(details, start, over);
        list.add(newEvent);
        //Ui Section
        ui.showAddTaskSuccess(newEvent, list);
    }
}

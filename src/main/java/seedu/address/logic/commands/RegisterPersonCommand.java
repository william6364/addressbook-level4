package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.event.EpicEvent;
import seedu.address.model.event.exceptions.EventNotFoundException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Registers a person to an event.
 */
public class RegisterPersonCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "register";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Registers the person identified by the index number used in the last person listing"
            + " to a particular event.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1" + " AY201718 Graduation";

    public static final String MESSAGE_SUCCESS = "Registered person %1$s for event %2$s";
    public static final String MESSAGE_EVENT_NOT_FOUND = "The event specified cannot be found";

    private final Index targetIndex;
    private final String eventName;

    private Person personToRegister;
    private EpicEvent eventToRegisterFor;

    /**
     * Creates an RegisterPersonCommand to register the Person at targetIndex in the last person
     * listing for the EpicEvent with name eventName
     */
    public RegisterPersonCommand(Index targetIndex, String eventName) {
        requireAllNonNull(targetIndex, eventName);
        this.targetIndex = targetIndex;
        this.eventName = eventName;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireAllNonNull(personToRegister, eventToRegisterFor);
        try {
            model.registerPersonForEvent(personToRegister, eventToRegisterFor);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        } catch (EventNotFoundException enfe) {
            throw new CommandException(MESSAGE_EVENT_NOT_FOUND);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, personToRegister, eventToRegisterFor));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToRegister = lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RegisterPersonCommand // instanceof handles nulls
                && this.targetIndex.equals(((RegisterPersonCommand) other).targetIndex) // state check
                && Objects.equals(this.personToRegister, ((RegisterPersonCommand) other).personToRegister)
                && this.eventName.equals(((RegisterPersonCommand) other).eventName)
                && Objects.equals(this.eventToRegisterFor, ((RegisterPersonCommand) other).eventToRegisterFor));
    }
}

package com.assignment.exception;

import jakarta.persistence.EntityNotFoundException;

public class NoteNotFoundException extends EntityNotFoundException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}

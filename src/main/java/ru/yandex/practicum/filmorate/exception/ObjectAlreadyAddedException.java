package ru.yandex.practicum.filmorate.exception;

public class ObjectAlreadyAddedException extends RuntimeException {
    public ObjectAlreadyAddedException(String message) {
        super(message);
    }
}

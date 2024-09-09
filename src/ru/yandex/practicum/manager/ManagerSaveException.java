package ru.yandex.practicum.manager;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(final String message, Throwable e) {
        super(message, e);
    }
}

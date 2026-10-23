package service;

public class AlreadyTakenExeption extends RuntimeException {
    public AlreadyTakenExeption(String message) {
        super(message);
    }
}

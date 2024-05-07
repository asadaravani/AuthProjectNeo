package kg.beganov.AuthProject.service;

public interface EmailSenderService {
    void send(String to, String emil);
    String buildEmail(String name, String link);
}

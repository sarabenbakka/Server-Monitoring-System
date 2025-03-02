package com.monitoring;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailNotifier {
    private static EmailNotifier instance;
    private String emailTo = "";
    private final String username = "benbakkasara@gmail.com";
    private final String password = "vlgf jkpe kuld xipe"; 

    private EmailNotifier() {}

    public static EmailNotifier getInstance() {
        if (instance == null) {
            instance = new EmailNotifier();
        }
        return instance;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public void sendAlert(String serverId, String metricType, double value) {
        if (emailTo.isEmpty()) {
            System.out.println("Aucune adresse email configurée pour les alertes");
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.debug", "true"); 

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setSubject("Alerte Serveur - " + serverId);
            
            String content = String.format(
                "Alerte de surveillance serveur\n\n" +
                "Serveur: %s\n" +
                "Métrique: %s\n" +
                "Valeur: %.1f%%\n\n" +
                "Cette alerte a été générée automatiquement par le système de monitoring.",
                serverId, metricType, value
            );
            
            message.setText(content);

            Transport.send(message);
            System.out.println("Alerte envoyée à " + emailTo);
            
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.monitoring;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.ObservableList;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFExporter {
    public static void exportToPDF(ObservableList<ServerData> servers, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Ajouter le titre
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Rapport de Monitoring des Serveurs", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ajouter la date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Paragraph date = new Paragraph("Généré le : " + dateFormat.format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            document.add(new Paragraph("\n")); // Espace

            // Créer le tableau
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes du tableau
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            table.addCell(new PdfPCell(new Phrase("Serveur", headerFont)));
            table.addCell(new PdfPCell(new Phrase("CPU (%)", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Mémoire (%)", headerFont)));

            // Données des serveurs
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (ServerData server : servers) {
                table.addCell(new Phrase(server.getId(), cellFont));
                table.addCell(new Phrase(String.format("%.2f", server.getCpuUsage()), cellFont));
                table.addCell(new Phrase(String.format("%.2f", server.getMemoryUsage()), cellFont));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

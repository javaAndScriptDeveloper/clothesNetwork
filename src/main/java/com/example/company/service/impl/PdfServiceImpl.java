package com.example.company.service.impl;

import com.example.company.exception.PdfGenerationException;
import com.example.company.service.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateDocumentFromImages(List<BufferedImage> images) {
        var document = new Document();
        var outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
        } catch (DocumentException e) {
            throw new PdfGenerationException(e);
        }

        document.open();

        images.forEach(image -> {
            try {
                document.add(Image.getInstance(convertBufferedImageToBytes(image)));
            } catch (IOException | DocumentException exception) {
                throw new PdfGenerationException(exception);
            }
        });

        document.close();
        return outputStream.toByteArray();
    }

    public static byte[] convertBufferedImageToBytes(BufferedImage image) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

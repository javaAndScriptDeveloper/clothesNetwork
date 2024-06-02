package com.example.company.service;

import java.awt.image.BufferedImage;
import java.util.List;

public interface PdfService {

    byte[] generateDocumentFromImages(List<BufferedImage> image);
}

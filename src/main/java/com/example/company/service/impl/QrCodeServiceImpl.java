package com.example.company.service.impl;

import com.example.company.exception.QrCodeGenerationException;
import com.example.company.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    private final QRCodeWriter qrCodeWriter = new QRCodeWriter();

    @Value("${qr-code.width}")
    private Integer qrCodeWidth;

    @Value("${qr-code.height}")
    private Integer qrCodeHeight;

    @Override
    public BufferedImage generateQrCode(String content) {
        try {
            return MatrixToImageWriter.toBufferedImage(
                    qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight));
        } catch (WriterException exception) {
            throw new QrCodeGenerationException(content, exception);
        }
    }
}

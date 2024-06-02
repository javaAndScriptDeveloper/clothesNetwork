package com.example.company.service;

import java.awt.image.BufferedImage;

public interface QrCodeService {

    BufferedImage generateQrCode(String content);
}

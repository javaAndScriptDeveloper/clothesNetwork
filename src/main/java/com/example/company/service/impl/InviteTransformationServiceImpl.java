package com.example.company.service.impl;

import com.example.company.service.PdfService;
import com.example.company.service.QrCodeService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteTransformationServiceImpl implements InviteTransformationService {

    private final PdfService pdfService;
    private final QrCodeService qrCodeService;

    @Override
    public byte[] convertInviteUrlsToPdfWithQrCodes(List<String> inviteUrls) {
        var qrCodeImages =
                inviteUrls.stream().map(qrCodeService::generateQrCode).collect(Collectors.toList());
        return pdfService.generateDocumentFromImages(qrCodeImages);
    }
}

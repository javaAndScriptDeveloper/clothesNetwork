package com.example.company.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.company.dto.request.CreateInvitesRequest;
import com.example.company.service.InviteService;
import com.example.company.service.impl.InviteTransformationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "invites")
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/invites")
public class InviteController {

    private static final String PDF_WITH_QR_CODES_FILENAME = "invite_qr_codes.pdf";

    private final InviteService inviteService;
    private final InviteTransformationService inviteTransformationService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<?> createInvites(@RequestBody CreateInvitesRequest request) {

        var inviteUrls = inviteService.generateInviteUrls(request.getSize());
        return switch (request.getInviteFormatType()) {
            case URL -> generateUrlFormatTypeResponse(inviteUrls);
            case PDF_WITH_QR_CODES -> generatePdfWithQrCodesFormatTypeResponse(inviteUrls);
        };
    }

    private ResponseEntity<?> generateUrlFormatTypeResponse(List<String> inviteUrls) {
        return ResponseEntity.ok(inviteUrls);
    }

    private ResponseEntity<?> generatePdfWithQrCodesFormatTypeResponse(List<String> inviteUrls) {
        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=%s".formatted(PDF_WITH_QR_CODES_FILENAME));
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        var payload = inviteTransformationService.convertInviteUrlsToPdfWithQrCodes(inviteUrls);
        return new ResponseEntity<>(payload, headers, HttpStatus.OK);
    }
}

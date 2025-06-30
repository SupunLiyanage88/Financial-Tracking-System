package com.financetracker.financetrackersystem.controller.report;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.financetrackersystem.dto.report.ReportRequestDTO;
import com.financetracker.financetrackersystem.dto.report.ReportResponseDTO;
import com.financetracker.financetrackersystem.service.JWTService;
import com.financetracker.financetrackersystem.service.report.ReportService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final JWTService jwtService;

    @PostMapping("/report-generate")
    public ResponseEntity<ReportResponseDTO> generateReport(
        @RequestBody ReportRequestDTO req,
        HttpServletRequest request
    ){

        String jwtToken = jwtService.extractJwtFromCookie(request);
        ReportResponseDTO report = reportService.generateReport(req, jwtToken);

        return ResponseEntity.ok(report);
    }
    

}

package com.example.medappointmentscheduler.service.REST;

import com.example.medappointmentscheduler.domain.REST.Prescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PrescriptionClient {

    private final RestTemplate restTemplate;
    private final String prescriptionServiceUrl;

    public PrescriptionClient(RestTemplate restTemplate, @Value("${prescription.service.url}") String prescriptionServiceUrl) {
        this.restTemplate = restTemplate;
        this.prescriptionServiceUrl = prescriptionServiceUrl;
    }

    public Prescription getPrescriptionByPatientId(Long appointmentId, Long patientId) {
        String url = prescriptionServiceUrl + "/prescriptions/patient/" + patientId + "/appointment/" + appointmentId;
        ResponseEntity<Prescription> response = restTemplate.getForEntity(url, Prescription.class);
        return response.getBody();
    }

    public Prescription addPrescription(Prescription prescription) {
        String url = prescriptionServiceUrl + "/prescriptions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Prescription> requestEntity = new HttpEntity<>(prescription, headers);

        ResponseEntity<Prescription> response = restTemplate.postForEntity(url, requestEntity, Prescription.class);
        return response.getBody();
    }

    public void deletePrescription(Long appointmentId, Long patientId) {
        String url = prescriptionServiceUrl + "/prescriptions/patient/" + patientId + "/appointment/" + appointmentId;
        restTemplate.delete(url);
    }
}

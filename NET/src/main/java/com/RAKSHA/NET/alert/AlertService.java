package com.RAKSHA.NET.alert;

package com.RAKSHA.NET.AlertService;

import com.RAKSHA.NET.model.AlertEntity;
import com.RAKSHA.NET.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertEntity createAlert(AlertEntity alert) {

        alert.setCreatedAt(LocalDateTime.now());

        return alertRepository.save(alert);
    }

    public List<AlertEntity> getAllAlerts() {
        return alertRepository.findAll();
    }

    public AlertEntity getAlertById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
    }

    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}

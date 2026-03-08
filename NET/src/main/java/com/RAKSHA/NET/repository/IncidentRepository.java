package com.RAKSHA.NET.repository;


import com.RAKSHA.NET.enums.IncidentStatus;
import com.RAKSHA.NET.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
}
package com.RAKSHA.NET.repository;


import com.RAKSHA.NET.enums.ResponderStatus;
import com.RAKSHA.NET.model.Responder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponderRepository extends JpaRepository<Responder, Long> {
    List<Responder> findByStatus(ResponderStatus status);
}
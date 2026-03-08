package com.RAKSHA.NET.controller;


import com.RAKSHA.NET.dto.DispatchRequest;
import com.RAKSHA.NET.model.DispatchLog;
import com.RAKSHA.NET.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    // 3. SMART RESPONDER DISPATCH
    @PostMapping
    public ResponseEntity<DispatchLog> dispatch(@Valid @RequestBody DispatchRequest req) {
        return ResponseEntity.ok(dispatchService.dispatchNearest(req));
    }
}

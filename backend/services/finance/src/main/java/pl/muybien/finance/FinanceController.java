package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService service;

    @GetMapping("/{type}/{uri}")
    public ResponseEntity<FinanceResponse> getFinance(
            @PathVariable String type,
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(service.fetchFinance(type, uri));
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<FinanceFileDTO>> displayAvailableFinance(
            @PathVariable String type
    ) {
        return ResponseEntity.ok(service.displayAvailableFinance(type));
    }
}

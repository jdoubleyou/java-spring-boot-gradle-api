package com.sourceallies.boilerplate.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {
    @GetMapping("/public/info")
    public String getThing() {
        var thread = Thread.currentThread();
        return "Thread: %s with type %s".formatted(
            thread.getName(),
            thread.isVirtual() ? "virtual" : "non-virtual"
        );
    }
}

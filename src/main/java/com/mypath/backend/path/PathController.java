package com.mypath.backend.path;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/path")
public class PathController {
    private final PathService pathService;
    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

}

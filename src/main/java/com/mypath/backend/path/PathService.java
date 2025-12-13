package com.mypath.backend.path;

import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final PathRepository pathRepository;
    private final IdeaRepository ideaRepository;
    public PathService(PathRepository pathRepository, IdeaRepository ideaRepository) {
        this.pathRepository = pathRepository;
        this.ideaRepository = ideaRepository;
    }

}

package com.mypath.backend.path.service;

import com.mypath.backend.path.repository.IdeaRepository;
import com.mypath.backend.path.repository.PathRepository;
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

package com.tramo.backend.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// One-time (per boot) backfill of PUBLISH snapshots for projects published before that
// existed. Safe to run every boot — ProjectService.backfillMissingPublishSnapshots only
// touches projects it hasn't already snapshotted.
@Component
public class ProjectSnapshotBackfillRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ProjectSnapshotBackfillRunner.class);

    private final ProjectService projectService;

    public ProjectSnapshotBackfillRunner(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void run(ApplicationArguments args) {
        projectService.backfillMissingPublishSnapshots();
        log.info("ProjectSnapshot backfill check complete");
    }
}

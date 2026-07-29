package com.tramo.backend.project.snapshot;

import java.util.List;

// Plain nested tree serialized to JSON for ProjectSnapshot.content — not an API DTO.
public record ProjectSnapshotData(
        Long projectId,
        String title,
        String description,
        String visibility,
        String thumbnail,
        String tags,
        List<TrailData> trails
) {
    public record TrailData(Long id, String title, String description, String visibility, int version,
                             Long forkedFromId, List<ItemData> items) {
    }

    public record ItemData(Long id, String title, String type, String titleAlign, String content,
                            String annotation, Long associationId, List<AssociationData> associations) {
    }

    // The item's own outgoing link (from -> target), not the step's "arrived via" pointer (see ItemData.associationId).
    public record AssociationData(Long id, String type, String targetType, Long targetId, String targetTitle) {
    }
}

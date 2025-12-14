package com.mypath.backend.path.repository;

import com.mypath.backend.path.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
}

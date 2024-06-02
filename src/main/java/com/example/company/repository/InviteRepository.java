package com.example.company.repository;

import com.example.company.entity.InviteEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<InviteEntity, UUID> {}

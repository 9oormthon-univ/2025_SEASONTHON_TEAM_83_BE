package com.seasonthon.pleanet.badge.repository;

import com.seasonthon.pleanet.badge.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {
    Optional<Badge> findByName(String name);

}

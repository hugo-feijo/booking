package com.hostfully.interview.repository;

import com.hostfully.interview.model.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
}

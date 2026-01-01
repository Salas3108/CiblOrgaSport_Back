package com.ciblorgasport.repository;

import com.ciblorgasport.entity.Event;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {}

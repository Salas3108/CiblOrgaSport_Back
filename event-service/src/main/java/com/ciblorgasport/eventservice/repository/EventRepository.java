package com.ciblorgasport.eventservice.repository;

import com.ciblorgasport.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}

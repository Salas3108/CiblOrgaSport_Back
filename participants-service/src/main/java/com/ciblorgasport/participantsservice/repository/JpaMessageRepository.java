package com.ciblorgasport.participantsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Message;

@Repository
public interface JpaMessageRepository extends JpaRepository<Message, Long> {
}

package com.ciblorgasport.notificationsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ciblorgasport.notificationsservice.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("select n.idSpectateur from Notification n where n.sourceEventId = :sourceEventId and n.idSpectateur in :recipientIds")
	List<Long> findRecipientIdsBySourceEventIdAndIdSpectateurIn(
			@Param("sourceEventId") String sourceEventId,
			@Param("recipientIds") List<Long> recipientIds
	);
}

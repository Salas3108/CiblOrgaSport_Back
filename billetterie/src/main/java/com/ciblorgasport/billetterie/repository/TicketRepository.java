package com.ciblorgasport.billetterie.repository;

import com.ciblorgasport.billetterie.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("select t.basePrice from TicketModel t where t.category = :category")
	Double findBasePriceByCategory(@Param("category") String category);
}

package io.kermoss.cmd.domain.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;

@Repository
public interface CommandRepository extends JpaRepository<AbstractCommand, String> {
	@Query("SELECT out_cmd FROM OutboundCommand out_cmd WHERE out_cmd.id = :id")
	OutboundCommand findOutboundCommand(@Param("id") final String id);

	@Query("SELECT in_cmd FROM InboundCommand in_cmd WHERE in_cmd.id = :id")
	InboundCommand findInboundommand(@Param("id") final String id);

	@Query("SELECT out_cmd FROM AbstractCommand out_cmd WHERE out_cmd.LTX = :ltx AND out_cmd.destination=:destination")
	OutboundCommand findByLTX(@Param("ltx") final String ltx, @Param("destination") final String destination);

	@Query("SELECT in_cmd FROM AbstractCommand in_cmd WHERE in_cmd.FLTX = :fltx AND in_cmd.subject=:subject")
	InboundCommand findByFLTXAndSubject(@Param("fltx") final String fltx, @Param("subject") final String subject);

	default Optional<OutboundCommand> findOutboundCommandOpt(final String id) {
		return Optional.ofNullable(this.findOutboundCommand(id));
	}

	default Optional<InboundCommand> findInboundommandOpt(final String id) {
		return Optional.ofNullable(this.findInboundommand(id));
	}

	default Optional<AbstractCommand> findOneOpt(final String id) {
		return findById(id);
	}

	default boolean exists(@NotNull String id) {
		return this.existsById(id);
	};

	default AbstractCommand findOne(@NotNull String id) {
		return this.findById(id).get();
	}

	Optional<InboundCommand> findByRefId(final String refId);

	@Query("SELECT in_cmd FROM InboundCommand in_cmd WHERE in_cmd.id = :id and in_cmd.status = :status")
	Optional<InboundCommand> findByIdAndStatus(@Param("id") final String id,
			@Param("status") InboundCommand.Status status);

	@Query("SELECT in_cmd FROM InboundCommand in_cmd WHERE in_cmd.status = 'FAILED'")
	List<InboundCommand> failedInboundCommands();
}

package io.kermoss.cmd.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.InboundCommand.Status;
import io.kermoss.cmd.domain.OutboundCommand;

public class CommandRepositoryFake implements CommandRepository{

	@Override
	public List<AbstractCommand> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractCommand> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends AbstractCommand> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<AbstractCommand> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractCommand getOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AbstractCommand> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AbstractCommand> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AbstractCommand> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AbstractCommand> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractCommand findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	@Override
	public void delete(AbstractCommand entity) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public <S extends AbstractCommand> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AbstractCommand> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends AbstractCommand> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OutboundCommand findOutboundCommand(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InboundCommand findInboundommand(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutboundCommand findByLTX(String ltx, String destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InboundCommand findByFLTXAndSubject(String fltx, String subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<InboundCommand> findByRefId(String refId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<InboundCommand> findByIdAndStatus(String id, Status status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InboundCommand> failedInboundCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractCommand> findAllById(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AbstractCommand> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AbstractCommand> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends AbstractCommand> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends AbstractCommand> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

}

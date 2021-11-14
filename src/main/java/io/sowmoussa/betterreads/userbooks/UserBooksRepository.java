package io.sowmoussa.betterreads.userbooks;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserBooksRepository extends CassandraRepository<Userbooks, UserBooksPrimaryKey> {
    
}

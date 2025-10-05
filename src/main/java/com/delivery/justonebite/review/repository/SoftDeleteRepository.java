package com.delivery.justonebite.review.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T, ID> extends JpaRepository<T, ID> {

    @Override
    default void delete(T entity) {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override
    default void deleteById(ID id) {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override
    default void deleteAll(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override
    default void deleteAllById(Iterable<? extends ID> ids) {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override default void deleteAllInBatch() {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

    @Override default void deleteAllInBatch(Iterable<T> entities) {
        throw new UnsupportedOperationException("Use service.softDelete(...) instead.");
    }

}

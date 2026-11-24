package com.aerocast.repository;

import com.aerocast.model.FavoriteCityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCityRepository extends JpaRepository<FavoriteCityEntity, Long> {
    List<FavoriteCityEntity> findAllByOrderByDisplayOrderAscPinnedAtAsc();
    Optional<FavoriteCityEntity> findByCityNameIgnoreCase(String cityName);
    boolean existsByCityNameIgnoreCase(String cityName);
    void deleteByCityNameIgnoreCase(String cityName);
}

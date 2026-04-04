package com.ecommerce.stock.infrastructure.repository.reservation;

import com.ecommerce.stock.domain.entity.StockReservationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_reservations")
@Entity
public class StockReservationEntity {

    @Id
    private UUID id;

    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private StockReservationStatus status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockReservationItemEntity> items;
}

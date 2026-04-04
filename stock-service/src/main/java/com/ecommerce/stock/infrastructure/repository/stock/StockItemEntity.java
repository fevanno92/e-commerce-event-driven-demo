package com.ecommerce.stock.infrastructure.repository.stock;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_items")
@Entity
public class StockItemEntity {

    @Id
    private UUID id;
    private UUID productId;
    private int totalQuantity;
    private int reservedQuantity;
}

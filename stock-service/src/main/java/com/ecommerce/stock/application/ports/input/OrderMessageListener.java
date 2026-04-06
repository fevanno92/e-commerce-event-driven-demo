package com.ecommerce.stock.application.ports.input;

import com.ecommerce.stock.application.dto.ConfirmStockRequest;
import com.ecommerce.stock.application.dto.ReleaseStockRequest;
import com.ecommerce.stock.application.dto.ReserveStockRequest;

public interface OrderMessageListener {
    void reserveStock(ReserveStockRequest request);

    void confirmStock(ConfirmStockRequest request);

    void releaseStock(ReleaseStockRequest request);
}

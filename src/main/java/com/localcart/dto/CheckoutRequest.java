package com.localcart.dto;

import com.localcart.entity.DeliveryMode;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull
    private DeliveryMode deliveryMode;

    public CheckoutRequest() {
    }

    public DeliveryMode getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(DeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }
}
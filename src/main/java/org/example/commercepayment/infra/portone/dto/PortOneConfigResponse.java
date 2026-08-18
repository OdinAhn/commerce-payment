package org.example.commercepayment.infra.portone.dto;

public record PortOneConfigResponse(
        String storeId,
        String channelKey
) {}
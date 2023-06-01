package com.example.tialimentacao.utils;

public enum OrderStatus {
    ORDER_RECEIVED("Pedido recebido"),
    AWAITING_PAYMENT("Aguardando pagamento"),
    ORDER_PREPARING("Pedido em preparo"),
    ORDER_IN_ROUTE("Pedido em rota"),
    ORDER_DELIVERED("Pedido entregue"),
    ORDER_SUCCESS("Pedido finalizado"),
    ORDER_CANCELED("Pedido cancelado");


    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static boolean isValid(OrderStatus status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public String getStatus() {
        return status;
    }
}

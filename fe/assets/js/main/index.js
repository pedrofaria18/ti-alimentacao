function _startIndex(){
    _get('/products').then(produtos => {
        const now = new Date();
        const produtosDiv = document.getElementById("produtos");
        produtosDiv.innerHTML = produtos.length;

        const produtosDate = document.getElementById("produtosDate");
        produtosDate.innerHTML = "Atualizado em: " + _dateToFullDateString(now);
    });

    _get('/orders').then(pedidos => {
        const now = new Date();
        const pedidosDiv = document.getElementById("pedidos");
        pedidosDiv.innerHTML = pedidos.length;

        const pedidosDate = document.getElementById("pedidosDate");
        pedidosDate.innerHTML = "Atualizado em: " + _dateToFullDateString(now);
    });
}
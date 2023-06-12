function _startProdutoListagem() {
  _get('/products').then(responseData => {
    if (responseData.length > 0) {
      document.getElementById("noProducts").style.display = "none";
      const table = document.getElementById("table-container");
      table.innerHTML = `
                        <table class="table" id="table">
                          <thead>
                            <tr>
                              <th scope="col">#</th>
                              <th scope="col">Produto</th>
                              <th scope="col">Quantidade</th>
                              <th scope="col">Valor Unitário</th>
                            </tr>
                          </thead>
                        <tbody>
                          ${_getProdutoTBody(responseData)}
                        </tbody>
                      </table>`;
      table.style.display = "block";
    }
  }).catch(error => {
    console.error('Error:', error);
  });
}

function _getProdutoTBody(responseData) {
  let tBody = '';
  for (let i = 0; i < responseData.length; i++) {
    tBody += `
            <tr>
              <th scope="row">${i + 1}</th>
              <td>${responseData[i].name}</td>
              <td>${responseData[i].quantity}</td>
              <td>${_valueToMoney(responseData[i].price)}</td>
            </tr>`
  }
  return tBody;
}

function _startPedidoListagem(){
  _get('/orders').then(responseData => {
    if (responseData.length > 0) {
      document.getElementById("noProducts").style.display = "none";
      const table = document.getElementById("table-container");
      table.innerHTML = `
                        <table class="table" id="table">
                          <thead>
                            <tr>
                              <th scope="col">#</th>
                              <th scope="col">Empresa</th>
                              <th scope="col">Status</th>
                              <th scope="col">Pagamento</th>
                              <th scope="col">Produtos</th>
                              <th scope="col">Total</th>
                            </tr>
                          </thead>
                        <tbody>
                          ${_getPedidoTBody(responseData)}
                        </tbody>
                      </table>`;
      table.style.display = "block";
    }
  }).catch(error => {
    console.error('Error:', error);
  });
}

function _getPedidoTBody(responseData) {
  let tBody = '';
  for (let i = 0; i < responseData.length; i++) {
    tBody += `
            <tr>
              <th scope="row">${i + 1}</th>
              <td>${responseData[i].company}</td>
              <td>${_getStatus(responseData[i].status)}</td>
              <td>${responseData[i].paymentMethod}</td>
              <td>${_getProdutosInPedidos(responseData[i].products)}</td>
              <td>${_valueToMoney(responseData[i].total)}</td>
            </tr>`
  }
  return tBody;
}

function _getProdutosInPedidos(produtos){
  let result = [];
  for (let i = 0; i < produtos.length; i++) {
    result.push(`${produtos[i].name} (${produtos[i].quantity}x)`) ;
  }
  return result.join('<br>');
}

function _getStatus(status){
  switch (status) {
    case 'ORDER_IN_ROUTE':
      return 'Em Rota';
    case 'ORDER_DELIVERED':
      return 'Entregue';
    case 'ORDER_SUCCESS':
      return 'Sucesso';
    case 'ORDER_CANCELLED':
      return 'Cancelado';
    case 'AWAITING_PAYMENT':
      return 'Aguardando Pagamento';
    default:
      return 'Status Desconhecido';
  }

}
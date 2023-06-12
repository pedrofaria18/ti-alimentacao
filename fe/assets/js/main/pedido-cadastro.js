var PRODUTO_SELECT_INDEX = 0;

function _startPedidoCadastro(){
    _addProdutoQuantidadeItem();

    const addButton = document.getElementById("add");
    addButton.addEventListener("click", function() {
        _addProdutoQuantidadeItem();
    });

    const deleteButton = document.getElementById("delete");
    deleteButton.addEventListener("click", function() {
        _deleteProdutoQuantidadeItem();
    });

    const submitButton = document.getElementById("submit");
    submitButton.addEventListener("click", function() {
        _submitPedido();
    });
}

function _addProdutoQuantidadeItem(){
    const div = document.getElementById("productQuantity");
    div.innerHTML += `
    <div id="pq${PRODUTO_SELECT_INDEX}">
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Produto</label>
            <div class="col-sm-10">
                <select id="select${PRODUTO_SELECT_INDEX}" class="form-select" onchange="_updateTotal()">
                    <option selected="">Selecione</option>
                    <option value="1">Produto 1</option>
                    <option value="2">Produto 2</option>
                    <option value="3">Produto 3</option>
                </select>
            </div>
        </div>
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Quantidade</label>
            <div class="col-sm-10">
                <input id="quantity${PRODUTO_SELECT_INDEX}" type="number" class="form-control" onchange="_updateTotal()">
            </div>
        </div>
  </div>`;
  PRODUTO_SELECT_INDEX++;
}

function _deleteProdutoQuantidadeItem(){
    if (PRODUTO_SELECT_INDEX > 1){
        PRODUTO_SELECT_INDEX--
        const parent = document.getElementById("productQuantity");
        const element = document.getElementById(`pq${PRODUTO_SELECT_INDEX}`);
        parent.removeChild(element);
    }
}

function _updateTotal(){

}

function _submitPedido(){

}
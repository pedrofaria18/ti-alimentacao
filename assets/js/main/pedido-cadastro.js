var PQ_INDEX = 0;

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

}

function _addProdutoQuantidadeItem(){
    const div = document.getElementById("productQuantity");
    div.innerHTML += `
    <div id="pq${PQ_INDEX}">
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Produto</label>
            <div class="col-sm-10">
                <select id="select${PQ_INDEX}" class="form-select">
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
                <input id="quantity${PQ_INDEX}" type="number" class="form-control">
            </div>
        </div>
  </div>`;
  PQ_INDEX++;
}

function _deleteProdutoQuantidadeItem(){
    if (PQ_INDEX > 1){
        PQ_INDEX--
        const parent = document.getElementById("productQuantity");
        const element = document.getElementById(`pq${PQ_INDEX}`);
        parent.removeChild(element);
    }
}

function _updateTotal(){

}

function _submit(){

}
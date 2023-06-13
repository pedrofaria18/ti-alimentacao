var PRODUTO_SELECT_INDEX = 0;
var PRODUTOS;
var OPTIONS_SELECT = ""

function _startPedidoCadastro() {
    _get('/products').then(produtos => {
        PRODUTOS = produtos;
        OPTIONS_SELECT = _getProdutosOptions();
        _addProdutoQuantidadeItem();

        const addButton = document.getElementById("add");
        addButton.addEventListener("click", function () {
            _addProdutoIfNotExist();
        });

        const deleteButton = document.getElementById("delete");
        deleteButton.addEventListener("click", function () {
            _deleteProdutoQuantidadeItem();
        });

        const submitButton = document.getElementById("submit");
        submitButton.addEventListener("click", function (event) {
            event.preventDefault();
            const company = document.getElementById("inputCompany").value || document.getElementById("inputCompany").placeholder;
            const paymentMethod = document.getElementById("inputPayment").value;
            const total = document.getElementById("outputTotal").value;
            _postPedidoIfValid(company, paymentMethod, total);
        });
    }).catch(error => {
        _badgeError('Erro: Não foi possível carregar os produtos: ' + error);
    });

}

function _addProdutoQuantidadeItem() {
    const div = document.getElementById("productQuantity");
    const newDiv = document.createElement('div');
    newDiv.id = `pq${PRODUTO_SELECT_INDEX}`;
    newDiv.innerHTML = `
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Produto</label>
            <div class="col-sm-10">
                <select id="select${PRODUTO_SELECT_INDEX}" class="form-select" onchange="_processSelect(${PRODUTO_SELECT_INDEX})">
                    <option selected="Selecione">Selecione</option>
                    ${OPTIONS_SELECT}
                </select>
            </div>
        </div>
        <div class="row mb-3">
            <label class="col-sm-2 col-form-label">Quantidade</label>
            <div class="col-sm-10">
                <input id="quantity${PRODUTO_SELECT_INDEX}" type="number" class="form-control" placeholder="0" onchange="_processQuantity(${PRODUTO_SELECT_INDEX})">
            </div>
        </div>
    `;
    
    div.appendChild(newDiv);

    const select = document.getElementById(`select${PRODUTO_SELECT_INDEX}`);
    select.addEventListener("change", function () {

    });
    PRODUTO_SELECT_INDEX++;
}


function _addProdutoIfNotExist() {
    let hasEmpty = false;
    for (let i = 0; i < PRODUTO_SELECT_INDEX; i++) {
        const select = document.getElementById(`select${i}`);
        const quantity = document.getElementById(`quantity${i}`);
        if (select.value == "Selecione" || quantity.value == "") {
            hasEmpty = true;
            break;
        }
    }
    if (hasEmpty) {
        _badgeWarning('Preencha todos os campos de <b>Produto</b> e <b>Quantidade</b> antes de adicionar um novo produto.');
    } else {
        _addProdutoQuantidadeItem();
    }
}

function _processSelect(i){
    const quantity = document.getElementById(`quantity${i}`);
    const value = document.getElementById(`select${i}`).value;
    if (value == "Selecione") {
        quantity.placeholder = "0";
    } else {
        quantity.placeholder = "Máximo: " + _getQuantityById(value);
        if (quantity.value != "") {
            _processQuantity(i);
        }
    }
    _updateTotal();
}

function _processQuantity(i) {
    const select = document.getElementById(`select${i}`).value;
    const quantity = document.getElementById(`quantity${i}`);
    const value = quantity.value;
    const max = _getQuantityById(select);

    if (!isNaN(value) && value != "" && value > 0 && Number.isInteger(parseFloat(value))) {
        if (value > max) {
            _badgeWarning(`A quantidade máxima para o produto <b>${_getNameByID(select)}</b> é <b>' ${max} '</b>.`);
            quantity.value = max;
        }
    } else {
        _badgeWarning('Preencha o campo <b>Quantidade</b> com um valor inteiro maior que 0.');
        quantity.value = "";
    }
    _updateTotal();
}

function _getNameByID(id) {
    const item = PRODUTOS.find(item => item.id === id);
    return item ? item.name : "?";
}

function _getPriceByID(id) {
    const item = PRODUTOS.find(item => item.id === id);
    return item ? item.price : "?";
}

function _getQuantityById(id) {
    const item = PRODUTOS.find(item => item.id === id);
    return item ? item.quantity : "?";
}

function _getProdutosOptions() {
    let OPTIONS_SELECT = '';
    PRODUTOS.forEach(PRODUTO => {
        OPTIONS_SELECT += `<option value="${PRODUTO.id}">${PRODUTO.name} (${_valueToMoney(PRODUTO.price)})</option>`;
    });
    return OPTIONS_SELECT;
}

function _deleteProdutoQuantidadeItem() {
    if (PRODUTO_SELECT_INDEX > 1) {
        PRODUTO_SELECT_INDEX--
        const parent = document.getElementById("productQuantity");
        const element = document.getElementById(`pq${PRODUTO_SELECT_INDEX}`);
        parent.removeChild(element);
    }
}

function _updateTotal() {
    let result = 0;
    const total = document.getElementById(`outputTotal`);
    for (let i = 0; i < PRODUTO_SELECT_INDEX; i++) {
        const id = document.getElementById(`select${i}`).value;
        const quantityDiv = document.getElementById(`quantity${i}`);
        if (id != "Selecione" && quantityDiv.value != "") {
            const price = parseFloat(_getPriceByID(id));
            const quantity = parseInt(quantityDiv.value);
            result += (price * quantity);
        }
    }
    total.value = _valueToMoney(result);
}

function _postPedidoIfValid(company, paymentMethod, total) {
    let isValid = true;

    if (company == "") {
        isValid = false;
        _badgeWarning('Preencha o campo <b>Empresa</b>.');
    }

    if (paymentMethod == "Selecione") {
        isValid = false;
        _badgeWarning('Selecione uma opção de <b>Pagamento</b>.');
    }

    const totalValue = _moneyToValue(total);

    if (totalValue <= 0) {
        isValid = false;
        _badgeWarning('Adicione pelo menos um produto.');
    }

    let products = [];

    for (let i = 0; i < PRODUTO_SELECT_INDEX; i++) {
        const id = document.getElementById(`select${i}`).value;
        const quantityDiv = document.getElementById(`quantity${i}`);
        if (id != "Selecione" && quantityDiv.value != "") {
            const product = {
                name: _getNameByID(id),
                quantity: parseInt(quantityDiv.value),
            }
            products.push(product);
        }
    }

    const data = {
        company: company,
        paymentMethod: paymentMethod,
        total: totalValue,
        products: products
    }

    if (isValid) {
        _postPedido(data);
    }
}

async function _postPedido(data) {
    try {
        const response = await fetch(HOST + "/orders", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            _badgeSuccess("Pedido cadastrado com sucesso! 🎉");
            _clearPedidoInputs();
        } else {
            _badgeError('<b>Falha na Requisição</b> 😖: ' + response.status);
            console.error(response.status);
        }
    } catch (error) {
        _badgeError('<b>Erro</b> 😖: '+ error);
        console.error(error);
    }
}

function _clearPedidoInputs() {
    const company = document.getElementById("inputCompany");
    const paymentMethod = document.getElementById("inputPayment");
    const total = document.getElementById("outputTotal");

    company.value = "";
    paymentMethod.value = "Selecione";
    total.value = _valueToMoney(0);

    document.getElementById("productQuantity").innerHTML = "";
    PRODUTO_SELECT_INDEX = 0;

    _startPedidoCadastro()
}
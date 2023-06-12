function _startProdutoCadastro() {
    const name = document.getElementById("inputName");
    const number = document.getElementById("inputNumber");
    number.addEventListener("change", function () {
        _validateValorUnitario(number);
    });

    const quantity = document.getElementById("inputQuantity");
    quantity.addEventListener("change", function () {
        _validateQuantidade(quantity);
    });

    const submitButton = document.getElementById("submit");
    submitButton.addEventListener("click", function (event) {
        event.preventDefault();
        const nome = name.value;
        const valor = number.value
        const quantidade = quantity.value || 1;

        if (_validadeProdutoInputs(nome, valor)) {
            _postProduto(nome, valor, quantidade);
        }

    });
}

function _validateValorUnitario(div) {
    const value = div.value;
    if (isNaN(value) || parseFloat(value) < 0) {
        div.value = "";
        _badgeWarning("<b>Valor Unitário</b> inválido");
    }
}

function _validateQuantidade(div) {
    const value = div.value;
    if (isNaN(value) || parseFloat(value) < 1 || !Number.isInteger(parseFloat(value))) {
        div.value = "";
        _badgeWarning("<b>Quantidade</b> inválida");
    }
}

function _validadeProdutoInputs(nome, valor) {
    if (nome === "" && valor === "") {
        _badgeWarning("Preencha os campos de <b>Nome</b> e de <b>Valor Unitário</b>");
        return false;
    } else if (nome === "") {
        _badgeWarning("Preencha o campo de <b>Nome</b>");
        return false;
    } else if (valor === "") {
        _badgeWarning("Preencha o campo de <b>Valor Unitário</b>");
        return false;
    } else return true;
}

async function _postProduto(nome, valor, quantidade) {
    const data = {
        name: nome,
        price: parseFloat(valor),
        quantity: parseInt(quantidade)
    }
    try {
        const response = await fetch(HOST + "/products", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            _badgeSuccess("Produto <b>" + nome + "</b> cadastrado com sucesso! 🎉");
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
    const name = document.getElementById("inputName");
    const number = document.getElementById("inputNumber");
    const quantity = document.getElementById("inputQuantity");
    name.value = "";
    number.value = "";
    quantity.value = "";
}
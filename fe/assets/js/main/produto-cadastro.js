function _startProdutoCadastro() {
    const submitButton = document.getElementById("submit");
    submitButton.addEventListener("click", function() {
        _submitPedido();
    });
}
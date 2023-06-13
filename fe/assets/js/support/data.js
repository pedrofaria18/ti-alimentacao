const HOST = 'http://localhost:8080';

async function _post(url, data) {
    try {
        const response = await fetch(HOST + url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const responseData = await response.json();
            return responseData
        } else {
            console.error(response.status);
        }
    } catch (error) {
        console.error(error);
    }
}

async function _get(url) {
    try {
        const response = await fetch(HOST + url);
        if (response.ok) {
            const responseData = await response.json();
            return responseData
        } else {
            console.error(response.status);
        }
    } catch (error) {
        console.error(error);
    }
}

function _valueToMoney(value) {
    return value.toLocaleString('pt-br', { style: 'currency', currency: 'BRL' });
}

function _moneyToValue(money) {
    return parseFloat(money.replace("R$", "").replace(",", "."));
}
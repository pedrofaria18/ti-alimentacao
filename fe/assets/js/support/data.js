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
            console.error('Request failed. Status:', response.status);
        }
    } catch (error) {
        console.error('Request failed:', error);
    }
}

async function _get(url) {
    try {
        const response = await fetch(HOST + url);
        if (response.ok) {
            const responseData = await response.json();
            return responseData
        } else {
            console.error('Request failed. Status:', response.status);
        }
    } catch (error) {
        console.error('Request failed:', error);
    }
}

function _intToMoney(value) {
    return value.toLocaleString('pt-br', { style: 'currency', currency: 'BRL' });
}
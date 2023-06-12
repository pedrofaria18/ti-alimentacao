function _badgeSuccess(message){
    const alerts = document.getElementById("alerts");
    if (alerts === null) return;
    alerts.innerHTML += `
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle me-1"></i>
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>`
}

function _badgeError(message){
    const alerts = document.getElementById("alerts");
    if (alerts === null) return;
    alerts.innerHTML += `
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-octagon me-1"></i>
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>`
}

function _badgeWarning(message){
    const alerts = document.getElementById("alerts");
    if (alerts === null) return;
    alerts.innerHTML += `
                    <div class="alert alert-warning alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle me-1"></i>
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>`
}
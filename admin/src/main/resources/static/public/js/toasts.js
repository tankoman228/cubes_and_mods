export function showSuccessToast(message) {
    Vue.toasted.show(`<img src="/public/icons/success.png" style="width: auto; height: 2.3rem; margin-left: 2px;"> ${message}`, {
        type: 'success',
        duration: 4000,
        theme: 'toasted-success',
        position: "bottom-right",
    });
}

export function showErrorToast(message) {
    Vue.toasted.show(`<img src="/public/icons/error.png" style="width: auto; height: 2.3rem; margin-left: 2px;"> ${message}`, {
        type: 'error',
        duration: 5000,
        theme: 'toasted-error',
        position: "bottom-right",
    });
}

export function showInfoToast(message) {
    Vue.toasted.show(`<img src="/public/icons/info.png" style="width: auto; height: 2.3rem; margin-left: 2px;"> ${message}`, {
        type: 'info',
        duration: 8500,
        theme: 'toasted-info',
        position: "bottom-right",
    });
}

export function showWarningToast(message) {
    Vue.toasted.show(`<img src="/public/icons/warning.png" style="width: auto; height: 2.3rem; margin-left: 2px;"> ${message}`, {
        type: 'warning',
        duration: 8500,
        theme: 'toasted-warning',
        position: "bottom-right",
    });
}
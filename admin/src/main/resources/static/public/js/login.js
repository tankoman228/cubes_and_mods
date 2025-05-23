import * as Toasts from "/public/js/toasts.js";

const app = new Vue({
    el: '#app',
    data: {
        username: '',
        password: ''
        
    },
    methods: {
        login() {
            axios.post('/api/loginn/login', {
                username: this.username,
                passwordHash: this.password,
            },
            {
                withCredentials: true // Включает передачу сессионных cookie
            }).then(response => {
                
                console.log(response);
                Toasts.showSuccessToast("Вы вошли в систему!");
                setTimeout(() => {
                    window.location.href = '/home';
                }, 1000);
                                  
            }).catch(error => {
                console.error(error);
                console.error(error.response.status);
                if (error.response.status == 400) {
                    Toasts.showErrorToast("Неверный логин или пароль!");
                }
                else if (error.response.status == 404) {
                    Toasts.showErrorToast("Пользователь не найден!");
                }   
                else if (error.response.status == 403) {
                    Toasts.showErrorToast("Слишком много попыток входа! Доступ временно заблокирован!");
                }   
                else {
                    Toasts.showErrorToast("Произошла неизвестная ошибка!");
                }     
            });
        }
    }
});

Vue.use(BootstrapVue);
Vue.use(Toasted);
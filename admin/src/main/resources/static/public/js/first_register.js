import * as Toasts from "/public/js/toasts.js";
//Toasts.showErrorToast("Ошибка при регистрации!");

const app = new Vue({
    el: '#app',
    data: {
        username: '',
        password: '',
        confirmPassword: '' 
    },
    methods: {
        register() {

            // Пароль
            const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).+$/;
            if (!passwordRegex.test(this.password)) {
                Toasts.showWarningToast("Пароль должен содержать хотя бы одну цифру, заглавную и строчную букву!");
                return;
            }  
            if (this.password !== this.confirmPassword) {
                Toasts.showWarningToast("Пароли не совпадают!");
                return;
            }

            axios.post('/api/loginn/first_register', {
                'username': this.username,
                'passwordHash': this.password,

            }).then(response => {
                
                console.log(response);
                Toasts.showSuccessToast("Регистрация прошла успешно!");
                setTimeout(() => {
                    window.location.href = '/home';
                }, 1500);
                                  
            }).catch(error => {
                console.error(error);
                Toasts.showErrorToast(error.response.data.message || 'Произошла ошибка!');          
            });
        }
    }
});
Vue.use(BootstrapVue);
Vue.use(Toasted);
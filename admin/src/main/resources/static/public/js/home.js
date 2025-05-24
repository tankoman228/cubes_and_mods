import * as Toasts from "/public/js/toasts.js";

const app = new Vue({
    el: '#app',
    data: {
        user: {}
    },
    methods: {
        
        logout() {
            axios.post('/api/loginn/logout')
            .then(response => {
                console.log(response);
                Toasts.showSuccessToast("Вы вышли из системы!");
                setTimeout(() => {
                    window.location.href = '/login';
                }, 1500);
            })
            .catch(error => {
                console.error(error);
            });
        },
        initSessionInfo() {

            axios.get('/api/loginn/session_info')
            .then(response => {    
                console.log(response.data);
                console.log(response);
                
                this.user = response.data;
                this.$forceUpdate();
            })
            .catch(error => {
                console.error(error);
            });
        }
    }
});
Vue.use(BootstrapVue);
Vue.use(Toasted);

//showNotifications();
app.initSessionInfo();
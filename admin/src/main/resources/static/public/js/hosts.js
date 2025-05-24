import * as Toasts from "/public/js/toasts.js";

const app = new Vue({
    el: '#app',
    data: {
        hosts: [],
        previous_count: 0,
    },
    methods: {
        loadHosts() {
            axios.get('/api/hosts')
                .then(response => {
                    this.hosts = response.data;

                    if (this.previous_count != 0) { // Вызовется только после попытки удаления
                        if (this.previous_count == this.hosts.length) {
                            Toasts.showErrorToast("Хосты не изменились. Неизвестная ошибка");
                        }
                        else {
                            Toasts.showSuccessToast("Список хостов обновлён успешно");
                        }
                    }
                    
                    this.previous_count = this.hosts.length;
                })
                .catch(error => {
                    Toasts.showErrorToast("Ошибка загрузки хостов.");
                    console.error(error);
                });
        },
        confirmDelete(host) {
            if (confirm(`Удалить хост "${host.name}"? Это необратимая операция. Если ошибиться, клиент потеряет игровой мир и будет недоволен, в общем, соглашайтесь, но только в случае нарушения пользовательского соглашения или когда слишком долго не продлевают`)) {
                this.deleteHost(host.id);
            }
        },
        deleteHost(id) {
            axios.delete(`/api/hosts/${id}`)
                .then(() => {
                    Toasts.showSuccessToast("Хост удалён");
                    this.loadHosts();
                })
                .catch(error => {
                    //Toasts.showToast("Получено сообщение: " + error.response?.data?.message);
                    console.error(error);
                });
        }
    },
    mounted() {
        this.loadHosts();
    }
});

Vue.use(BootstrapVue);
Vue.use(Toasted);

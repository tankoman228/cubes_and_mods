import * as Toasts from "/public/js/toasts.js";

const app = new Vue({
    el: '#app',
    data: {
        users: [],
        showModal: false,
        modalTitle: '',
        selectedUser: null
    },
    created() {
        this.loadUsers();
    },
    methods: {
        loadUsers() {
            axios.get('/api/admins')
                .then(response => {
                    this.users = response.data;
                })
                .catch(error => {
                    console.error(error);
                    Toasts.showErrorToast("Ошибка при загрузке списка администраторов!");
                });
        },
        showAddModal() {
            this.selectedUser = {
                email: '',
                passwordHash: '',
                passwordConfirm: '',
                canViewStats: false,
                canViewLogs: false,
                canClients: false,
                canHosts: false,
                canOrders: false,
                canServers: false,
                canMonitorSrv: false,
                canTariffs: false,
                canAdmins: false
            };
            this.modalTitle = 'Добавление администратора';
            this.showModal = true;
        },
        editUser(user) {
            this.selectedUser = {...user};
            this.modalTitle = 'Обновление администратора';
            this.showModal = true;
            this.selectedUser.email = user.username;
            this.selectedUser.passwordHash = '';
            this.selectedUser.passwordConfirm = '';
        },
        saveUser() {
            if (this.selectedUser.passwordHash != this.selectedUser.passwordConfirm) {
                Toasts.showErrorToast("Пароли не совпадают!");
                return;
            }

            if (this.selectedUser.id == null || this.selectedUser.passwordHash != null && this.selectedUser.passwordHash != "" ) {
                const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
                if (!passwordRegex.test(this.selectedUser.passwordHash)) {
                    Toasts.showErrorToast("Пароль должен содержать минимум 8 символов, включая буквы разного регистра и цифры!");
                    return;
                }
            }

            const userData = {
                username: this.selectedUser.email,
                passwordHash: this.selectedUser.passwordHash,
                canViewStats: this.selectedUser.canViewStats,
                canViewLogs: this.selectedUser.canViewLogs,
                canClients: this.selectedUser.canClients,
                canHosts: this.selectedUser.canHosts,
                canOrders: this.selectedUser.canOrders,
                canServers: this.selectedUser.canServers,
                canMonitorSrv: this.selectedUser.canMonitorSrv,
                canTariffs: this.selectedUser.canTariffs,
                canAdmins: this.selectedUser.canAdmins
            };

            const request = this.selectedUser.id != null
                ? axios.put(`/api/admins/${this.selectedUser.id}`, userData)
                : axios.post('/api/admins', userData);

            request.then(response => {
                Toasts.showSuccessToast(this.selectedUser.id ? "Администратор обновлен!" : "Администратор добавлен!");
                this.showModal = false;
                this.loadUsers();
            }).catch(error => {
                console.error(error);
                Toasts.showErrorToast("Ошибка при сохранении администратора!");
            });
        }
    }
});

Vue.use(BootstrapVue);
Vue.use(Toasted);
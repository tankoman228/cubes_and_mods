const app = new Vue({
    el: '#app',
    data() {
        return {
            servers: [],
            fields: [
                { key: 'id', label: 'ID' },
                { key: 'name', label: 'Название' },
                { key: 'address', label: 'Адрес' },
                { key: 'resources', label: 'Ресурсы' },
                { key: 'actions', label: 'Действия' }
            ],
            showModal: false,
            isEditing: false,
            showWarning: false,
            currentServer: this.getEmptyServer(),
            modalTitle: ''
        }
    },
    mounted() {
        this.loadServers();
    },
    methods: {
        loadServers() {
            axios.get('/api/servers')
                .then(response => {
                    this.servers = response.data;
                })
                .catch(error => {
                    console.error('Error loading servers:', error);
                    alert('Ошибка загрузки серверов');
                });
        },
        showAddModal() {
            this.isEditing = false;
            this.currentServer = this.getEmptyServer();
            this.modalTitle = 'Добавить сервер';
            this.showModal = true;
            this.showWarning = false;
        },
        editServer(server) {
            this.isEditing = true;
            this.currentServer = { ...server };
            this.modalTitle = 'Редактировать сервер';
            this.showModal = true;
            this.showWarning = false;
        },
        saveServer() {
            if (!this.isEditing) {
                // При создании устанавливаем свободные ресурсы равными общим
                this.currentServer.cpu_threads_free = this.currentServer.cpu_threads;
                this.currentServer.ram_free = this.currentServer.ram;
                this.currentServer.memory_free = this.currentServer.memory;
            }

            const request = this.isEditing 
                ? axios.put(`/api/servers/${this.currentServer.id}`, this.currentServer)
                : axios.post('/api/servers', this.currentServer);
            
            request.then(() => {
                this.showModal = false;
                this.loadServers();
            })
            .catch(error => {
                console.error('Error saving server:', error);
                alert('Ошибка сохранения сервера');
            });
        },
        confirmDelete(server) {
            if (confirm(`Удалить сервер "${server.name}"?`)) {
                axios.delete(`/api/servers/${server.id}`)
                    .then(() => {
                        this.loadServers();
                    })
                    .catch(error => {
                        console.error('Error deleting server:', error);
                        alert('Ошибка удаления сервера');
                    });
            }
        },
        resetModal() {
            this.currentServer = this.getEmptyServer();
        },
        getEmptyServer() {
            return {
                name: '',
                address: '',
                cpu_name: '',
                cpu_threads: 0,
                cpu_threads_free: 0,
                ram: 0,
                ram_free: 0,
                memory: 0,
                memory_free: 0
            };
        },
        showResourceWarning() {
            if (this.isEditing) {
                this.showWarning = true;
            }
        }
    }
});

Vue.use(BootstrapVue);
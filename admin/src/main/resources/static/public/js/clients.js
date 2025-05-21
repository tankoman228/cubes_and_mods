new Vue({
    el: '#app',
    data: {
        clients: [],
        error: null,
        fields: [
            { key: 'id', label: 'ID' },
            { key: 'email', label: 'Email' },
            { key: 'banned', label: 'Статус' },
            { key: 'additional_info', label: 'Доп. информация' }
        ]
    },
    mounted() {
        this.loadClients();
    },
    methods: {
        loadClients() {
            axios.get('/api/admin/clients')
                .then(response => {
                    this.clients = response.data;
                })
                .catch(error => {
                    this.error = 'Ошибка загрузки клиентов: ' + error.message;
                });
        },
        toggleBan(client) {
            axios.post(`/api/admin/clients/${client.id}/toggle-ban`)
                .then(() => {
                    client.banned = !client.banned;
                })
                .catch(error => {
                    this.error = 'Ошибка изменения статуса: ' + error.message;
                });
        },
        updateClient(client) {
            axios.put(`/api/admin/clients/${client.id}`, {
                additional_info: client.additional_info
            })
            .catch(error => {
                this.error = 'Ошибка обновления клиента: ' + error.message;
            });
        }
    }
});
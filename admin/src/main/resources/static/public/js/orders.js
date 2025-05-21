new Vue({
    el: '#app',
    data: {
        orders: [],
        error: null,
        showClosed: false,
        showConfirm: false,
        selectedOrder: null,
        fields: [
            { key: 'code', label: 'Код' },
            { key: 'client', label: 'Клиент' },
            { key: 'server', label: 'Сервер' },
            { key: 'tariff', label: 'Тариф' },
            { key: 'host', label: 'Хост' },
            { key: 'made_at', label: 'Создан', formatter: v => v.slice(0, 10) },
            { key: 'confirmed', label: 'Подтвержден', formatter: v => v ? 'Да' : 'Нет' },
            { key: 'closed_at', label: 'Закрыт', formatter: v => v ? v.slice(0, 10) : '-' },
            { key: 'actions', label: 'Действия' }
        ]
    },
    mounted() {
        this.loadOrders();
    },
    methods: {
        loadOrders() {
            axios.get(`/api/admin/orders?showClosed=${this.showClosed}`)
                .then(response => {
                    this.orders = response.data;
                })
                .catch(error => {
                    this.error = 'Ошибка загрузки заказов: ' + error.message;
                });
        },
        confirmAccept(order) {
            this.selectedOrder = order;
            this.showConfirm = true;
        },
        acceptOrder(order) {
            axios.post(`/api/admin/orders/${order.code}/accept`)
                .then(() => this.loadOrders())
                .catch(error => {
                    this.error = 'Ошибка принятия заказа: ' + error.message;
                });
        },
        rejectOrder(order) {
            axios.post(`/api/admin/orders/${order.code}/reject`)
                .then(() => this.loadOrders())
                .catch(error => {
                    this.error = 'Ошибка отклонения заказа: ' + error.message;
                });
        }
    }
});
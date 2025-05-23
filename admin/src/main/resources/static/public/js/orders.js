import * as Toasts from "/public/js/toasts.js";

new Vue({
    el: '#app',
    data: {
        orders: [],
        error: null,
        showClosed: false,
        showConfirm: false,
        selectedOrder: null
    },
    computed: {
        ordersFiltered() {
            return this.showClosed ? this.orders : this.orders.filter(order => !order.closed_at);
        }
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
                    Toasts.showErrorToast("Ошибка! " + error.message);
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
                    Toasts.showErrorToast("Ошибка! " + error.message);
                });
        },
        rejectOrder(order) {
            axios.post(`/api/admin/orders/${order.code}/reject`)
                .then(() => this.loadOrders())
                .catch(error => {
                    Toasts.showErrorToast("Ошибка! " + error.message);
                });
        }
    }
});

Vue.use(BootstrapVue);
Vue.use(Toasted);
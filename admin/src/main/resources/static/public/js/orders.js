import * as Toasts from "/public/js/toasts.js";

new Vue({
    el: '#app',
    data: {
        orders: [],
        error: null,
        showClosed: false,
        showConfirm: false,
        selectedOrder: null,
        action: null,
    },
    computed: {
        ordersFiltered() {
            return this.showClosed ? 
            this.orders.filter(order => order.closedAt != null) : 
            this.orders.filter(order => order.closedAt == null);
        }
    },
    mounted() {
        this.loadOrders();
    },
    methods: {
        loadOrders() {
            axios.get(`/api/admin/orders`)
                .then(response => {
                    this.orders = response.data;
                })
                .catch(error => {
                    Toasts.showErrorToast("Ошибка! " + error.message);
                });
        },
        confirmAsk(order, action) {
            this.selectedOrder = order;
            this.showConfirm = true;
            this.action = action;
        },
        confirmAction() {
            if (this.action == 'accept') {
                this.acceptOrder(this.selectedOrder);
            } else if (this.action == 'reject') {
                this.rejectOrder(this.selectedOrder);
            }
            this.showConfirm = false;
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
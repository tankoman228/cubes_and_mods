import * as Toasts from "/public/js/toasts.js";


const app = new Vue({
    el: '#app',
    data() {
        return {
            tariffs: [],
            fields: [
                { key: 'id', label: 'ID' },
                { key: 'name', label: 'Название' },
                { key: 'cost_rub', label: 'Стоимость ₽' },
                { key: 'hours_work_max', label: 'Часов рантайма' },
                { key: 'ram', label: 'ОЗУ (ГБ)' },
                { key: 'cpu_threads', label: 'Потоки ЦП' },
                { key: 'memory_limit', label: 'Лимит ПЗУ (КБ)' },
                { key: 'enabled', label: 'Статус' },
                { key: 'actions', label: 'Действия' }
            ],
            showModal: false,
            isEditing: false,
            currentTariff: this.getEmptyTariff(),
            modalTitle: ''
        }
    },
    mounted() {
        this.loadTariffs();
    },
    methods: {
        loadTariffs() {
            axios.get('/api/tariffs')
                .then(response => {
                    this.tariffs = response.data;
                })
                .catch(error => {
                    console.error('Error loading tariffs:', error);
                    alert('Ошибка загрузки тарифов');
                });
        },
        showAddModal() {
            this.isEditing = false;
            this.currentTariff = this.getEmptyTariff();
            this.modalTitle = 'Добавить тариф';
            this.showModal = true;
        },
        editTariff(tariff) {

            this.isEditing = true;
            this.currentTariff = { ...tariff };
            this.modalTitle = 'Редактировать тариф';
            this.showModal = true;
        },
        saveTariff() {
            const request = this.isEditing 
                ? axios.put(`/api/tariffs/${this.currentTariff.id}`, this.currentTariff)
                : axios.post('/api/tariffs', this.currentTariff);
            
            if (this.isEditing) {
                Toasts.showWarningToast("Если с данным тарифом связан хотя бы один хост или активный заказ,<br>часть изменений будет проигнорирована (ресурсы и рантайм)!");
            }

            request.then(() => {
                this.showModal = false;
                this.loadTariffs();
            })
            .catch(error => {
                console.error('Error saving tariff:', error);
                Toasts.showErrorToast("Ошибка сохранения тарифа!");
            });
        },
        confirmDelete(tariff) {
            if (confirm(`Удалить тариф "${tariff.name}"?`)) {
                axios.delete(`/api/tariffs/${tariff.id}`)
                    .then(() => {
                        this.loadTariffs();
                    })
                    .catch(error => {
                        console.error('Error deleting tariff:', error);
                        Toasts.showErrorToast("Ошибка удаления тарифа! (Лучше отключите тариф)");
                    });
            }
        },
        resetModal() {
            this.currentTariff = this.getEmptyTariff();
        },
        getEmptyTariff() {
            return {
                name: '',
                cost_rub: 0,
                ram: 0,
                cpu_threads: 0,
                memory_limit: 0,
                enabled: true,
                hours_work_max: 0,
                max_players: 0
            };
        }
    }
});

Vue.use(BootstrapVue);
Vue.use(Toasted);
import * as Toasts from "/public/js/toasts.js";

Vue.use(BootstrapVue);
Vue.use(Toasted);

new Vue({
    el: '#app',
    data: {
        sessions: [],
        centralAddress: '',
        showConfirm: false,
        selectedSession: null,
        toggleField: null,
        confirmText: '',
        field: '',
        timer: null,
        logs: ''
    },
    mounted() {
        this.loadSessions();
        this.timer = setInterval(() => {
            this.sessions.forEach(s => this.checkAlive(s));
        }, 5000);

        // Получим адрес auth-сервера
        axios.get('/api/settings/auth-address')
            .then(res => this.centralAddress = res.data)
            .catch(() => this.centralAddress = '???');
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    },
    methods: {
        loadSessions() {
            axios.get('/api/admin/sessions')
                .then(res => {
                    this.sessions = res.data.map(s => ({ ...s, alive: false }));
                    this.sessions.forEach(s => this.checkAlive(s));
                })
                .catch(err => Toasts.showErrorToast('Ошибка загрузки: ' + err.message));
        },
        checkAlive(session) {
            axios.get(`/api/admin/sessions/check/${encodeURIComponent(session.ip_port)}`)
                .then(() => session.alive = true)
                .catch(() => session.alive = false);
        },
        confirmToggle(session, text, field) {
            this.selectedSession = session;
            this.toggleField = text;
            this.confirmText = (session[text] ? 'отключить' : 'включить') + ' ' + text;
            this.showConfirm = true;
            this.field = field;
        },
        executeToggle() {
            const session = this.selectedSession;
            axios.post(`/api/admin/sessions/${this.field}/${encodeURIComponent(session.ip_port)}`, null)
                .then(() => this.loadSessions())
                .catch(err => Toasts.showErrorToast('Ошибка: ' + err.message));
            this.showConfirm = false;
        },
        loadLogs(ipPort) {
            axios.post(`/api/admin/sessions/logs/${encodeURIComponent(ipPort)}`)
                .then(res => {
                    
                    this.logs = res.data.replace(
                        /.*\/verify_ssl.*\n.*\n/g,
                        ''
                    );
                    this.logs = res.data.replace(
                        /.*\/api\/admin\/sessions\/check\/.*\n.*\n/g,
                        ''
                    );                    // Промотать вниз после следующего DOM-обновления
                    this.$nextTick(() => {
                        const logOutput = this.$refs.logOutput;
                        if (logOutput) {
                            logOutput.scrollTop = logOutput.scrollHeight;
                        }
                    });
                })
                .catch(err => {
                    console.error(err);
                    this.logs = 'Лог не загружен \n' + err.response.data;
                });
        }
    }
});

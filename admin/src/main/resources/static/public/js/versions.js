import * as Toasts from "/public/js/toasts.js";

Vue.use(BootstrapVue);
Vue.use(Toasted);

const app = new Vue({
    el: '#app',
    data() {
        return {
            form: {
                name: '',
                description: '',
                idGame: 1
            },
            tableFields: [
                { key: 'id', label: 'ID' },
                { key: 'name', label: 'Название' },
                { key: 'description', label: 'Описание' },
                { key: 'idGame', label: 'ID Игры' },
                { key: 'actions', label: 'Действия' }
            ],
            gameFields: [
                { key: 'id', label: 'ID' },
                { key: 'name', label: 'Название' },
                { key: 'description', label: 'Описание' },
                { key: 'actions', label: 'Действия' }
            ],
            games: [],
            versions: [],
            gameForm: {
                id: null,
                name: '',
                description: ''
            },
            editingGame: false,
            editingVersion: false,
        };
    },
    watch: {
        'form.idGame': 'fetchVersions'
    },
    mounted() {
        this.fetchGames();
        this.fetchVersions();
    },    
    methods: {
        showAddModal() {
            this.$bvModal.show('uploadModal');
        },
        async fetchGames() {
            const res = await axios.get("/api/games");
            this.games = res.data;
            if (this.games.length > 0 && !this.form.idGame) {
                this.form.idGame = this.games[0].id;
                this.fetchVersions();
            }
        },
        async fetchVersions() {
            if (!this.form.idGame) return;
            const res = await axios.get(`/api/versions/list?idGame=${this.form.idGame}`);
            this.versions = res.data;
        },
        async submitUpload() {
            const formData = new FormData();
            formData.append("name", this.form.name);
            formData.append("description", this.form.description);
            formData.append("idGame", this.form.idGame);
            
            const file = this.$refs.archiveFile.files[0];
            if (file) {
                formData.append("archive", file);
            }
        
            try {
                if (this.editingVersion) {
                    const url = `/api/versions/${this.form.id}`;
                    await axios.put(url, formData, {
                        headers: { 'Content-Type': 'multipart/form-data' }
                    });
                    Toasts.showSuccessToast("Версия обновлена");
                } else {
                    await axios.post("/api/versions/upload", formData, {
                        headers: { 'Content-Type': 'multipart/form-data' }
                    });
                    Toasts.showSuccessToast("Загружено успешно");
                }
                this.fetchVersions();
            } catch (error) {
                console.error(error);
                Toasts.showErrorToast("Ошибка при сохранении версии");
            }
        },
        async deleteVersion(id) {
            await axios.delete(`/api/versions/${id}`);
            Toasts.showSuccessToast("Удалено");
            this.fetchVersions();
        },
        async downloadArchive(id) {
            const res = await axios.get(`/api/versions/${id}/download`, { responseType: 'blob' });
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const a = document.createElement('a');
            a.href = url;
            a.download = "archive.zip"; // или из headers
            a.click();
        },
        showAddGameModal() {
            this.editingVersion = false;
            this.form = {
                name: '',
                description: '',
                idGame: this.form.idGame // сохранить текущий выбор игры
            };
            this.$refs.archiveFile.value = null;
            this.$bvModal.show('uploadModal');
        },
        editVersion(version) {
            this.editingVersion = true;
            this.form = {
                id: version.id,
                name: version.name,
                description: version.description,
                idGame: version.idGame
            };
            this.$refs.archiveFile.value = null;
            this.$bvModal.show('uploadModal');
        },
        editGame(game) {
            this.editingGame = true;
            this.gameForm = { ...game };
            this.$bvModal.show('gameModal');
        },
        async deleteGame(id) {
            try {
                await axios.delete(`/api/games/${id}`);
                Toasts.showSuccessToast("Игра удалена");
                this.fetchGames();
            } catch (error) {
                console.error(error);
                Toasts.showErrorToast("Ошибка при удалении");
            }
        },
        async submitGame() {
            try {
                if (this.editingGame) {
                    await axios.put(`/api/games/${this.gameForm.id}`, this.gameForm);
                    Toasts.showSuccessToast("Игра обновлена");
                } else {
                    await axios.post(`/api/games`, this.gameForm);
                    Toasts.showSuccessToast("Игра добавлена");
                }
                this.fetchGames();
            } catch (error) {
                console.error(error);
                Toasts.showErrorToast("Ошибка при сохранении");
            }
        }
        
    }
});

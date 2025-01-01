import config from "/config.js"; 

// Mounted to a huge Vue object in app.js

export let data = {
    jsonVersions: null,
    version: {
        name: '',
        description: '',
        path: '',
    },
}

export let methods = {
	
    async addVersion() {

        try {
            const response = await axios({
                method: "POST",
                url: `${config.res}/version/add`,
                headers: {
                    'Content-Type': 'application/json',
                },
                data: {
                    path: this.version.path,
                    version: {
                        name: this.version.name,
                        description: this.version.description
                    }
                }
            });
            alert('Успешно');
            //location.reload();
        } catch (error) {
            console.error('Ошибка', error);
            alert('Ошибка');
        }
    },
	
    async getVersions() {

        const response = await axios({
            method: "GET",
            url: `${config.res}/version/all`
        });

        this.jsonVersions = response.data;
    },
	
    deleteVersion(v) {
		
        const confirmation = confirm("Вы уверены, что хотите удалить этот архив версии игры?");
        if (!confirmation) return;

        try {
            const response = axios({
                method: "POST",
                url: `${config.res}/version/delete`,
                headers: {
                    'Content-Type': 'text/plain',
                },
                data: v.name
            });

            alert('Удалено. Обновите таблицу, чтобы убедиться')
            //location.reload();
        } catch (error) {
            console.error('Ошибка', error);
            alert('Ошибка');
        }
    }
}

export function mounted() {
	//НЕ загружаем, список может быть просто огромный
}
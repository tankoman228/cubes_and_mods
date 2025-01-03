import config from "../config.js"; 

// Mounted to a huge Vue object in app.js
// This file is for API testing section of index.html

export let data = {
    requests: []
}

export let methods = {
	
	// For getting: if we need to split JsonObject into fields or it is primitive
    isObject(value) {
        return value && typeof value === 'object' && !Array.isArray(value);
    },
	
	// Подготовка URL с подстановкой параметров
    prepareRequest(request) {
        let url = request.path;
        if (request.pathParams) {
            request.pathParams.forEach(param => {
                const value = request.pathValues[param];
                url = url.replace(`{${param}}`, encodeURIComponent(value || ''));
            });
        }
        return url;
    },
	
	// Вызывается после нажатия на кнопку отправки запроса
    async sendRequest(request, index) {
        try {
            let data;
            if (request.contentType === 'application/json') {
                data = request.jsonBody;
                /*
                for (const key in data) {
                    if (request.allowNull[key] && data[key] === '') {
                        delete data[key];
                    }
                }*/
            } else {
                data = request.plainTextBody;
            }

            const response = await axios({
                method: request.method,
                url: this.prepareRequest(request),
                headers: {
                    'Content-Type': request.contentType || 'application/json'
                },
                data: data
            });

            // Обновляем ответ для конкретного запроса
            this.$set(this.requests[index], 'response', response.data);
        } catch (error) {
            console.error(error);
            this.$set(this.requests[index], 'response', error.response ? error.response.data : error.message);
        }
    },
}

export function mounted() {

    // Превращает полученные объекты об API в объекты с расширенной информацией для редактирования через v-model
    this.requests = initialRequests.map(request => ({
        ...request,
        // Извлекаем параметры пути
        pathParams: (request.path.match(/\{(\w+)\}/g) || [])
            .map(p => p.slice(1, -1)),
			
        // Объект для хранения значений параметров пути
        pathValues: {},
		
        // Динамически создаем тело для JSON
        jsonBody: request.defaultBody ? JSON.parse(request.defaultBody) : {},
		
        allowNull: {},
		
        // Тело для текстового запроса
        plainTextBody: request.defaultBody || ''
    }));
    for (const request of this.requests) {
		// Заменить пути для тестирования в конфигурации
        request.path = request.path.replace('buy', config.buy).replace('res', config.res).replace('usr', config.usr);
    }
}
import config from "../config.js"; 

export let data = {
    requests: []
}

export let methods = {
    isObject(value) {
        return value && typeof value === 'object' && !Array.isArray(value);
    },
    prepareRequest(request) {
        // Подготовка URL с подстановкой параметров
        let url = request.path;
        if (request.pathParams) {
            request.pathParams.forEach(param => {
                const value = request.pathValues[param];
                url = url.replace(`{${param}}`, encodeURIComponent(value || ''));
            });
        }
        return url;
    },
    async sendRequest(request, index) {
        try {
            // Определение данных в зависимости от типа контента
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

    // API section
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
        request.path = request.path.replace('buy', config.buy).replace('res', config.res).replace('usr', config.usr);
    }
}
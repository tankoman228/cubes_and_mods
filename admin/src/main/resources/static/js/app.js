import * as api from './api.js';
import * as machines from './machines.js';
import * as versions from './versions.js';
import * as tariffs from './tariffs.js';



let data = {
    ...api.data,
    ...machines.data,
    ...versions.data,
    ...tariffs.data
}

console.log(data); 

new Vue({
    el: '#app',
    data: data,
    mounted() {
		machines.mounted.call(this); // Привязываем контекст
		versions.mounted.call(this);
		tariffs.mounted.call(this);
		api.mounted.call(this);
    },
    methods: {
        ...api.methods,
        ...machines.methods,
        ...versions.methods,
        ...tariffs.methods,
    }
});

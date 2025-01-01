import * as api from './api.js';
import * as machines from './machines.js';
import * as versions from './versions.js';
import * as tariffs from './tariffs.js';
import * as mineservers from "./mineservers.js";
import * as users from "./users.js";

// Collecting data from other js files for mounting to a huge Vue object

let data = {
    ...api.data,
    ...machines.data,
    ...versions.data,
    ...tariffs.data,
    ...users.data,
    ...mineservers.data,
}

console.log(data); 

new Vue({
    el: '#app',
    data: data,
    mounted() {
		machines.mounted.call(this); // Привязываем контекст
		versions.mounted.call(this);
		tariffs.mounted.call(this);
        users.mounted.call(this);
        mineservers.mounted.call(this);
		api.mounted.call(this);
    },
    methods: {
        ...api.methods,
        ...machines.methods,
        ...versions.methods,
        ...tariffs.methods,
        ...users.methods,
        ...mineservers.methods
    }
});

import config from "/config.js"; 

// Mounted to a huge Vue object in app.js

console.log('machines import');

export let data = {
	
    machines: [],
	
    chartData: null,
    charts_cpu: {},
    charts_ram: {},
    charts_mem: {},
    has_chart: {} ,
    stats: null,
	
    server: {
        name: '',
        address: '',
        cpu_name: '',
        cpu_threads: '',
        ram: '',
        memory: '',
        ram_free: 0,
        cpu_threads_free: 0,
        memory_free: 0
    }
}

export let methods = {
	
    fetchMachines() { 
        axios.get('/api/machines')
            .then(response => {
                this.machines = response.data;

                this.machines.forEach(m => this.fetchStats(m));
            });
    },
	
    fetchStats(machine) {

        axios.get(`/api/machines/${machine.id}/stats`)
            .then(async response => {
                const stats = response.data; // Получаем статистику

                this.$set(this.has_chart, machine.id, true); // Установка значения, чтобы отобразить canvas

                await this.$nextTick(); // От ошибки нулевого тика
                this.renderChart_(stats, machine.id);
            })
            .catch(error => {
                console.error("Ошибка получения статистики:", error);
            });
    },
	
	// Render ALL charts in table for machine by ID. Do not edit name of this func please
    renderChart_(stats, machineId) {

        let timestamps = stats.map(stat => new Date(stat.timestamp).toLocaleTimeString());
        let memoryFree = stats.map(stat => stat.memoryFree);
        let ramFree = stats.map(stat => stat.ramFree);
        let freeCpuThreads = stats.map(stat => stat.cpuThreadsFree);

		this.renderTheChart(`memoryChart-${machineId}`, this.charts_mem, timestamps, machineId,
		[{
			label: 'Свободная память ПЗУ (КБ)',
			data: memoryFree,
			borderColor: 'rgba(255, 0, 0, 1)',
			fill: false
		}]);	

		this.renderTheChart(`ramChart-${machineId}`, this.charts_ram, timestamps, machineId,
		[{
			label: 'Свободная ОЗУ (ГБ)',
			data: ramFree,
			borderColor: 'rgba(0, 255, 0, 1)',
			fill: false
		}]);	

        this.renderTheChart(`cpuChart-${machineId}`, this.charts_cpu, timestamps, machineId,
        [{
            label: 'Свободные ядра CPU (шт)',
            data: freeCpuThreads,
            borderColor: 'rgba(0, 0, 255, 1)',
            fill: false
        }]);
    },
	
	// Renderer of chart
    renderTheChart(canvasElementId, charts, timestamps, id, datasets) {

        if (charts[id]) {
            try { charts[id].destroy();}
            catch (error) { console.error(error);}
        }

        let canvasElement = document.getElementById(canvasElementId);
        if (canvasElement) {
            let ctx = canvasElement.getContext('2d');
            charts[id] = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: timestamps,
                    datasets: datasets
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        } else {
            console.error('Canvas element not found!');
        }
    },

    // Add new machine
    async addServer() {

        try {
            this.server.ram_free = this.server.ram;
            this.server.cpu_threads_free = this.server.cpu_threads;
            this.server.memory_free = this.server.memory;

            const response = await axios({
                method: "POST",
                url: `${config.res}/machines`,
                headers: {
                    'Content-Type': 'application/json' || 'application/json',
                },
                data: this.server
            });

            console.log('Сервер добавлен', response.data);
            location.reload(); // Аналог F5

        } catch (error) {
            console.error('Ошибка при добавлении сервера:', error);
            alert('Ошибка при добавлении сервера');
        }
    },
	
    async deleteMachine(machine) {

        const confirmation = confirm("Вы уверены, что хотите удалить этот сервер?");
        if (!confirmation) return;

        try {
            const response = await axios({
                method: "DELETE",
                url: `${config.res}/machines/` + machine.id,
                headers: {
                    'Content-Type': 'application/json' || 'application/json',
                },
                data: this.server
            });

            console.log('Сервер удалён', response.data);
            location.reload(); // F5
			
        } catch (error) {
            console.error('Ошибка при удалении сервера', error);
            alert('Ошибка при удалении сервера. Возможно, из-за связанных данных в базе и удалять его нельзя');
        }
    },
}

export function mounted() {
    this.fetchMachines(); // machines table
}
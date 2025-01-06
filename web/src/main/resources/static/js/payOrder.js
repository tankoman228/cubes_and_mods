document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserId,
			email: Email,
			tariffId: TariffId,
			machineId: MachineId,
			key: Key,
			tariff:{
				id: -1,
				name: "",
				cost_rub: -1,
				ram: -1,
				cpu_threads: -1,
				memory_limit: -1,
				enabled: false,
				hours_work_max: -1,
				max_players: -1,
			},
	    },
	    created() {
			this.getTariff();
	    },
	    methods: {
			getTariff(){
				axios.get('/tariffs/getById?TariffId=' + this.tariffId)
			        .then(response => {
			            this.tariff = response.data;
						this.reserve();
			        })
			        .catch(error => {
						alert(error);
			        });
			},
			confirm(){
				alert(this.key);
				axios.post('/pay/confirm', { key: this.key })
					.then(response => {
						console.log("Место зарезервированно");
						window.location.href = "/myServers"
					})
					.catch(error => {
						alert(error);
					});
			},
			decline(){
				alert(this.key);
				axios.post('/pay/decline', { key: this.key })
					.then(response => {
						console.log("Место зарезервированно");
					    this.free();
					})
					.catch(error => {
						alert(error);
					});
			},
			reserve(){
				axios.post('/machines/reserve?id=' + this.machineId, this.tariff)
					.then(response => {
						console.log("Место зарезервированно");
						
						axios.post('/machines/recount?id=' + this.machineId)
							.then(response => {
							    console.log("Расчет свободного места");
							})
							.catch(error => {
								alert(error);
							});
					})
					.catch(error => {
						alert(error);
					});
			},
			free(){
				axios.post('/machines/free?id=' + this.machineId, this.tariff)
					.then(response => {
						console.log("Место освобождено");
						
						axios.post('/machines/recount?id=' + this.machineId)
							.then(response => {
							    console.log("Расчет свободного места");
								window.location.href = "/";
							})
							.catch(error => {
								alert(error);
							});
					})
					.catch(error => {
						alert(error);
					});
			},
			cancel(){
				this.decline();
			},
	    }
	});
});
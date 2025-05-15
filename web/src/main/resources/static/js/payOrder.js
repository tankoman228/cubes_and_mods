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
				axios.get('/getTariffs/getById?TariffId=' + this.tariffId)
			        .then(response => {
			            this.tariff = response.data;
			        })
			        .catch(error => {
						console.log("Ошибка при получении тарифа: " + error);
						alert(error);
			        });
			},
			confirm(){				
				axios.post('/pay/confirm', { key: this.key })
					.then(response => {
						console.log("Подтверждение");
						window.location.href = "/myServers"
					})
					.catch(error => {
						alert(error);
					});
				//alert(this.key);
			},
			decline(){
				//alert(this.key);
				axios.post('/pay/decline', { key: this.key })
					.then(response => {
						console.log("Отмена");
					    this.free();
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
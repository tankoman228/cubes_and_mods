document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			tariffId: TariffId,
			email: Email,
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
			order:{
				/*mineserver:{
					//id: -1,
					//memory_used: 0,
					clientOrder: UserId,
					tariffOrder: TariffId,
					serverOrder: -1,
					//seconds_working: 0,
					//name: "",
					//description: "",
				},*/
				//newTariff: null
				clientOrder: 
				{
					id: UserId
				},
				tariffOrder: 
				{
					id: TariffId,
				},
				serverOrder:{
					id: -1
				},
				/*hostOrder:{
					name: "",
					description: "",
					id_client: -1,
					id_tariff: -1,
					id_server: -1,
				}*/
			},
			machines: [],
			hostOrder:{
				name: "",
				description: "",
				id_client: -1,
				id_tariff: -1,
				id_server: -1,
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
						this.order.tariffOrder.id = this.tariff.id;
						this.getMachines();
						
			        })
			        .catch(error => {
						alert(error);
			        });
			},
			getMachines(){
				axios.get('/getTariffs/AvalibleServers?TariffId=' + this.tariff.id)
					.then(response => {
			            this.machines = response.data;
			        })
			        .catch(error => {
						//const statusCode = error.response.status;
						alert("Ошибка: " + error);
			        });
			},
			confirm(){
				if(this.order.serverOrder.id == -1){
					alert("Выберите сервер, на котором вы хотите зарезервировать место");
					return;
				}
				if(this.hostOrder.name == ""){
					alert("Введите название сервера");
					return;
				}
				console.log(this.order.clientOrder.id);
				console.log(this.order);
				this.hostOrder.id_client = this.order.clientOrder.id;
				this.hostOrder.id_server = this.order.serverOrder.id;
				this.hostOrder.id_tariff = this.order.tariffOrder.id;
				axios.post('/pay/request', this.order)
					.then(response => {
						key = response.data;
						window.location.href = "/payOrderEdit?tariffId=" + this.tariff.id + "&machineId=" + this.order.serverOrder.id + "&key=" + key
						+ "&name=" + this.hostOrder.name + "&description=" + this.hostOrder.description;
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			choose(machine){
				this.order.serverOrder.id = machine.id;
			},
			cancel(){
				window.location.href = "/";
			},
	    }
	});
});
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
				mineserver:{
					id: 5, //Временное решение
					memory_used: 0,
					id_user: UserId,
					id_tariff: TariffId,
					id_machine: -1,
					seconds_working: 0,
					ip: "37.194.70.114:6666", //Временное решение
					name: "",
					description: "",
				},
				newTariff: null
			},
			machines: [],
	    },
	    created() {
			this.getTariff();
	    },
	    methods: {
			getTariff(){
				axios.get('/tariffs/getById?TariffId=' + this.tariffId)
			        .then(response => {
			            this.tariff = response.data;
						this.order.mineserver.id_tariff = this.tariff.id;
						this.getMachines();
						
			        })
			        .catch(error => {
						alert(error);
			        });
			},
			getMachines(){
				axios.post('/machines/whichCan', this.tariff)
					.then(response => {
			            this.machines = response.data;
			        })
			        .catch(error => {
						//const statusCode = error.response.status;
						alert("Ошибка: " + error);
			        });
			},
			confirm(){
				if(this.order.mineserver.id_machine == -1){
					alert("Выберите сервер, на котором вы хотите зарезервировать место");
					return;
				}
				if(this.order.mineserver.name == ""){
					alert("Введите название сервера");
					return;
				}
				//alert(this.order);
				axios.post('/pay/request', this.order)
					.then(response => {
						key = response.data;
						window.location.href = "/payOrder?tariffId=" + this.tariff.id + "&machineId=" + this.order.mineserver.id_machine + "&key=" + key;
					})
					.catch(error => {
						alert(error);
					});
			},
			choose(machine){
				this.order.mineserver.id_machine = machine.id;
			},
			cancel(){
				window.location.href = "/";
			},
	    }
	});
});
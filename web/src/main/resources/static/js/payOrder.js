document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserId,
			email: Email,
			tariffId: TariffId,
			machineId: MachineId,
			key: Key,
			hostName: Name,
			hostDescription: Description,
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
						if(this.hostName != null && this.hostDescription != null){
							hostOrder = {
								name: this.hostName,
								description: this.hostDescription,
								id_client: -1,
								id_tariff: -1,
								id_server: -1,
							};
							//TODO: придумать иной метод
							axios.get('/mcserver/my?id='+this.userId)
								.then(response => {
									var servers = response.data;
									maxIdServer = servers.reduce((max, server) => {
										return server.id > max.id ? server : max;
									}, servers[0]);

									axios.put("/mcserver/my/edit?id="+maxIdServer.id, hostOrder)
									.then(response => {
										window.location.href = "/myServers";
									})
									.catch(error => {
										alert(error);
										console.log(error);
									});
								})
								.catch(error => {
									alert(error);
									console.log(error);
								});
						}
						else{
							window.location.href = "/myServers";
						}
					})
					.catch(error => {
						alert(error);
					});
				//alert(this.key);

			},
			decline(){
				console.log(this.key);
				axios.post('/pay/decline', { key: this.key })
					.then(response => {
						console.log("Отмена");
					    window.location.href = "/"
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			cancel(){
				this.decline();
			},
	    }
	});
});
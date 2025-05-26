document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			email: Email,
			serverId: SrvID,
			userId: UserId,
			tarifId: TariffId,
			hostOrder:{
				name: "",
				description: "",
				id_client: -1,
				id_tariff: -1,
				id_server: -1,
			},
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
			console.log(this.tarifId);
			this.getTariff();
			this.getServer();
	    },
	    methods: {
			confirm(){
				axios.put("/mcserver/my/edit?id="+this.serverId, this.hostOrder)
					.then(response => {
						alert("Сервер изменен");
						window.location.href = "/serverSettings?ServerId=" + this.serverId;
					})
					.catch(error => {
						alert(error);
						console.log(error);
					})
			},
			cancel(){
				window.location.href = "/serverSettings?ServerId=" + this.serverId;
			},
			getTariff(){
				axios.get('/getTariffs/getById?TariffId=' + this.tarifId)
			        .then(response => {
			            this.tariff = response.data;
			        })
			        .catch(error => {
						alert(error);
			        });
			},
			getServer(){
				axios.get('/mcserver/my/?id=' + this.serverId)
			        .then(response => {
			            server = response.data;
						this.hostOrder.name = server.name;
						this.hostOrder.description = server.description;
			        })
			        .catch(error => {
						alert(error);
			        });
			}
	    }
	});
});
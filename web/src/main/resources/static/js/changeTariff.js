document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			UserId: UserID,
			SrvId: SrvID,
			email: Email,
			tarifs: [],
			tariff: null,
			newTariffId: -1,
			order:{
				mineserver: null,
				newTariff: null,
			},
	    },
	    created() {
			this.getTarifs();
			this.getServer();
	    },
	    methods: {
			getTarifs(){
				axios.get('/getTariffs/')
			        .then(response => {
			            this.tarifs = response.data;
			        })
			        .catch(error => {
						alert(error);
			        });
			},
			getServer(){
				axios.get('/mcserver/my?id='+this.UserId)
				    .then(response => {
				        servers = response.data;
						server = this.findServerByID(servers, this.SrvId);
						this.order.mineserver = server;
						axios.get('/tariffs/getById?TariffId=' + server.id_tariff)
							.then(response => {
								this.tariff =  response.data;
							})
							.catch(error => {
								alert(error);
							});

				    })
				    .catch(error => {
						alert(error);
						console.log(error);
				    });
			},
			selectTarif(tarif) {
				this.order.newTariff = tarif;
				this.newTariffId = tarif.id;
			},
			findServerByID(data, search){
				data = data.filter(srv => srv.id == search);
				return data[0];
			},
			cancel(){
				window.location.href = "/serverSettings?ServerId="+this.SrvId;
			},
			confirm(){
				if(this.order.newTariff == null){
					alert("Для начала выберите тариф!");
					return;
				}
				if(this.order.newTariff.id == this.tariff.id){
					alert("Нельзя выбрать тот-же тариф!");
					return;
				}
					
				axios.post('/machines/canHandleWithNewTariff?id_mineserver=' + this.order.mineserver.id + '&id_tariff=' + this.order.newTariff.id)
					.then(response =>{
						isReady = response.data;
						if(isReady == false){
							alert('Сервер не может выделить ресурсы под данный тариф, пожалуйста попробуйте другой тариф.')
						}

						//this.order.mineserver.id_tariff = this.tariff.id;
						axios.post('/pay/request', this.order)
							.then(response => {
								key = response.data;
								window.location.href = "/payOrder?tariffId=" + this.order.newTariff.id + "&machineId=" + this.order.mineserver.id_machine + "&key=" + key;
							})
							.catch(error => {
								alert(error);
							});
					})
					.catch(error => {
						alert(error);
					});
			}
	    }
	});
});
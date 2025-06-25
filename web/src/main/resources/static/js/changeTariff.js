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
			/*order:{
				mineserver: null,
				newTariff: null,
			},*/
			order:{
				clientOrder: 
				{
					id: UserID
				},
				tariffOrder: 
				{
					id: -1,
				},
				serverOrder:{
					id: -1
				},
				hostOrder:{
					id: SrvID
				}
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
				axios.get('/mcserver/my/?id='+this.SrvId)
				    .then(response => {
				        host = response.data;
						console.log(host);
						this.order.serverOrder.id = host.serverHost.id;
						axios.get('/getTariffs/getById?TariffId=' + host.tariffHost.id)
							.then(response => {
								this.tariff =  response.data;
							})
							.catch(error => {
								alert(error);
								console.error(error);
							});

				    })
				    .catch(error => {
						alert(error);
						console.log(error);
				    });
			},
			selectTarif(tarif) {
				this.order.tariffOrder.id = tarif.id;
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
				if(this.order.tariffOrder.id == -1){
					alert("Для начала выберите тариф!");
					return;
				}
				if(this.order.tariffOrder.id == this.tariff.id){
					alert("Нельзя выбрать тот-же тариф!");
					return;
				}
					
				axios.post('/pay/request', this.order)
					.then(response => {
						key = response.data;
						window.location.href = "/payOrder?tariffId=" + this.order.tariffOrder.id + "&machineId=" + this.order.serverOrder.id + "&key=" + key;
					})
					.catch(error => {
						alert(error);
					});
			}
	    }
	});
});
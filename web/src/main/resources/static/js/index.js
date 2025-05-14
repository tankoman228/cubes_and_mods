document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			email: Email,
			tarifs: [],
	    },
	    created() {
			this.getTariffs();
	    },
	    methods: {
			getTariffs(){
				axios.get('/getTariffs/')
	                .then(response => {
	                    this.tarifs = response.data;
	                })
	                .catch(error => {
						alert(error);
	                });
			},
			buyTarif(tarif) {
				axios.get('/getTariffs/AvalibleServers?TariffId=' + tarif.id)
					.then(response => {
				        machines = response.data;
						if(this.email == null){
							alert("Пожалуйста, войдите в систему для совершения покупки.");
							return;
						}
						if(machines.length == 0){
							alert("Пожалуйста, выберите другой ториф, в данный момент на серверах не хватает ресурсов");
							return;
						}
						window.location.href = "/buyTariff?tariffId="+tarif.id;
				    })
				    .catch(error => {
						//const statusCode = error.response.status;
						alert("Ошибка: " + error);
				    });
			},
	    }
	});
});
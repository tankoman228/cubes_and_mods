document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			email: Email,
			tarifs: [],
	    },
	    created() {
			this.getTarifs();
			//alert(this.isAuthenticated);
	    },
	    methods: {
			getTarifs(){
				axios.get('/tariffs/')
	                .then(response => {
	                    this.tarifs = response.data;
	                })
	                .catch(error => {
						alert(error);
	                });
			},
			buyTarif(tarif) {
			    if(this.email == null){
					alert("Пожалуйста, войдите в систему для совершения покупки.");
				}
				else{
					window.location.href = "http://localhost:8080/buyTariff?tariffId="+tarif.id;
				}
			},
	    }
	});
});
document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			email: Email,
			tarifs: [],
	    },
	    created() {
			this.getTarifs();
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
					window.location.href = "/buyTariff?tariffId="+tarif.id;
				}
			},
	    }
	});
});
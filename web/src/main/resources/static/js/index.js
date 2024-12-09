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
			
			goTo(page = "") {
			    axios.get("http://localhost:8080"+page)
			        .then(response => {
			            console.log("Ответ:", response.data);
						window.location.href = "http://localhost:8080"+page;
			        })
			        .catch(error => {
						console.log(page);
						console.log(encodeURIComponent(page));
			            console.error("Ошибка при загрузке страницы:", error);
			            alert("Это отвалn\n" + error);
			        });
			},
			getTarifs(){
				axios.get('http://127.0.0.1:8082/tariffs')
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
					alert(tarif.name);
				}
			},
	    }
	});
});
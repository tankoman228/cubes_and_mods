document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			UserId: UserID,
			email: Email,
			servers: [],
	    },
	    created() {
			this.getMyMCServers();
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
			getMyMCServers(){
				axios.get('http://localhost:8080/mcserver/my?id='+this.UserId)
	                .then(response => {
	                    this.servers = response.data;
	                })
	                .catch(error => {
						alert(error);
	                });
			},
			openServer(server) {
			    if(server != null){
					window.location.href = 'http://localhost:8080/console?ServerId='+server.id;
				}
				else{
					alert("Ошибка: переданы невалидные данные сервера");
				}
			},
	    }
	});
});
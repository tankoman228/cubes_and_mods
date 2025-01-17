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
			    window.location.href = page;
			},
			getMyMCServers(){
				
				axios.get('/mcserver/my?id='+this.UserId)
	                .then(response => {
	                    this.servers = response.data;
	                })
	                .catch(error => {
						alert(error);
	                });
			},
			openServer(server) {
			    if(server != null){
					window.location.href = '/console?ServerId='+server.id;
				}
				else{
					alert("Ошибка: переданы невалидные данные сервера");
				}
			},
			openSettigs(server){
				if(server != null){
					window.location.href = '/serverSettings?ServerId='+server.id;
				}
				else{
					alert("Ошибка: переданы невалидные данные сервера");
				}
			},
	    }
	});
});
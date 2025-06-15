document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			UserId: UserID,
			email: Email,
			servers: [],
			installedServers: {},
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

						this.servers.forEach(server => {
						axios.post('/root/isInstalled', server.id, {
								headers: { 'Content-Type': 'application/json' }
							})
							.then(response => {
								this.$set(this.installedServers, server.id, response.data);
							})
							.catch(error => {
								console.error('Ошибка при проверке установки сервера:', error);
								this.$set(this.installedServers, server.id, false);
							});
						});
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
			clearServer(server){
				result = confirm("Очистить данные сервера?");
				if(result == false) return;
				
				axios.post('/root/is_alive', server.id,
					{
					headers: {
					    'Content-Type': 'application/json'
					}
					})
					.then(response => {
						if(response.data == true){
							alert('Сервер сейчас активен, удаление невозможно!');
							return;
						}
						axios.post('/root/delete', server.id,
							{
							headers: {
							    'Content-Type': 'application/json'
							}
							})
							.then(response => {
								alert("Успешно!");
								this.getMyMCServers();
							})
							.catch(error => {
								alert(error);
								console.error(error);
							});
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
	    }
	});
});
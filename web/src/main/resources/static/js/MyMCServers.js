document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			UserId: UserID,
			email: Email,
			servers: [],
			status: "",
			canClose: false,
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
			getStatus(serverID) {
				//this.isModalVisible = true;
				modal.showModal();
				this.canClose = false;

				const checkStatus = () => {
					axios.post('/backups/status?id_task=' + this.taskId)
					.then(response => {
						this.status = response.data;
						if (this.status === 'Готово!' || this.status === 'Завершено на этапе: ' || this.status === 'success' || response.data.error) {
						this.canClose = true;
						this.deleteServer(serverID);
						} else {
						setTimeout(checkStatus, 1000);
						}
					})
					.catch(error => {
						this.status = 'Ошибка: ' + error.message;
						this.canClose = true; 
					});
				};

				checkStatus();
			},
			closeModal() {
				//this.isModalVisible = false;
				modal.close();
				this.status = ''; 
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
						createBackup = confirm("Создать резервную копию?");

						if(createBackup){
							backupName = prompt('Введите название бэкапа');
							console.log(backupName);

							if(backupName === null) {
								alert("Нельзя оставлять имя резервной копии пустым!");
								return;
							}
							
							axios.post('/backups/create?id_server='+server.id+'&name='+backupName)
								.then(response => {
									this.taskId = response.data;
									this.getStatus(server.id);
								})
								.catch(error => {
									if(error.response.status == 400){
										alert("Имя бэкапа не может быть пустым!");
										return;
									}
									alert("При создании возникла ошибка: " + error);
								});

						}
						else{
							this.deleteServer(server.id);
						}
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			deleteServer(serverID){
				axios.post('/root/delete', serverID,
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
			}
	    }
	});
});
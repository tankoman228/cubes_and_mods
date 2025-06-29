document.addEventListener('DOMContentLoaded', function() {	
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			backups: [],
			status: "",
			taskId: -1,
			//isModalVisible: false,
			canClose: false,
	    },
	    created() {
			this.isAlive();
			this.getBackups();
	    },
	    methods: {
			isAlive(){
				axios.post('/root/is_alive',this.SrvId,
					{
					headers: {
					    'Content-Type': 'application/json'
					}
					})
					.then(response => {
						if(response.data == true){
							alert('Сервер сейчас активен, вы будете перенаправлены к консоли сервера, подключитесь к нему и остановите, а затем повторите попытку.')
							window.location.href = '/console?ServerId=' + this.SrvId;
						}
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			getBackups(){
				axios.post('/backups?id='+this.SrvId)
				    .then(response => {
				        this.backups = response.data;
				    })
				    .catch(error => {
						alert(error);
						console.error(error);
				    });
			},
			deleteBackup(backup){
				result = confirm("Удалить"  + backup.name + " за " + this.getDate(backup.created_at) + "?");
				if(result===true){
					this.getStatus();
					axios.post('/backups/delete?id_server='+this.SrvId, backup)
					    .then(response => {
							this.taskId = response.data;
							this.getStatus();
					    })
					    .catch(error => {
							alert("При удалении возникла ошибка: " + error);
					    });
				}
			},
			rollbackBackup(backup){
				result = confirm("Откатить сервер к " + backup.name + " за " + this.getDate(backup.created_at) + "?");
				if(result===true){
					axios.post('/backups/rollBack?id_server='+this.SrvId+'&id_backup='+backup.id)
					    .then(response => {
							this.taskId = response.data;
							this.getStatus();
					    })
					    .catch(error => {
							alert("При удалении возникла ошибка: " + error);
					    });
				}
			},
			createBackup(){
				//window.location.href = "/createBackup?ServerId="+this.SrvId;
				backupName = prompt('Введите название бэкапа');
				console.log(backupName);

				if(backupName === null) {
					alert("Нельзя оставлять имя резервной копии пустым!");
					return;
				}
				
				axios.post('/backups/create?id_server='+this.SrvId+'&name='+backupName)
				    .then(response => {
						this.taskId = response.data;
						this.getStatus();
				    })
				    .catch(error => {
						if(error.response.status == 400){
							alert("Имя бэкапа не может быть пустым!");
							return;
						}
						alert("При создании возникла ошибка: " + error);
				    });
			},
			getStatus() {
				//this.isModalVisible = true;
				modal.showModal();
				this.canClose = false;

				const checkStatus = () => {
					axios.post('/backups/status?id_task=' + this.taskId)
					.then(response => {
						this.status = response.data;
						if (this.status === 'Готово!' || this.status === 'Завершено на этапе: ' || this.status === 'success' || response.data.error) {
						this.canClose = true;
						this.getBackups();
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
			getDate(dateTime) {
				const date = new Date(dateTime); // Преобразуем строку в объект Date
				const options = { year: 'numeric', month: 'long', day: 'numeric' };
				return date.toLocaleDateString('ru-RU', options); // "12 января 2025 г."
			},
			getTime(dateTime) {
				const date = new Date(dateTime); // Преобразуем строку в объект Date
				const timeOptions = { hour: '2-digit', minute: '2-digit', second: '2-digit' };
				return date.toLocaleTimeString('ru-RU', timeOptions); // "12:54:53"
			},
	    }
	});
});
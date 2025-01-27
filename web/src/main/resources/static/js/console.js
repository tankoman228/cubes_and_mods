document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserID,
			serverId: SRVId,
			serverTariffId: SRVTariff,
			serverSerconds: SRVSeconds,
			serverMemory: SRVMem,
			running: false,
	        command: "",
	        commandHistory: [],
			messages: [],
	        historyIndex: -1,
			socket: null,
			tariff: null,
	    },
		mounted() {
		  this.startFetchingServer();
		},
		beforeDestroy() {
		  this.stopFetchingServer();
		},
		created() {
			this.getTariff();
			this.isAlive();
		},
		computed: {
			totalAvailableTime() {
				if(0 > this.serverSerconds/3600){
					return (this.tariff.hours_work_max + Math.abs(this.serverSerconds/3600)).toFixed(2);
				}
				else{
					return (this.tariff.hours_work_max - Math.abs(this.serverSerconds/3600)).toFixed(2);
				}
			},
			totalTime() {
				if(0 > this.serverSerconds/3600){
					return Math.ceil(this.totalAvailableTime/this.tariff.hours_work_max) * this.tariff.hours_work_max;
				}
				else{
					return this.tariff.hours_work_max;
				}
			},
		},
	    methods: {
			isAlive(){
				axios.post('/root/is_alive',this.serverId,
					{
				    headers: {
				        'Content-Type': 'application/json'
				    }
					})
					.then(response => {
						if(this.running != response.data){
							this.running = response.data;
							console.log("Статус сервера: "+response.data);
							
							if(this.running == true){
								this.startServer();
							}
						}
					})
					.catch(error => {
						alert(error);
					});
			},
			getTariff(){
				axios.get('/tariffs/getById?TariffId=' + this.serverTariffId)
					.then(response => {
						this.tariff = response.data;
					})
					.catch(error => {
						alert(error);
					});
			},
			getServerData(){
				axios.get('/mcserver/my?id=' + this.userId)
					.then(response => {
						servers = response.data;
						server = this.findServerByID(servers, this.serverId);
						console.log(this.userId);
						console.log(server.name);
						this.serverSerconds = server.seconds_working;
						this.serverMemory = server.memory_used;
						console.log("Получен сервер");
					})
					.catch(error => {
						alert(error);
					});
			},
			startFetchingServer() {
				this.getServerData();
			  
				this.intervalId = setInterval(() => {
					this.isAlive();
			 		this.getServerData();
				}, 1000);
			},
			stopFetchingServer() {
			  if (this.intervalId) {
			    clearInterval(this.intervalId);
			    this.intervalId = null;
			  }
			},
			findServerByID(data, search){
				data = data.filter(srv => srv.id == search);
				return data[0];
			},
	        addToConsole(text) {
				if (this.messages.length >= 300) {
				    this.messages.shift();
				}
	            this.messages.push(text);
	            this.$nextTick(() => {
	                const consoleDiv = document.getElementById('console');
	                consoleDiv.scrollTop = consoleDiv.scrollHeight;
	            });
	        },
	        executeCommand() {
				this.command = this.command.trim();
	            if (this.command !== '') {
					console.log("Command = " + this.command);
					
					if(this.command === 'stop'){
						this.running = false;
					}
					
					if (this.commandHistory[this.commandHistory.length - 1] != this.command){
						this.commandHistory.push(this.command);
						this.historyIndex = this.commandHistory.length;
					}
					
					if (this.command && this.socket) {
					  this.socket.send(this.command);
					  this.command = '';
					}
					
					this.$nextTick(() => {
					    const consoleDiv = document.getElementById('console');
					    consoleDiv.scrollTop = consoleDiv.scrollHeight;
					});
	            }
	        },
			executeCommandSilent(command) {
			    if (command.trim() !== '') {
					if (command && this.socket) {
					  this.socket.send(command);
					}
					
					this.$nextTick(() => {
					    const consoleDiv = document.getElementById('console');
					    consoleDiv.scrollTop = consoleDiv.scrollHeight;
					});
			    }
			},
	        startServer() {
				axios.post('/root/launch', this.serverId, {
				    headers: {
				        'Content-Type': 'application/json'
				    }
				})
				.then(response => {
				    console.log('Response Status:', response.status);
					this.running = true;
					this.connect();
					if(this.socket != null){
						console.log('Connected!');
						//alert("Сервер запущен");
					} else{
						console.log('Not connected!');
					}	
				})
				.catch(error => {
					this.running = false;
				    if (error.response) {
				        console.error('Error Status:', error.response.status);
				        console.error('Error Data:', error.response.data);
				        alert("Произошла ошибка: " + error.response.status + " " + error.response.data);
				    } else if (error.request) {
				        alert("Ответ не был получен");
				        console.error('No Response Received:', error.request);
				    } else {
				        alert("Ошибка при формировании запроса");
				        console.error('Error Message:', error.message);
				    }
				});
	        },
			checkServer(id){
				axios.post('/root/is_alive', id, {
				    headers: {
				        'Content-Type': 'application/json'
				    }
				})
				.then(response => {
				    console.log('Response Status:', response.status);
				    this.running = response.data;
				})
				.catch(error => {
				    if (error.response) {
				        console.error('Error Status:', error.response.status);
				        console.error('Error Data:', error.response.data);
				        alert(error.response.status + " " + error.response.data);
				    } else if (error.request) {
				        alert("Ответ не был получен");
				        console.error('No Response Received:', error.request);
				    } else {
				        alert("Ошибка при формировании запроса");
				        console.error('Error Message:', error.message);
				    }
				});
			},
	        saveServer() {
				this.executeCommandSilent("save-all");
	            //alert("Сервер сохранен");
	        },
			reloadServer() {
				this.executeCommandSilent("reload");
			    //alert("Сервер остановлен");
			},
	        stopServer() {
				result = confirm("Остановить сервер?");
				if(result == false) return;
				this.executeCommandSilent("stop");
				this.running = false;
	            //alert("Сервер остановлен");
	        },
			killServer(){
				result = confirm("ВНИМАНИЕ!!! При экстренном закрытие сервера возможна потеря данных! Используйте только при зависании сервера!");
				if(result == false) return;
				this.running = false;
				axios.post('/root/kill', this.serverId, {
					    headers: {
					        'Content-Type': 'application/json'
					    }
					})
					.then(response => {
						console.log("Сервер остановлен");
					})
					.catch(error => {
						console.log(error);
					});
			},
	        handleKeyDown(event) {
	            if (event.key === 'ArrowUp') {
	                event.preventDefault();
	                if (this.historyIndex > 0) {
	                    this.historyIndex--;
	                    this.command = this.commandHistory[this.historyIndex];
	                }
	            } else if (event.key === 'ArrowDown') {
	                event.preventDefault();
	                if (this.historyIndex < this.commandHistory.length - 1) {
	                    this.historyIndex++;
	                    this.command = this.commandHistory[this.historyIndex];
	                } else {
	                    this.historyIndex = this.commandHistory.length;
	                    this.command = '';
	                }
	            } else if (event.key === 'Enter') {
	                this.executeCommand();
	            }
	        },
			connect() {
			  if(this.socket == null || this.socket.readyState === WebSocket.CLOSED){
				//this.socket = new WebSocket('ws://localhost:8083/console');
				this.socket = new WebSocket('/consoleSocket');
			  }

			  this.socket.onopen = () => {
			    console.log('Connecting to server ID = '+this.serverId);
				setTimeout(() => {
				    this.socket.send(this.serverId);
				}, 500);
			  };
	
			  this.socket.onmessage = (event) => {
				console.log('Getting messages');
				if (this.messages.length >= 250) {
				    this.messages.shift();
				}
			    this.messages.push(event.data);
				this.$nextTick(() => {
				    const consoleDiv = document.getElementById('console');
				    consoleDiv.scrollTop = consoleDiv.scrollHeight;
				});
			  };
	
			  this.socket.onclose = () => {
				this.running = false;
			    console.log('Disconnected from the server');
				alert("Соединение с сервером разорвано");
			  };
	
			  this.socket.onerror = (event) => {
			      this.running = false;
			      console.error('Connection error:', event);
			      alert('Ошибка соединения: ' + event.message || event.reason || 'Неизвестная ошибка');
			  };
			},
	    }
	});
});

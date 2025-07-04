document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			userId: UserID,
			serverId: SRVId,
			serverTariffId: SRVTariff,
			serverSerconds: SRVSeconds,
			serverMemory: SRVMem,
			token: Email,
			running: false,
	        command: "",
	        commandHistory: [],
			messages: [],
	        historyIndex: -1,
			socket: null,
			tariff: null,
			netData: null,
			isChecking: false,
	    },
		created() {
			this.getTariff();
			this.isAlive();
			this.getServerData();
			this.getNetData();
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
				const IsAlive = () => {
				if (this.isChecking) return;
				this.isChecking = true;

				axios.post('/root/is_alive',this.serverId,
					{
				    headers: {
				        'Content-Type': 'application/json'
				    },
					timeout: 5000
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
						console.error(error);
					})
					.finally(() => {
						this.isChecking = false;
						setTimeout(IsAlive, 3000);
					});
				};

				IsAlive();
			},
			getTariff(){
				console.log(this.serverTariffId);
				axios.get('/getTariffs/getById?TariffId=' + this.serverTariffId)
					.then(response => {
						this.tariff = response.data;
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			getNetData(){
				console.log("this.serverId = " + this.serverId);
				axios.post('/root/netConfig', this.serverId, {
						headers: {
							'Content-Type': 'application/json'
						}
					})
					.then(response => {
						this.netData = response.data;
						console.log(this.netData);
					})
					.catch(error => {
						alert(error);
						console.error(error);
					});
			},
			getServerData(){
				const GetServerData = () => {

				axios.get('/mcserver/my?id=' + this.userId, {timeout: 5000})
					.then(response => {
						servers = response.data;
						server = this.findServerByID(servers, this.serverId);
						//console.log(this.userId);
						//console.log(server.name);
						this.serverSerconds = server.seconds_working;
						this.serverMemory = server.memory_used;
						//console.log("Получен сервер");
					})
					.catch(error => {
						alert(error);
						console.err(error);
					})
					.finally(() => {
						setTimeout(GetServerData, 10000);
					});
				};

				GetServerData();
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
					}
					this.historyIndex = this.commandHistory.length;
					
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
					//this.running = true;
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
				    //console.log('Response Status:', response.status);
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
				//this.running = false;
	            //alert("Сервер остановлен");
	        },
			killServer(){
				result = confirm("ВНИМАНИЕ!!! При экстренном закрытие сервера возможна потеря данных! Используйте только при зависании сервера!");
				if(result == false) return;
				this.running = false;
				for(i = 0; i < 10; i++){
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
				}
			},
	        handleKeyDown(event) {
	            if (event.key === 'ArrowUp') {
	                event.preventDefault();
	                if (this.historyIndex > 0) {
	                    this.historyIndex--;
	                    this.command = this.commandHistory[this.historyIndex];
						console.log(this.commandHistory);
	                }
	            } else if (event.key === 'ArrowDown') {
	                event.preventDefault();
	                if (this.historyIndex < this.commandHistory.length - 1) {
	                    this.historyIndex++;
	                    this.command = this.commandHistory[this.historyIndex];
						console.log(this.commandHistory);
	                } else {
	                    this.historyIndex = this.commandHistory.length;
	                    this.command = '';
						console.log(this.commandHistory);
	                }
	            } else if (event.key === 'Enter') {
	                this.executeCommand();
	            }
	        },
			connect() {
			  if(this.socket == null || this.socket.readyState === WebSocket.CLOSED){
				this.socket = new WebSocket('/consoleSocket?id='+this.serverId+'&token='+this.token);
			  }

			  this.socket.onopen = () => {
			    console.log('Connecting to server ID = '+this.serverId);
				setTimeout(() => {
				    this.socket.send(this.serverId);
					console.log("sended " + this.serverId);
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
			showSSHData(){
				alert("IP для SSH: " + this.netData.host + ":" + this.netData.ssh_port + "\n"
					+ "пользователь: " + this.netData.user + "\n"
					+ "пароль: " + this.netData.password);
			},
	    }
	});
});

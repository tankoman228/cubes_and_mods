document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			serverId: SRVId,
			running: false,
	        command: "",
	        commandHistory: [],
			messages: [],
	        historyIndex: -1,
			socket: null,
	    },
	    methods: {
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
						console.log("Гуд");
					})
					.catch(error => {
						console.log("АААААА ошибка 0000X00000000000000000");
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

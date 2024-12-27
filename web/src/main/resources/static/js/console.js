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
		mounted() {
		  window.addEventListener('beforeunload', this.yourMethod);
		},
		beforeDestroy() {
		  window.removeEventListener('beforeunload', executeCommandSilent('CloseSession'));
		},
	    methods: {
	        addToConsole(text) {
				if (this.messages.length >= 250) {
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
	            if (this.command !== '' && this.command != 'CloseSession') {
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
						alert("Сервер запущен");
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
	            alert("Сервер сохранен");
	        },
			reloadServer() {
				this.executeCommandSilent("reload");
			    alert("Сервер остановлен");
			},
	        stopServer() {
				this.executeCommandSilent("stop");
				this.running = false;
	            alert("Сервер остановлен");
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
			  if(this.socket == null)
			  this.socket = new WebSocket('ws://localhost:8083/console');
			  
			  this.socket.onopen = () => {
			    console.log('Connecting...');
			    this.socket.send(this.serverId);
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
	
			  this.socket.onerror = (error) => {
				this.running = false;
			    console.error('Connection error:', error);
				alert('Ошибка соединения:', error);
			  };
			},
	    }
	});
});

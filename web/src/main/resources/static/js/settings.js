document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			UserId: UserID,
			SrvId: SrvID,
			tarifId: TariffId,
			Settings: [],
			search: "",
			order:{
				clientOrder: 
				{
					id: UserId
				},
				tariffOrder: 
				{
					id: TariffId,
				},
				serverOrder:{
					id: -1
				},
				hostOrder:{
					name: "",
					description: "",
					id_client: -1,
					id_tariff: -1,
					id_server: -1,
				}
			},
	    },
	    created() {
			this.isAlive();
			this.getFile();
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
					});
			},
			getFile(){
				console.log(this.filePath);
				axios.post('/files/getText?id_server=' + this.SrvId + "&path=/server.properties")
					.then(response =>{
						const text = response.data.text.split('\n');
						settings = text.map(item => {
						  if (item.trim().startsWith('#') || item == "") {
						    return null;
						  }
						  const [setting, data] = item.split('=');
						  return {
						    name: setting.trim(),
						    data: data ? data.trim() : '' 
						  };
						}).filter(item => item !== null);
						this.Settings = this.where(settings, this.search);
					})
					.catch(error =>{
						alert(error);
					});
			},
			where(data, search){
				if(search != ""){
					return data.filter(version => version.name.includes(search));
				}
				return data;
			},
			save(){

				result = confirm("Сохранить настройки?");
				if(result == false) return;
				
				file = {
					path: "server.properties",
					contents: [],
					children: null,
					isDirectory: false,
					size: -1,
				}

				const encoder = new TextEncoder();
				str = this.Settings.map(item => item.name + '=' + item.data).join('\n') + '\n';
				
				const byteArray = encoder.encode(str);

				file.contents = Array.from(byteArray);
				file.size = Array.from(byteArray).length;

				console.log("Файл сформирован");
				console.log("SrvId = " + this.SrvId);
				console.log(file.path);
				axios.post('/files/upload?id_server=' + this.SrvId, file)
					.then(response =>{
						alert("Настройки сохранены!")
					})
					.catch(error =>{
						alert(error);
					});
			},
			prolong(){
				result = confirm("Продлить тариф?");
				if(result == false) return;
				axios.get('/mcserver/my?id='+this.UserId)
				    .then(response => {
				        servers = response.data;
						console.log(servers);
						server = this.findServerByID(servers, this.SrvId);
						console.log(server);
						//TODO: требуются правки продления и смены тарифа под новый Order
						axios.post('/pay/request', this.order)
						.then(response => {
							key = response.data;
							window.location.href = "/payOrder?tariffId=" + tariff.id + "&machineId=" + this.order.mineserver.id_machine + "&key=" + key;
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
			change(){
				result = confirm("Сменить тариф?");
				if(result == false) return;
				axios.post('/files/delete?id_server=' + this.SrvId + "&path=" + "/user_jvm_args.txt")
					.then(response =>{
						window.location.href = "/changeTariff?ServerId=" + this.SrvId;
					})
					.catch(error =>{
						alert(error);
					});
			},
			findServerByID(data, search){
				data = data.filter(srv => srv.id == search);
				return data[0];
			},
			share(){
				var email = prompt("Введите email пользователя, c которым хотите поделиться сервером");
				if(email == null) return;
				console.log(email);
				console.log(this.SrvId);
				axios.post('/mcserver/my/share?id=' + this.SrvId + "&email=" + email)
					.then(response =>{
						alert("Вы успешно поделились сервером с пользователем " + email);
					})
					.catch(error =>{
						alert(error);
						console.error(error);
					});
			},
			edit(){
				window.location.href = "/edit?ServerId=" + this.SrvId;
			},
	    }
	});
});
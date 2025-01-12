document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			filesData: null,
			selectedFile: null,
			filePath: "",
			isContextMenuVisible: false,
			contextMenuX: 0,
			contextMenuY: 0,
	    },
	    created() {
			this.getAllFiles();
	    },
		mounted() {
		  window.addEventListener('click', this.hideContextMenu); // Скрываем меню при клике вне
		},
		beforeDestroy() {
		  window.removeEventListener('click', this.hideContextMenu); // Убираем слушатель при уничтожении компонента
		},
	    methods: {
			getAllFiles(){
				axios.post('/files/all?id_server=' + this.SrvId)
					.then(response =>{
						this.filesData = response.data;
						console.log(this.files);
					})
					.catch(error =>{
						alert(error);
					});
			},
			getFile(){
				console.log(this.filePath);
				axios.post('/files/byPath?id_server=' + this.SrvId + "&path=" + this.filePath)
					.then(response =>{
						this.filesData = response.data;
					})
					.catch(error =>{
						alert(error);
					});
			},
			goBack(){
				const lastSlashIndex = this.filePath.lastIndexOf('/');

				if (lastSlashIndex > 0) {
				  this.filePath = this.filePath.substring(0, lastSlashIndex);
				  this.getFile();
				} else {
				  this.filePath = '';
				  this.getAllFiles();
				}
			},
			handleClick(file) {
				console.log('Left click on:', file.name);
				this.selectedFile = file;
			},
			handleRightClick(file) {
				console.log('Right click on:', file.name);
				this.selectedFile = file;
				
				this.contextMenuX = event.clientX;
				this.contextMenuY = event.clientY * 1.3;
				
				this.isContextMenuVisible = true;
				//dialog.show();
			},
			hideContextMenu() {
			  this.isContextMenuVisible = false;
			  //dialog.close();
			},
			handleDoubleClick(file) {
  				console.log('Double click on:', file.name);
				if(file.isDirectory != true){
					alert("Редактирование файлов пока недоступно");
					return;
				}
				this.filePath += "/" + file.name;
				this.getFile();
			},
			deleteFile(file){
				result = confirm("Удалить файл " + file.name + "?");
				if(result == false) return;
				axios.post('/files/delete?id_server=' + this.SrvId + "&path=" + this.filePath+"/"+file.name)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getFile();
						}
						alert("Успешно");
					})
					.catch(error =>{
						alert(error);
					});
			},
			upload(){
				newFile = {
					isDirectory: false,
					files: [],
					contents_bytes: [],
					name: "",
				}
				alert("Нет");
			},
	    }
	});
});
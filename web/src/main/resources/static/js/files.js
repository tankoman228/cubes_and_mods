document.addEventListener('DOMContentLoaded', function() {
	new Vue({
	    el: '#app',
	    data: {
			SrvId: SrvID,
			filesData: [],
			selectedFile: 
			{
				name: "",
				contents_bytes: [],
				files: null,
				isDirectory: false,
			},
			filePath: "",
			isContextMenuVisible: false,
			isUlContextMenuVisible: false,
			contextMenuX: 0,
			contextMenuY: 0,
			parts: [],
			searchTerm: "",
	    },
	    created() {
			this.getAllFiles();
	    },
		mounted() {
		  window.addEventListener('click', this.hideContextMenu);
		},
		beforeDestroy() {
		  window.removeEventListener('click', this.hideContextMenu);
		},
	    methods: {
			getAllFiles(){
				axios.post('/files/all?id_server=' + this.SrvId)
					.then(response =>{
						Data = response.data;
						//console.log(Data.files);
						if(Data.files == null || Data.files.length === 0){
							this.filesData = [];
							return;
						};	
						Data.files = Data.files
								.filter(item => item.name.toLowerCase()
								.includes(this.searchTerm.toLowerCase()));
						this.filesData = Data;
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
						Data = response.data;
						//console.log(Data.files);
						if(Data.files == null || Data.files.length === 0){
							this.filesData = [];
							return;
						};
						Data.files = Data.files
								.filter(item => item.name.toLowerCase()
								.includes(this.searchTerm.toLowerCase()));
						this.filesData = Data;
						console.log(this.filesData);
					})
					.catch(error =>{
						alert(error);
					});
			},
			search(){
				const lastSlashIndex = this.filePath.lastIndexOf('/')
				if (lastSlashIndex > 0) {
				  //this.filePath = this.filePath.substring(0, lastSlashIndex);
				  this.getFile();
				} else {
				  this.filePath = '';
				  this.getAllFiles();
				}
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
			downloadFile(file){
					const fileUrl = '/files/download?id_server=' + this.SrvId + "&path=" + this.filePath + "/" + file.name;
					window.open(fileUrl, '_blank');
			},
			handleClick(file) {
				console.log('Left click on:', file.name);
				this.selectedFile = file;
			},
			handleRightClick(file, event) {
				event.stopPropagation();
				console.log('Right click on:', file.name)
				console.log("Нажатие на li");
				if(this.isContextMenuVisible == true || this.isUlContextMenuVisible == true) return;
				if(file == null) return;
				this.selectedFile = file;

				this.contextMenuX = event.clientX;
				this.contextMenuY = event.clientY * 1.3;
				this.isContextMenuVisible = true
				console.log(this.isContextMenuVisible);
			},
			handleUlRightClick(event){
				if(this.isContextMenuVisible == true || this.isUlContextMenuVisible == true) return;
				this.contextMenuX = event.clientX;
				this.contextMenuY = event.clientY * 1.3;
				console.log("Нажатие на ul");
				this.isUlContextMenuVisible = true;
			},
			hideContextMenu() {
				this.isContextMenuVisible = false;
				this.isUlContextMenuVisible = false;
			},
			handleDoubleClick(file) {
  				console.log('Double click on:', file.name);
				if(file.name === 'user_jvm_args.txt' || file.name === 'eula.txt' || file.name === 'run.sh'){
					alert("Этот файл нельзя редактировать!");
					return;
				}
				if(file.isDirectory == false){
					filename = file.name;
					fileExtension = filename.split('.').pop().toLowerCase();
						
					if(fileExtension === "txt" || fileExtension === "json" || fileExtension === "properties"){

					fileUrl = '/textReader?ServerId=' + this.SrvId + "&path=" + this.filePath + "/" + file.name;
					window.open(fileUrl, '_blank');
					}
					else{
						alert("Этот тип файла нельзя редактировать!");
						return;
					}
				}
				else
				{
					console.log(fileExtension);
					this.filePath += "/" + file.name;
					this.getFile()
				}
			},
			deleteFile(file){
				if(file.name === 'user_jvm_args.txt' || file.name === 'eula.txt' || file.name === 'run.sh'){
					alert("Этот файл нельзя редактировать!");
					return;
				}
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
			makeDir(){
				dirName = prompt("Создать папку?");
				if(dirName == null) return;
				
				if(dirName === 'user_jvm_args.txt' || dirName === 'eula.txt' || dirName === 'run.sh'){
					alert("Этот файл нельзя редактировать!");
					return;
				}
				
				console.log(dirName);
				
				this.parts = this.filePath.split('/').filter(part => part);
					
				//this.parts.push(dirName);
				newDir = {
					isDirectory: true,
					files: [],
					contents_bytes: [],
					name: dirName,
				}
				dir = this.makeDirRec(newDir);
				
				axios.post('/files/upload?id_server=' + this.SrvId, dir)
					.then(response =>{
						if(this.filePath == ""){
							this.getAllFiles();
						}
						else{
							this.getFile();
						}
					})
					.catch(error =>{
						alert(error);
					});
			},
			makeDirRec(file){
				if(this.parts.length <= 0) return file;
				
				currentName = this.parts.shift();
				dir = {
					isDirectory: true,
					files: [],
					contents_bytes: [],
					name: currentName,
				}
				
				if(this.parts.length > 0) dir.files.push(makeDirRec(file));
				else dir.files = file;
				
				return dir;
			},
			
			handleFileUpload(event) {
			  const files = event.target.files; // Получаем список выбранных файлов
			  
			  if (files.length > 0) {
			    // Превращаем FileList в массив для удобной обработки
			    const fileArray = Array.from(files);
				
			    fileArray.forEach(file => {
			      const reader = new FileReader();

			      // Получаем имя файла
			      const fileName = file.name;
			      console.log('Имя файла:', fileName); // Выводим имя файла в консоль
				  
				  if(file.name === 'user_jvm_args.txt' || file.name === 'eula.txt' || file.name === 'run.sh'){
				  	alert("Этот файл нельзя редактировать!");
					return;
				  }
				  
			      // Читаем файл как ArrayBuffer
			      reader.readAsArrayBuffer(file);

			      reader.onload = () => {
			        const arrayBuffer = reader.result; // Получаем ArrayBuffer
			        const byteArray = new Uint8Array(arrayBuffer); // Преобразуем в массив байтов

			        console.log('Массив байтов для', fileName, ':', byteArray); // Здесь вы можете отправить массив на сервер
			        newFileInfo = {
						isDirectory: false,
						files: [],
						contents_bytes: Array.from(byteArray),
						name: fileName,
					};
					
					this.parts = this.filePath.split('/').filter(part => part);
					fs = this.makeDirRec(newFileInfo);

					axios.post('/files/upload?id_server=' + this.SrvId, fs)
						.then(response =>{
							if(this.filePath == ""){
								this.getAllFiles();
							}
							else{
								this.getFile();
							}
						})
						.catch(error =>{
							alert(error);
						});
			      };

			      reader.onerror = (error) => {
			        console.error('Ошибка чтения файла:', error);
			      };
			    });
			  } else {
			    console.log('Файлы не выбраны');
			  }
			},
	    }
	});
});
import config from "/config.js"; 

// Mounted to a huge Vue object in app.js

export let data = {
    mines: [],
	has_minechart: {},
    minecharts: {}
}

export let methods = {
    loadMineDiagram(m) {
        axios.get(`/api/mineservers/${m.mineserver.id}/stats`)
            .then(async response => {
                const stats = response.data; // Получаем статистику

                this.$set(this.has_minechart, m.mineserver.id, true); // Установка значения, чтобы отобразить canvas

                await this.$nextTick();
                this.renderChart(stats, m);
            })
            .catch(error => {
                console.error("Ошибка получения статистики:", error);
            });

    },
	
	// Отображение диаграммы с двумя осями. Не путать с renderChart_, я накосячил с названиями, да
    renderChart(stats, m) {

        let timestamps = stats.map(stat => new Date(stat.timestamp).toLocaleTimeString());
        let memoryUsed = stats.map(stat => stat.memory_used);
        let secondsWorking = stats.map(stat => stat.seconds_working);

		console.log("render mine:");
		console.log(m);
        let id = m.mineserver.id;

		// Если обновляем диаграмму
        if (this.minecharts[id]) {
            try { this.minecharts[id].destroy();}
            catch (error) { console.error(error);}
        }

		// Сам рендер диаграммы
        let canvasElement = document.getElementById('mineChart-' + m.mineserver.id);
        if (canvasElement) {
            
			let ctx = canvasElement.getContext('2d');
			this.minecharts[id] = new Chart(ctx, {
			    type: 'line',
			    data: {
			        labels: timestamps,
			        datasets: [
			            {
			                label: 'Использование ПЗУ (КБ)',
			                data: memoryUsed,
			                borderColor: 'rgba(0, 0, 255, 1)',
			                fill: false,
			                yAxisID: 'y1' // Указать, что этот датасет будет использовать ось y1
			            },
			            {
			                label: 'Время рантайма (с)',
			                data: secondsWorking,
			                borderColor: 'rgba(255, 0, 0, 1)',
			                fill: false,
			                yAxisID: 'y2' // Указать, что этот датасет будет использовать ось y2
			            }
			        ]
			    },
			    options: {
			        responsive: true,
			        scales: {
			            y1: { // Первая ось Y (для использования ПЗУ)
			                type: 'linear',
			                position: 'left',
			                beginAtZero: true,
			                title: {
			                    display: true,
			                    text: 'Использование ПЗУ (КБ)'
			                }
			            },
			            y2: { // Вторая ось Y (для времени рантайма)
			                type: 'linear',
			                position: 'right',
			                beginAtZero: true,
			                title: {
			                    display: true,
			                    text: 'Время рантайма (с)'
			                },
			                grid: {
			                    drawOnChartArea: false
			                }
			            },
			            x: {
			                title: {
			                    display: true,
			                    text: 'Время'
			                }
			            }
			        }
			    }
			});

        } else {
            console.error('Canvas element not found!');
        }
    },
	
	// Сложная последовательность запросов для удаления всей инфомрации об игровом сервере и освобождение ресурсов
    async deleteMineserver(m) {

        const confirmation = confirm("Вы уверены, что хотите удалить этот игровой сервер?");
        if (!confirmation) return;

        try {
			
            // Отправляем POST запрос на удаление сервера
            const deleteServerResponse = await fetch(`http://${m.machine.address}/delete_server/${m.mineserver.id}`, {
                method: 'POST'
            });
            if (!deleteServerResponse.ok) {
                alert('Ошибка при удалении файлов сервера'); return;
            }

            // Получаем все бекапы для данного mineserver
            const backupResponse = await fetch(`http://${m.machine.address}/backup/all/${m.mineserver.id}`, {
				method: 'POST'
			});
            const backups = await backupResponse.json();


            for (const backup of backups) {
                const deleteBackupResponse = await fetch(`http://${m.machine.address}/backup/delete/${m.mineserver.id}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(backup)
                });
                if (!deleteBackupResponse.ok) {
                    console.error('Ошибка при удалении бекапа:', backup);
                }
            }
			

			// Освобождаем ресурсы на физической машине
			const freeRequest = await fetch(`${config.res}/machines/free/${m.mineserver.id_machine}`, {
				method: 'POST',
				headers: {
				    'Content-Type': 'application/json'
				},
				body: JSON.stringify(m.tariff)
			});
			if (!freeRequest.ok) {
				alert('Ошибка при освобождении ресрсов ');
			}
			
            // Удаляем mineserver
            const finalDeleteResponse = await fetch(`${config.res}/mineservers/delete/${m.mineserver.id}`, {
                method: 'DELETE',
				body: null
            });
            if (!finalDeleteResponse.ok) {
				alert('Ошибка при окончательном удалении mineserver, что-то с базой');
                throw new Error('Ошибка при окончательном удалении mineserver');
            }

            alert('Сервер успешно удалён! Все файлы удалены');

            this.loadMines(); // Обновляем список серверов после удаления
			} catch (error) {
			    alert('Ошибка при удалении mineserver: ' + error.message + '\n' + error.stack);
			}
    },
	
    async loadMines() {
        try {
			
			// Получаем список сырых объектов
            const response = await fetch(`${config.res}/mineservers/all`, {
                method: 'POST'
            });
            if (!response.ok) {
                throw new Error('Ошибка при загрузке mineservers');
            }
            const mineservers = await response.json();

			// Созда view model для отображения в index.html
            this.mines = [];
            mineservers.forEach((mineserver) => {
				
				// Предполагается, что другие списки уже загружены, по ним строится view model
				let machine = this.machines.find(m => m.id === mineserver.id_machine);
				let tariff = this.tariffs.find(t => t.id === mineserver.id_tariff);
				let user = this.users_all.find(u => u.id === mineserver.id_user);		
				
				let runtime, disk;
				console.log("runtime and disk calculation");
				if (tariff) {
				    runtime = (mineserver.seconds_working / 3600 / tariff.hours_work_max * 100).toFixed(2) + "% = " + (mineserver.seconds_working / 3600).toFixed(2) + " ч.";
					disk = ((mineserver.memory_used / tariff.memory_limit) * 100).toPrecision(2) + " % = " + (mineserver.memory_used / 1024).toFixed() + " МБ";
				} else {
				    runtime = 'Error: нет данных для расчета';
					disk = 'Error: нет данных для расчета';
				}
				console.log("Привет, исходникик смотрим? Удачи)");
								
				// View Model полностью собран и отправляется на страничку
                this.mines.push({
				    mineserver: mineserver,
				    machine: machine,
				    tariff: tariff,
					user: user,
				    runtime: runtime,
				    disk: disk
				});
            });
            
        } catch (error) {
            console.error('Ошибка при загрузке mineservers:', error);
        }
    }
}

export async function mounted() {
	
    // Перед загрузкой списка надо подождать, чтобы загрузился список machines и tariffs
    await new Promise(r => setTimeout(r, 1400));
	
    await this.loadMines(); 
}


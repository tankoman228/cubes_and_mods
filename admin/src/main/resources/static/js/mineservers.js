console.log('mineservers import');

export let data = {
    mines: [],
	has_minechart: {},
    minecharts: {}
}

export let methods = {
    loadMineDiagram(m) {
        axios.get(`/api/mineservers/${m.mineserver.id}/stats`)
            .then(async response => {
                const stats = response.data; // Получаем данные статистики

                this.$set(this.has_minechart, m.mineserver.id, true); // Установка значения, чтобы отобразить canvas

                // Дожидаемся обновления DOM
                await this.$nextTick();

                // Рендерим график с полученными данными
                this.renderChart(stats, m);
            })
            .catch(error => {
                console.error("Ошибка получения статистики:", error);
            });

    },
    renderChart(stats, m) {

        let timestamps = stats.map(stat => new Date(stat.timestamp).toLocaleTimeString());
        let memoryUsed = stats.map(stat => stat.memory_used);
        let secondsWorking = stats.map(stat => stat.seconds_working);

		console.log("render mine:");
		console.log(m);
        let id = m.mineserver.id;

        if (this.minecharts[id]) {
            try { this.minecharts[id].destroy();}
            catch (error) { console.error(error);}
        }

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
			                    drawOnChartArea: false // Не отображать сетку для правой оси
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
    async deleteMineserver(m) {
        // Запросить подтверждение
        const confirmation = confirm("Вы уверены, что хотите удалить этот игровой сервер?");
        if (!confirmation) return;

        try {
            // Отправляем POST запрос на удаление сервера
            const deleteServerResponse = await fetch(`http://${m.machine.address}/delete_server/${m.mineserver.id}`, {
                method: 'POST'
            });
            if (!deleteServerResponse.ok) {
                alert('Ошибка при удалении сервера'); return;
            }

            // Получаем все бекапы для данного mineserver
            const backupResponse = await fetch(`http://${m.machine.address}/backup/all/${m.mineserver.id}`, {
				method: 'POST'
			});
            const backups = await backupResponse.json();

            // Удаляем каждый бекап
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

            // Удаляем mineserver
            const finalDeleteResponse = await fetch(`http://localhost:8084/mineservers/delete/${m.mineserver.id}`, {
                method: 'DELETE'
            });
            if (!finalDeleteResponse.ok) {
                throw new Error('Ошибка при окончательном удалении mineserver');
            }

            alert('Сервер успешно удалён! Все файлы удалены');

            this.loadMines(); // Обновляем список серверов после удаления
        } catch (error) {
            alert('Ошибка при удалении mineserver:', error);
        }
    },
    async loadMines() {
        try {
            const response = await fetch('http://localhost:8084/mineservers/all', {
                method: 'POST'
            });
            if (!response.ok) {
                throw new Error('Ошибка при загрузке mineservers');
            }

            const mineservers = await response.json();

            this.mines = [];
            mineservers.forEach((mineserver) => {
				
				let machine = this.machines.find(m => m.id === mineserver.id_machine);
				let tariff = this.tariffs.find(t => t.id === mineserver.id_tariff);
				let user = this.users_all.find(u => u.id === mineserver.id_user);
				
				console.log(user);
				
				//alert(machine.name);
				//alert(tariff.name);
				
                this.mines.push({
				    mineserver: mineserver,
				    machine: machine,
				    tariff: tariff,
					user: user,
				    runtime: (mineserver.seconds_working / 3600 / tariff.hours_work_max * 100).toFixed(2) + "% = " + mineserver.seconds_working / 3600 + " ч. ",
				    disk: ((mineserver.memory_used / tariff.memory_limit) * 100).toPrecision(2) + " % = " + (mineserver.memory_used / 1024).toFixed() + " МБ"
				});
            });
            
        } catch (error) {
            console.error('Ошибка при загрузке mineservers:', error);
        }
    }
}

export async function mounted() {
    // Перед загрузкой списка надо подождать, чтобы загрузился список machines и tariffs
    await new Promise(r => setTimeout(r, 2000));
    await this.loadMines(); // Загружаем список mineservers
}

